package hemming.common;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public
class HemmingResult {
    private boolean A;
    private boolean B;
    private boolean C;
    private boolean D;

    public void reverseA() {
        A = !A;
    }

    public void reverseB() {
        B = !B;
    }

    public void reverseC() {
        C = !C;
    }

    public void reverseD() {
        D = !D;
    }

    public List<Boolean> toList() {
        List<Boolean> list = new ArrayList<>(4);
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        return list;
    }
}
