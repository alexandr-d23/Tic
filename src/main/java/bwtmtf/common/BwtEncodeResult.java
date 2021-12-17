package bwtmtf.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BwtEncodeResult {
    private int indexSource;
    private List<Character> lastSymbols;
}
