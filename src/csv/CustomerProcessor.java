package csv;

import csv.FileProcessorUtil.CsvFields;

import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import com.opencsv.bean.MappingStrategy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.Getter;
import lombok.Setter;

public class CustomerProcessor extends AbstractProcessor<CustomerDataRecord> {

	private static final Logger LOG = LogManager
			.getLogger(CustomerProcessor.class);
	private ConcurrentHashMap<String, Integer> customerDataRecordId = new ConcurrentHashMap();
	private List<CustomerDataRecord> genericDataRecords = Collections
			.synchronizedList(Lists.newArrayList());
	@Setter
	@Getter
	private Integer customerType;

	public CustomerProcessor(Integer customerType) {
		this.customerType = customerType;
	}

	@Override
	protected Map<String, String> getColumnMappings() {
		System.out.println(customerType);
		Map<String, String> columnMap = new HashMap<>();
		if (customerType.compareTo(Integer.valueOf(0)) == 0) {
			columnMap.put(CsvFields.FIRST_NAME,
					FileProcessorUtil.OtherFields.FIRST_NAME);
			columnMap.put(CsvFields.LAST_NAME,
					FileProcessorUtil.OtherFields.LAST_NAME);
			columnMap.put(CsvFields.CUSTOMER_NUMBER,
					FileProcessorUtil.OtherFields.CUSTOMER_NUMBER);
		} else {
			columnMap.put(CsvFields.COMPANY_NAME,
					FileProcessorUtil.OtherFields.COMPANY_NAME);
		}
		columnMap.put(CsvFields.SALUTATION,
				FileProcessorUtil.OtherFields.SALUTATION);
		columnMap.put(CsvFields.EMAIL, FileProcessorUtil.OtherFields.EMAIL);
		columnMap.put(CsvFields.PRIMARY_PHONE,
				FileProcessorUtil.OtherFields.PRIMARY_PHONE);

		columnMap.put(CsvFields.NOTES, FileProcessorUtil.DtoFields.NOTES);
		columnMap.put(CsvFields.ADDRESS_STREET,
				FileProcessorUtil.DtoFields.ADDRESS_STREET);
		columnMap.put(CsvFields.ADDRESS_COUNTRY_CODE,
				FileProcessorUtil.DtoFields.ADDRESS_POSTCODE);
		columnMap.put(CsvFields.ADDRESS_CITY,
				FileProcessorUtil.DtoFields.ADDRESS_CITY);
		columnMap.put(CsvFields.ADDRESS_COUNTRY_CODE,
				FileProcessorUtil.DtoFields.ADDRESS_COUNTRY_CODE);
		return columnMap;
	}

	@Override
	public MappingStrategy getMappingStrategy() {
		return getMappingStrategy(CustomerDataRecord.class);
	}

	public <T> Stream<T> toStream(final Iterator<T> iterator,
			final boolean parallel) {
		Iterable<T> iterable = () -> iterator;
		return StreamSupport.stream(iterable.spliterator(), parallel);
	}

	@Override
	protected Boolean init(FilesRecord record,
			MappingStrategy<CustomerDataRecord> mappingStrategy) {
		return super.init(record, mappingStrategy);
	}

	/**
	 * Main function Process rows and do aggregation
	 *
	 * @param filesRecord
	 *            the file record from HBase that needs processing
	 */
	@Override
	public void processRecords(FilesRecord filesRecord) {
		long startTime = System.nanoTime();
		// Start processing
		UserFileDataMetrics metrics = new UserFileDataMetrics();
		Iterator<CustomerDataRecord> dataIterator = getDataIterator();
		AtomicInteger rowCount = new AtomicInteger(0);
		String privateCustomerHeader = "first_name,last_name,salutation,email,primary_phone,customer_number,notes,address_street,address_postcode,address_city,address_country_code";
		toStream(dataIterator, false)
				.map(record -> CustomerDataHelper.buildGenericDataRecord(
						record, filesRecord, rowCount.incrementAndGet()))
				.parallel()
				.forEach(nr -> {
					metrics.addToTotalRecords(1);
					// Add parse errors
						Set<String> parseErrors = fileParser.getParseErrors(nr
								.getRowNum());
						StringBuilder redundantColumn = new StringBuilder();
						try {
							CSVReader csvReader = fileParser.getCsvReader();
							Iterator<String[]> iter = csvReader.iterator();
							while (iter.hasNext()) {
								String[] nextLines = iter.next();
								for (String string : nextLines) {
									if (!privateCustomerHeader.contains(string)) {
										redundantColumn.append(string).append(
												",");
									}
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
						System.out.println("redundantColumn==="+redundantColumn);
						for (String error : parseErrors) {
							metrics.addInvalidValueErrorMessage(
									"InvalidValueValidation", nr.getRecord()
											.getId(), nr.getRowNum(), error);
						}
						boolean duplicateRecord = isDuplicate(nr, metrics);
						if (!duplicateRecord) {
							processRecord(nr.getRecord(), metrics);
							// writeToHbase(rowCount);
						}
					});
		// for remaining records (< batch size = 2000)
		// GenericDataHelper.addOrUpdateGenericDataRecords(genericDataRecords);
		System.out.println("Processing result for file " + filesRecord.getId()
				+ ": \n" + metrics.toLogOutput());
		try {
			// runGenericAggregationJob(aggregator, filesRecord.getId());
		} catch (Exception e) {
			System.out.println("Error aggregating generic data records" + e);
			System.exit(-1);
		}

		// Update status file after running aggregation successful
		try {
			String uploadSummary = new ObjectMapper()
					.writeValueAsString(metrics.createUserFileDataReport());
			System.out.println("Upload Summary: " + uploadSummary);
			filesRecord.setUploadSummary(uploadSummary);
			// if (!fileDataHelper.updateFilesRecord(filesRecord)) {
			// LOG.error("Failed to update upload summary for file record with fileID: "
			// + filesRecord.getId());
			// closeFileParserAndExit();
			// }
			long endTime = System.nanoTime();
			System.out.println("Finished all processing for file: "
					+ filesRecord.getId()
					+ " in "
					+ TimeUnit.SECONDS.convert(endTime - startTime,
							TimeUnit.NANOSECONDS) + " seconds ");
		} catch (IOException e) {
			LOG.error("Unable to create generic data report upload summary", e);
			closeFileParserAndExit();
		}
		fileParser.close();
	}

	private void processRecord(CustomerDataRecord record,
			UserFileDataMetrics metrics) {
		boolean validated = validateRecord(record, metrics);

		// if (validated && processingOptions.getGeocodingEnabled()) {
		// geocodeRecord(record, metrics, aggregator);
		// }
	}

	private void aggregateRecord(CustomerDataRecord record) {
		// aggregator.add(record);
	}

	private boolean validateRecord(CustomerDataRecord record,
			UserFileDataMetrics metrics) {
		Boolean validated = applyValidationRules(record, metrics);
		if (validated) {
			metrics.addToSuccessfulValidatedRecords(1);
		} else {
			metrics.addToFailedValidationRecords(1);
		}
		return validated;
	}

	private Boolean applyValidationRules(CustomerDataRecord record,
			UserFileDataMetrics metrics) {
		Boolean requiredFieldsValidation = new CustomerRequiredFieldsValidation(
				record, metrics).validationSuccessful();
		Boolean specialValidation = new CustomerDataRowsValidation(record,
				metrics).validationSuccessful();

		return specialValidation && requiredFieldsValidation;
	}

	private boolean isDuplicate(NumberedDataRecord<CustomerDataRecord> record,
			UserFileDataMetrics metrics) {
		// Check for duplicate records
		Integer dupeIndex = CustomerDataHelper.addIdWithIndex(
				customerDataRecordId, record);
		if (dupeIndex != null) {
			Integer rowNumber = CustomerDataHelper.getRecordRowNumberAndId(
					record.getRecord().getId()).getRowNumber();
			metrics.addDuplicateRecordsErrorMessage("DuplicateRowsValidation",
					record.getRecord().getId(), rowNumber, dupeIndex);
			metrics.addToDuplicateRecords(1);
			return true;
		}
		return false;
	}

	/**
	 * Write data in batch size.
	 *
	 * @param rowCount
	 */
	// private void writeToHbase(AtomicInteger rowCount) {
	// synchronized(genericDataRecords) {
	// if (genericDataRecords.size() ==
	// UploadProcessingConfig.getGenericProcessorBatchSize()) {
	// LOG.info("Writing to Hbase from index " + (rowCount.get() -
	// UploadProcessingConfig.getGenericProcessorBatchSize()) +
	// ". Number of records to write: " +
	// UploadProcessingConfig.getGenericProcessorBatchSize());
	// GenericDataHelper.addOrUpdateGenericDataRecords(genericDataRecords);
	// genericDataRecords.clear();
	// LOG.info("Finished writing to Hbase");
	// }
	// }
	// }

}