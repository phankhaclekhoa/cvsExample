package csv;

import lombok.Getter;

@Getter
public class NumberedDataRecord <T> {
    private int rowNum;
    private T record;

    public NumberedDataRecord(int rowNum, T record) {
        this.rowNum = rowNum;
        this.record = record;
    }
}
