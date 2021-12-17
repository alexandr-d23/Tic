package common;

import java.nio.file.Path;

public interface Encoder {
    void encode(Path source, Path encoded);
}
