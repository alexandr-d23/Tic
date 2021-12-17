package huffman.decoder;

import common.Constants;
import common.Decoder;
import common.UtilClass;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class HuffmanDecoder implements Decoder {

    private HashMap<String, Character> words = new HashMap<>();

    @Override
    public void decode(Path encoded, Path decoded) {
        try (InputStream wordCodesReader = Files.newInputStream(encoded)) {
            //сколько бит нужно пропустить
            int skip = UtilClass.readWordsTable(words, wordCodesReader);
            if (skip == -1) {
                Files.newBufferedWriter(decoded, StandardCharsets.UTF_8).write(Constants.EMPTY_STRING);
                return;
            }
            try (BufferedWriter writer = Files.newBufferedWriter(decoded, StandardCharsets.UTF_8);
                 InputStream byteReader = Files.newInputStream(encoded)) {
                byte[] lastBytes = new byte[0];
                int res = 0;
                StringBuilder currentWord = new StringBuilder();
                StringBuilder encodedString;
                byteReader.skip(skip);
                while (res != -1) {
                    byte[] buffer = new byte[255];
                    boolean[] lastBits = UtilClass.bytesToBitsArray(lastBytes, lastBytes.length);
                    res = byteReader.read(buffer);
                    if (res == -1) break;
                    encodedString = new StringBuilder();
                    currentWord = addWordsFromBits(currentWord, encodedString, lastBits);
                    boolean[] bits = UtilClass.bytesToBitsArray(buffer, res - 2);
                    currentWord = addWordsFromBits(currentWord, encodedString, bits);
                    writer.write(encodedString.toString());
                    lastBytes = new byte[]{buffer[res - 2], buffer[res - 1]};
                }
                int size = lastBytes[1];
                encodedString = new StringBuilder();
                boolean[] lastSymbol = UtilClass.bytesToBitsArray(new byte[]{lastBytes[0]}, 1);
                boolean[] reducedLastSymbol = new boolean[size];
                for (int i = 0; i < size; i++) {
                    reducedLastSymbol[i] = lastSymbol[i];
                }
                addWordsFromBits(currentWord, encodedString, reducedLastSymbol);
                writer.write(encodedString.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder addWordsFromBits(StringBuilder currentWord, StringBuilder encodedString, boolean[] lastBits) {
        for (int i = 0; i < lastBits.length; i++) {
            if (lastBits[i]) currentWord.append('1');
            else currentWord.append('0');

            if (words.containsKey(currentWord.toString())) {
                Character c = words.get(currentWord.toString());
                encodedString.append(c);
                currentWord = new StringBuilder();
            }
        }
        return currentWord;
    }
}
