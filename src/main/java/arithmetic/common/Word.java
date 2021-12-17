package arithmetic.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Word {
    private Character word;
    private double probability;
}
