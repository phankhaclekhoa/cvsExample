package csv;


import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.MappingStrategy;

import lombok.Setter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class AbstractProcessor<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractProcessor.class);

    @Setter
    protected FilesDataParser<T> fileParser;
    protected Optional<Iterator<T>> dataIteratorOption;
  
    protected FilesRecord filesRecord;

    @Setter
    protected FileDataHelper fileDataHelper;


    protected Boolean init(FilesRecord record, MappingStrategy<T> mappingStrategy) {
    	// Read file from S3
        try {
            this.filesRecord = record;
            // Init file parser if null
            if (fileParser == null) {
                fileParser = new FilesDataParser<>(mappingStrategy);
            }
            dataIteratorOption = fileParser.getDataIterator(filesRecord, "", "", "", "", "", 0);
            if(!dataIteratorOption.isPresent()){
                fileParser.close();
                System.out.println("Failed to parse csv file and construct data records for file id: " + this.filesRecord.getId());
                return false;
            }
            return true;
        } catch (Exception ex) {
            LOG.error("Failed to retrieve file record", ex);
            return false;
        }
    }

    protected HeaderColumnNameMappingStrategy getMappingStrategy(Class recordFileClass) {
    	HeaderColumnNameTranslateMappingStrategy strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(recordFileClass);
        strategy.setColumnMapping(getColumnMappings());
        return strategy;
    }

    protected abstract Map<String, String> getColumnMappings();

    /**
     * Process a file and then update its status in HBase
     * @param filesRecord the file record from HBase that needs processing
     */
    public void process(FilesRecord filesRecord) {
        if (init(filesRecord, getMappingStrategy())) {
            preProcessFile();
            System.out.println(filesRecord.toString());
            processRecords(filesRecord);
        }
    }

    public abstract MappingStrategy<T> getMappingStrategy();

    protected void preProcessFile() {
        if (fileDataHelper == null) {
            fileDataHelper = new FileDataHelper();
        }
        System.out.println("Downloaded S3 file and initialized " + filesRecord.getId());
        filesRecord.setStatus(FileStatus.PROCESSING);
//        if (!fileDataHelper.updateFilesRecord(filesRecord)) {
//            LOG.error("Failed to update file record with fileId: " + filesRecord.getId());
//            System.exit(-1);
//        }
    }

    protected void closeFileParserAndExit() {
        fileParser.close();
        System.exit(-1);
    }

    /**
     * Process a file and then update its status in HBase
     * @param filesRecord the file record from HBase that needs processing
     */
    public abstract void processRecords(FilesRecord filesRecord);


    protected List<T> getRecordsFromStream(){
        Iterator<T> records = dataIteratorOption.get();
        List<T> list = new ArrayList<>();
        while (records.hasNext()) {
            list.add(records.next());
        }
        return list;
    }


	protected Iterator<T> getDataIterator() {
        return dataIteratorOption.get();
    }

}
