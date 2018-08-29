package csv;

import org.apache.commons.lang3.StringUtils;

import com.neovisionaries.i18n.CountryCode;

public class CustomerDataRowsValidation implements ValidationRule {

	private final CustomerDataRecord record;
	private final UserFileDataMetrics metrics;

	public CustomerDataRowsValidation(CustomerDataRecord record,
			UserFileDataMetrics metrics) {
		this.metrics = metrics;
		this.record = record;
	}

	/**
	 * Check correct unit values
	 *
	 * @return Boolean - Units >= 0 or not
	 */
	@Override
	public Boolean validationSuccessful() {
		Boolean result = true;
		if (!(record.getSalutation() == 0 || record.getSalutation() == 1)) {
			int rowCount = CustomerDataHelper.getRecordRowNumberAndId(
					record.getId()).getRowNumber();
			metrics.addInvalidValueErrorMessage(
					this.getClass().getSimpleName(), record.getId(), rowCount,
					"Skipped: Unknown salutation code provided.");
			result = false;
		}
		if (StringUtils.isNotEmpty(record.getCountry())
				&& CountryCode.getByCode(record.getCountry()) == null) {
			int rowCount = CustomerDataHelper.getRecordRowNumberAndId(
					record.getId()).getRowNumber();
			metrics.addInvalidValueErrorMessage(
					this.getClass().getSimpleName(), record.getId(), rowCount,
					"Skipped: Unknown country code provided");
			result = false;
		}
		return result;
	}
}
