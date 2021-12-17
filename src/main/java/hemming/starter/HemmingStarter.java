package hemming.starter;

import common.Constants;
import hemming.decoder.HemmingDecoder;
import hemming.encoder.HemmingEncoder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class HemmingStarter {

    public static void main(String[] args) {
//        Path path = Paths.get("src/main/java/fourth/files/source");
        Path path = Paths.get(Constants.sourcePath);
        Path encoded = Paths.get(Constants.encodePath);
        Path decoded = Paths.get(Constants.decodePath);
        HemmingEncoder encoder = new HemmingEncoder();
//        encoder.encode(path, encoded);
        HemmingDecoder decoder = new HemmingDecoder();
        decoder.decode(encoded, decoded);
    }
}
