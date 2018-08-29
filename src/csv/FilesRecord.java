package csv;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by pklkhoa on 8/29/2018.
 */
@Data
public class FilesRecord {
	public static final Character ROW_KEY_SEPARATOR = Character.valueOf('|');
	private String id;
	private Long uploadedTs;
	private Long lastModifiedTs;
	private FileStatus status;
	private String fileName;
	private Long effectiveTs;
	private FileDataType fileDataType;
	private String uploadedBy;
	private String uploadSummary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUploadedTs() {
		return uploadedTs;
	}

	public void setUploadedTs(Long uploadedTs) {
		this.uploadedTs = uploadedTs;
	}

	public Long getLastModifiedTs() {
		return lastModifiedTs;
	}

	public void setLastModifiedTs(Long lastModifiedTs) {
		this.lastModifiedTs = lastModifiedTs;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getEffectiveTs() {
		return effectiveTs;
	}

	public void setEffectiveTs(Long effectiveTs) {
		this.effectiveTs = effectiveTs;
	}

	public FileDataType getFileDataType() {
		return fileDataType;
	}

	public void setFileDataType(FileDataType fileDataType) {
		this.fileDataType = fileDataType;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getUploadSummary() {
		return uploadSummary;
	}

	public void setUploadSummary(String uploadSummary) {
		this.uploadSummary = uploadSummary;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getOriginalSize() {
		return originalSize;
	}

	public void setOriginalSize(Long originalSize) {
		this.originalSize = originalSize;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public Long getUploadedSize() {
		return uploadedSize;
	}

	public void setUploadedSize(Long uploadedSize) {
		this.uploadedSize = uploadedSize;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getStorageDomain() {
		return storageDomain;
	}

	public void setStorageDomain(String storageDomain) {
		this.storageDomain = storageDomain;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getCompression() {
		return compression;
	}

	public void setCompression(String compression) {
		this.compression = compression;
	}

	public String getProcessingOptions() {
		return processingOptions;
	}

	public void setProcessingOptions(String processingOptions) {
		this.processingOptions = processingOptions;
	}

	private String fileType;
	private Long originalSize;
	private Long organizationId;
	private String userInfo;
	private Long uploadedSize;
	private String storageType;
	private String storageLocation;
	private String storageDomain;
	private String encryption;
	private String compression;
	private String processingOptions;

	public String getRowKeyAsString() {
		StringBuilder str = new StringBuilder();
		str.append(this.id);
		str.append(ROW_KEY_SEPARATOR);
		str.append(this.fileDataType.toString());
		str.append(ROW_KEY_SEPARATOR);
		return str.toString();
	}

	public void parseRowKey(String rowKey) {
		if (rowKey != null && !StringUtils.isEmpty(rowKey)) {
			String[] arr = split(rowKey, ROW_KEY_SEPARATOR.charValue());
			this.id = arr[0];
			this.fileDataType = FileDataType.fromString(arr[1]);
			// this.uploadedTs =
			// HBaseUtil.getTSFromReverseTimestamp(Long.valueOf(Long.parseLong(arr[2])));
		}

	}

	public static String[] split(String strToSplit, char delimiter) {
		ArrayList<String> arr = new ArrayList();
		int start = 0;

		for (int i = 0; i < strToSplit.length(); ++i) {
			if (strToSplit.charAt(i) == delimiter) {
				arr.add(strToSplit.substring(start, i));
				start = i + 1;
			}
		}

		arr.add(strToSplit.substring(start));
		return (String[]) arr.toArray(new String[0]);
	}

}
