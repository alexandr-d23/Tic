package bwtmtf.starter;

import bwtmtf.decoder.BWTDecoder;
import bwtmtf.encoder.BWTEncoder;
import common.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BwtMtfStarter {

    public static void main(String[] args) {
        Path path = Paths.get(Constants.sourcePath);
        Path encoded = Paths.get(Constants.encodePath);
        Path decoded = Paths.get(Constants.decodePath);
        BWTEncoder encoder = new BWTEncoder();
        encoder.encode(path, encoded);
        BWTDecoder decoder = new BWTDecoder();
        decoder.decode(encoded, decoded);
    }

}
