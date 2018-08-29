package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import csv.FileProcessorUtil.CsvFields;

public class FileUploadProcessingMain {
	private static Logger LOG;

	static {
		initLogging();
	}

	@Setter
	private static FileDataHelper filesHelper = new FileDataHelper();

	private static void initLogging() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		AbstractConfiguration config = (AbstractConfiguration) ctx
				.getConfiguration();
		config.getRootLogger().start();
		ctx.updateLoggers();

		LOG = LogManager.getLogger(FileUploadProcessingMain.class);
	}

	public static final String A_WeeklyFinalDatFilePath = "D:\\CS_Customer_Import.csv";// D_LifeLossPopFinalDat-Test.csv

	public static void main(String[] args) throws Exception {
		// ProcessCommand cmd = ProcessCli.parse(args);
		// if (cmd == null) {
		// ProcessCli.printHelp();
		// System.exit(1);
		// }
		// FilesRecord record = filesHelper.retrieveFileRecord(cmd.getFileId());
		// LOG.info("Got record from database: ");
		FilesRecord record = new FilesRecord();
		record.setFileDataType(FileDataType.CUSTOMER);
		record.setId("1");
		FileUploadProcessor.processFile(record);
		System.exit(0);
		// test();
	}

	public static String toString(List<String> ls) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < ls.size(); i++) {
			strBuilder.append(ls.get(i));
			if (i < (ls.size() - 1)) {
				strBuilder.append(',');
			}
		}
		return strBuilder.toString();
	}

	public static String toString(String[] strArr) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < strArr.length; i++) {
			strBuilder.append(strArr[i]);
			if (i < (strArr.length - 1)) {
				strBuilder.append(',');
			}
		}
		return strBuilder.toString();
	}

	protected static HeaderColumnNameMappingStrategy getMappingStrategy(
			Class recordFileClass) {
		HeaderColumnNameTranslateMappingStrategy strategy = new HeaderColumnNameTranslateMappingStrategy<>();
		strategy.setType(recordFileClass);
		strategy.setColumnMapping(getColumnMappings());
		return strategy;
	}

	protected static Map<String, String> getColumnMappings() {
		Map<String, String> columnMap = new HashMap<>();
		columnMap.put(CsvFields.FIRST_NAME,
				FileProcessorUtil.OtherFields.FIRST_NAME);
		columnMap.put(CsvFields.LAST_NAME,
				FileProcessorUtil.OtherFields.LAST_NAME);
		columnMap.put(CsvFields.SALUTATION,
				FileProcessorUtil.OtherFields.SALUTATION);
		columnMap.put(CsvFields.EMAIL, FileProcessorUtil.OtherFields.EMAIL);
		columnMap.put(CsvFields.PRIMARY_PHONE,
				FileProcessorUtil.OtherFields.PRIMARY_PHONE);
		columnMap.put(CsvFields.CUSTOMER_NUMBER,
				FileProcessorUtil.OtherFields.CUSTOMER_NUMBER);
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

	private static void test() {
		try (BOMInputStream reader = new BOMInputStream(
				FileUtils
						.openInputStream(new File("D:\\CS_Customer_Import.csv")))) {
			// Read and check header line
			System.out.println(reader.hasBOM());
			InputStreamReader readers = new InputStreamReader(reader, "utf-8");
			// String content = new Scanner(readers).useDelimiter("\\Z").next();

			CSVReaderBuilder csvBuilder = new CSVReaderBuilder(readers);
			CSVReader csvReader = csvBuilder.withFieldAsNull(
					CSVReaderNullFieldIndicator.BOTH).build();
			DataParserCsvToBeanFilter filter = new DataParserCsvToBeanFilter();
			DataParserIterableCsvToBean csvToBean = new DataParserIterableCsvToBean(
					csvReader, getMappingStrategy(CustomerDataRecord.class),
					filter);
			Optional<Iterator<CustomerDataRecord>> dataIterator = Optional
					.of(csvToBean.iterator());
			// return csvToBean.iterator();
			// System.out.println(dataIterator.get().hasNext());
			while (dataIterator.get().hasNext()) {
				System.out.println(dataIterator.get());
				CustomerDataRecord iter = dataIterator.get().next();
				System.out.println(iter.toString());
			}
			reader.close();
			// System.out.println("description: "
			// + new SyndFeedInput().build(reader).getDescription());
			// String[] nextLine = reader.hasBOM()
			// System.out.println(toString(nextLine));
			//
			// // Read CSV line by line and use the string array as you want
			// List<String> tranformedList = new ArrayList<>();
			// while ((nextLine = reader.readNext()) != null) {
			// if (nextLine != null) {
			//
			// List<String> l = new ArrayList(Arrays.asList(nextLine));
			//
			//
			// // Verifying the read data here
			// System.out.println(l.toString());
			//
			// tranformedList.add(toString(l));
			// }
			// }

			// write to file
			// writeFile(tranformedList,
			// NEW_A_WEEKLYFINALDAT_CSV_HEADER,A_WeeklyFinalDatFilePath +
			// "-tranformed" + CSV_EXTENSION);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
