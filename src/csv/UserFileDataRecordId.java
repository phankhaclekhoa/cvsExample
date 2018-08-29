package csv;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFileDataRecordId {
    private Integer rowNumber;
    private String uniqueId;
}
