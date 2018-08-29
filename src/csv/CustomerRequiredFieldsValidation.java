package csv;

import java.util.*;
import java.util.stream.Collectors;

import csv.FileProcessorUtil.CsvFields;

public class CustomerRequiredFieldsValidation implements ValidationRule {

	@FunctionalInterface
	interface BuildValuesMapInt {
		Map<String, Object> get();
	}

	private final CustomerDataRecord record;
	private final UserFileDataMetrics metrics;

	public CustomerRequiredFieldsValidation(CustomerDataRecord record,
			UserFileDataMetrics metrics) {
		this.record = record;
		this.metrics = metrics;
	}

	/**
	 * Build a map contain must have value column names
	 *
	 * @return
	 */
	protected Map<String, Object> buildValuesMap() {
		Map<String, Object> fieldValuesMap = new HashMap<>();
		if (record.getType() == 0) {
			fieldValuesMap.put(CsvFields.FIRST_NAME, record.getFirstName());
			fieldValuesMap.put(CsvFields.LAST_NAME, record.getLastName());
		} else {
			fieldValuesMap.put(CsvFields.COMPANY_NAME, record.getCompanyName());
		}
		return fieldValuesMap;
	}

	/**
	 * Do validation
	 *
	 * @return Boolean. Return false if missing column UNITS or missing [column
	 *         COUNTRY and LAT/LNG columns]. True for other cases.
	 */
	@Override
	public Boolean validationSuccessful() {
		Set<String> missingFields = getMissingRequiredFields();
		if (!missingFields.isEmpty()) {
			int rowNumber = CustomerDataHelper.getRecordRowNumberAndId(
					record.getId()).getRowNumber();
			final Set<String> columnsWithInvalidData = metrics
					.getRecords()
					.getOrDefault(
							MetricsContext.MetricsRecordType.VALIDATION_FAILURE,
							Collections.emptyList())
					.stream()
					.filter(rowRecord -> rowRecord.getRowNumber() == rowNumber)
					.map(rowRecord -> String.valueOf(rowRecord.getColumnName())
							.toUpperCase()).collect(Collectors.toSet());
			missingFields
					.stream()
					.filter(column -> !columnsWithInvalidData.contains(column
							.toUpperCase()))
					.forEach(
							columnName -> metrics.addMissingValueErrorMessage(
									this.getClass().getSimpleName(),
									record.getId(), rowNumber, columnName));
			return !(record.getType() == 0 && missingFields
					.contains(CsvFields.FIRST_NAME))
					|| (record.getType() == 1 && missingFields
							.contains(CsvFields.COMPANY_NAME));
		}
		return true;
	}

	/**
	 * Get missing required fields
	 *
	 * @return
	 */
	protected Set<String> getMissingRequiredFields() {
		return getMissingFields(this::buildValuesMap);
	}

	/**
	 * Collect column names of columns that have null values
	 *
	 * @param valueMapFunction
	 * @return Set string of column names
	 */
	protected Set<String> getMissingFields(BuildValuesMapInt valueMapFunction) {
		Set<String> missingFields = new HashSet<>();
		Map<String, Object> fieldValuesMap = valueMapFunction.get();
		fieldValuesMap.forEach((k, v) -> {
			if (v == null) {
				missingFields.add(k);
			}
		});
		return missingFields;
	}
}
