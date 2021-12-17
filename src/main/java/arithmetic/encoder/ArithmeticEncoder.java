package arithmetic.encoder;

import common.Encoder;
import common.Constants;
import common.UtilClass;
import lombok.SneakyThrows;
import arithmetic.common.Interval;
import arithmetic.common.Word;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ArithmeticEncoder implements Encoder {

    private final Map<Character, Integer> probabilities = new HashMap<>();
    private List<Word> firstInterval = new ArrayList<>();
    private final HashMap<Character, Integer> indices = new HashMap<>();
    int allWordsCount = 0;

    @Override
    public void encode(Path source, Path encoded) {
        try {
            int count = UtilClass.calculateProbabilities(source, probabilities);
            createArithmeticInterval(count);
            getEncodedDigit(source, encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createArithmeticInterval(int count) {
        AtomicReference<Double> lastProbability =
                new AtomicReference<>(0.0);
        firstInterval = probabilities
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map((Map.Entry<Character, Integer> entry) -> {
                            lastProbability.updateAndGet(
                                    v -> v + (double) entry.getValue() / count
                            );
                            return Word.builder()
                                    .word(entry.getKey())
                                    .probability(lastProbability.get())
                                    .build();
                        }
                ).collect(Collectors.toList());
        for (int i = 0; i < firstInterval.size(); i++) {
            indices.put(firstInterval.get(i).getWord(), i + 1);
        }
    }

    private void getEncodedDigit(Path source, Path encoded) {
        int allDigitsCount = 0;
        List<Byte> digits = new ArrayList<>();
        try (FileOutputStream fos = new FileOutputStream(encoded.toFile());
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw);
             BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            writeInterval(writer);
            List<Double> currentInterval = firstInterval.stream()
                    .map(Word::getProbability)
                    .collect(Collectors.toList());
            currentInterval.add(0, 0.0);
            char c;
            Interval startEnd = new Interval(0L, 10000000000000000L);
            boolean isFirst = true;
            while (line != null) {
                if (!isFirst) {
                    line = Constants.EMPTY_STRING + Constants.TRANSLATE + line;
                }
                isFirst = false;
                for (int i = 0; i < line.length(); i++) {
                    c = line.charAt(i);
                    allWordsCount++;
                    digits.addAll(addWord(currentInterval, c, startEnd));
                    history.add(new Interval(startEnd.getStart(), startEnd.getEnd()));
                    allDigitsCount += (digits.size() - digits.size() % 2);
                    writeDigits(digits, fos);
                }
                line = reader.readLine();
            }
            digits.addAll(startEnd.getResultInBytes());
            allDigitsCount += digits.size();
            String s = Constants.EMPTY_STRING + Constants.TRANSLATE + allWordsCount + Constants.SPACE + allDigitsCount;
            osw.write(s);
            if (digits.size() % 2 == 1) {
                digits.add((byte) 0);
            }
//            System.out.println("digits" + digits);
            writeDigits(digits, fos);
            //  System.out.println("CurrentWord:  " + words);
            //decode(digits);
//               fos.write(size);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Interval> history = new ArrayList<>();

    private List<Byte> addWord(List<Double> currentInterval, Character c, Interval startEnd) {
        Long diff = startEnd.getEnd() - startEnd.getStart();
        int currentWordInd = indices.get(c);
        startEnd.setEnd(
                startEnd.getStart() + (long) (diff * currentInterval.get(currentWordInd))
        );
        startEnd.setStart(
                startEnd.getStart() + (long) (diff * currentInterval.get(currentWordInd - 1))
        );
        return startEnd.reduceCommon();
    }

    @SneakyThrows
    private void writeInterval(BufferedWriter writer) {
        StringBuilder s = new StringBuilder();
        Character c;
        for (Word word : firstInterval) {
            c = word.getWord();
            if (c == Constants.TRANSLATE)
                s.append(word.getProbability()).append(Constants.TABLE_DIVIDER).append(Constants.BLOCK_DIVIDER);
            else
                s.append(word.getProbability()).append(Constants.TABLE_DIVIDER).append(c).append(Constants.BLOCK_DIVIDER);
            writer.write(s.toString());
            s = new StringBuilder();
        }
        writer.write(Constants.TRANSLATE_STRING);
        writer.flush();
    }

    @SneakyThrows
    private void writeDigits(List<Byte> digits, OutputStream output) {
        int size = digits.size() - digits.size() % 2;
        if (size == 0) return;
        List<Boolean> bits = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int b = digits.get(i);
            for(int d = 8; d >0; d/=2)
            if (b >= d) {
                b -= d;
                bits.add(true);
            } else bits.add(false);
        }
        digits.subList(0, size).clear();
        output.write(UtilClass.bitsToByte(bits));
    }

}
