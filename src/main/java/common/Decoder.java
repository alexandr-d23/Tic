package common;

import java.nio.file.Path;

public interface Decoder {
    void decode(Path encoded, Path decoded);
}
