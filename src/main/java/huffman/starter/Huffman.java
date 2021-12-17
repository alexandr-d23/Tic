package huffman.starter;

import common.Constants;
import huffman.decoder.HuffmanDecoder;
import huffman.encoder.HuffmanEncoder;

import java.nio.file.Path;
import java.nio.file.Paths;


public class Huffman {

    public static void main(String[] args) {
        Path path = Paths.get(Constants.sourcePath);
        Path encoded = Paths.get(Constants.encodePath);
        Path decoded = Paths.get(Constants.decodePath);
        HuffmanEncoder encoder = new HuffmanEncoder();
        encoder.encode(path, encoded);
        HuffmanDecoder decoder = new HuffmanDecoder();
        decoder.decode(encoded, decoded);
    }

}
