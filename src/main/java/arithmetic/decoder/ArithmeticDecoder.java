package arithmetic.decoder;

import common.Decoder;
import common.Info;
import common.UtilClass;
import lombok.SneakyThrows;
import arithmetic.common.Interval;
import arithmetic.common.Word;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArithmeticDecoder implements Decoder {

    private List<Word> firstInterval = new ArrayList<>();
    private int allWordsCount = 0;
    List<Byte> digits = new ArrayList<>();

    @Override
    public void decode(Path encoded, Path decoded) {
        System.out.println("start decode");
        try (InputStream wordCodesReader = Files.newInputStream(encoded)) {
            //сколько бит нужно пропустить
            Info info = UtilClass.readWordsInterval(firstInterval, wordCodesReader);
            int skip = info.getSkip();
            System.out.println("INFO:: " + info);
            allWordsCount = info.getInputSize();
            wordCodesReader.close();
            System.out.println("Skip:" + skip);
            if (skip == -1) return;
            try (BufferedWriter writer = Files.newBufferedWriter(decoded, StandardCharsets.UTF_8);
                 InputStream byteReader = Files.newInputStream(encoded)) {
                readByteList(byteReader, skip, info.getCodeSize());
                decode(digits, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private void readByteList(InputStream byteReader, int skip, int size) {
        byteReader.skip(skip);
        byte[] buffer = new byte[(size + size % 2) / 2];
        byteReader.read(buffer);
        List<Boolean> bits = UtilClass.bytesToBitsList(buffer, buffer.length * 8);
        for (int i = 0; i < bits.size(); i += 4) {
            int b = 0;
            if (bits.get(i)) b += 8;
            if (bits.get(i + 1)) b += 4;
            if (bits.get(i + 2)) b += 2;
            if (bits.get(i + 3)) b += 1;
            digits.add((byte) b);
        }
    }

    @SneakyThrows
    private void decode(List<Byte> bytes, BufferedWriter writer) {
        Interval startEnd = new Interval(0L, 10000000000000000L);
        List<Double> currentInterval = firstInterval.stream().map(Word::getProbability).collect(Collectors.toList());
        currentInterval.add(0, 0.0);
        int currentWordStart = 0;
        Long code;
        for (int i = 0; i < allWordsCount; i++) {
            code = getLongFromByteList(currentWordStart, currentWordStart + 15, bytes);
            currentWordStart += getWord(currentInterval, code, startEnd, writer);
        }
    }

    private Long getLongFromByteList(int startIndex, int endIndex, List<Byte> bytes) {
        long l = bytes.get(startIndex);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            l *= 10;
            l += i < bytes.size() ? bytes.get(i) : 0;
        }
        return l;
    }

    @SneakyThrows
    private int getWord(List<Double> currentInterval, Long code, Interval startEnd, BufferedWriter writer) {
        Long diff = startEnd.getEnd() - startEnd.getStart();
        int currentWordInd = currentInterval.size() - 1;
        Long wordProbability = (long) (10000000000000000L * ((double) (code - startEnd.getStart()) / (startEnd.getEnd() - startEnd.getStart())));

        for (int i = 1; i < currentInterval.size(); i++) {
            if (wordProbability < (long) (10000000000000000L * currentInterval.get(i))) {
                currentWordInd = i;
                break;
            }
        }
        writer.write(firstInterval.get(currentWordInd - 1).getWord());
        startEnd.setEnd(
                startEnd.getStart() + (long) (diff * currentInterval.get(currentWordInd))
        );
        startEnd.setStart(
                startEnd.getStart() + (long) (diff * currentInterval.get(currentWordInd - 1))
        );
        return startEnd.reduceCommon().size();
    }

}
