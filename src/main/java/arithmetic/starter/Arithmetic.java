package arithmetic.starter;

import arithmetic.decoder.ArithmeticDecoder;
import arithmetic.encoder.ArithmeticEncoder;
import common.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Arithmetic {

    public static void main(String[] args) {
        Path path = Paths.get(Constants.sourcePath);
        Path encoded = Paths.get(Constants.encodePath);
        Path decoded = Paths.get(Constants.decodePath);
        ArithmeticEncoder encoder = new ArithmeticEncoder();
        ArithmeticDecoder decoder = new ArithmeticDecoder();
        encoder.encode(path, encoded);
        decoder.decode(encoded, decoded);
    }

}
