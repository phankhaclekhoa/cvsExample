package csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserFileDataHelper {
    private static final Logger LOG = LogManager.getLogger(UserFileDataHelper.class);
    public static final String ID_SEPARATOR = ":";

    public static UserFileDataRecordId getRecordRowNumberAndId(String recordId) {
        Integer rowNum = 0;
        String uniqueId = "";
        if (recordId != null && !recordId.isEmpty()) {
            int separatorIndex = recordId.indexOf(ID_SEPARATOR);
            try {
                rowNum = Integer.parseInt(recordId.substring(0, separatorIndex));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            uniqueId = recordId.substring(separatorIndex + 1);
        }
        return new UserFileDataRecordId(rowNum, uniqueId);
    }
}
