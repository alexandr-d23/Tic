package arithmetic.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Interval {
    private Long start;
    private Long end;

    public List<Byte> reduceCommon() {
        List<Byte> digits = new ArrayList<>();
        int lengthStart = getDigitLength(start);
        int lengthEnd = getDigitLength(end);
        if (lengthStart < 16 && lengthEnd < 16) {
            while (lengthStart < 16 && lengthEnd < 16) {
                digits.add((byte) 0);
                start *= 10;
                end *= 10;
                lengthStart = getDigitLength(start);
                lengthEnd = getDigitLength(end);
            }
        }
        long divider;
        if (lengthStart != lengthEnd) return digits;
        for (int i = 1; i < lengthStart; i++) {
            divider = (long) Math.pow(10, lengthStart - 1);
            if ((start / divider) % 10 == (end / divider) % 10) {
                digits.add((byte) ((start / divider) % 10));
                start = start % divider;
                end = end % divider;
                start *= 10;
                end *= 10;
                end += 9;
            } else break;
        }
        return digits;
    }

    public static int getDigitLength(long digit) {
        int i = 1;
        while (Math.abs(digit) > 9) {
            digit /= 10;
            i++;
        }
        return i;
    }

    public List<Byte> getResultInBytes() {
        Long result = start + (end - start) / 2;
        List<Byte> digits = new ArrayList<>();
        int lengthStart = getDigitLength(start);
        long divider;
        int k = 0;
        for (int i = 1; i <= lengthStart; i++) {
            divider = (long) Math.pow(10, lengthStart - i);
            digits.add((byte) ((result / divider) % 10));
        }
        for (int i = 0; lengthStart + i <= 16; i++) {
            start *= 10;
            digits.add((byte) 0);
            lengthStart = getDigitLength(start);
        }
        return digits;
    }
}
