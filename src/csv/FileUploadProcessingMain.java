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
	}

}
