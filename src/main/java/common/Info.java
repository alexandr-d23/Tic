package common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Info {
    private int skip;
    private int inputSize;
    private int codeSize;
}
