package csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FileUploadProcessor {

	private static Logger LOG = LogManager.getLogger(FileUploadProcessor.class);

	@SuppressWarnings("rawtypes")
	public static void processFile(FilesRecord filesRecord) throws IOException,
			ClassNotFoundException {
		System.out.println("Processing file type: "
				+ filesRecord.getFileDataType());
		AbstractProcessor fileProcessor = getFileProcessorByFileType(filesRecord
				.getFileDataType());
		try {
			if (fileProcessor != null) {
				fileProcessor.process(filesRecord);
			} else {
				System.out.println("No known processor for file type: "
						+ filesRecord.getFileDataType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static AbstractProcessor getFileProcessorByFileType(
			FileDataType fileType) throws IOException, ClassNotFoundException {
		AbstractProcessor fileProcessor;
		switch (fileType) {
		case CUSTOMER:
			fileProcessor = new CustomerProcessor();
			break;
		default:
			fileProcessor = null;
			break;
		}

		return fileProcessor;
	}
}
