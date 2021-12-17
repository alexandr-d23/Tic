package common;

import lombok.SneakyThrows;
import arithmetic.common.Word;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UtilClass {

    public static byte[] bitsToByte(List<Boolean> bitsList) {
        BitSet bits = new BitSet(bitsList.size());
        byte[] result = new byte[bitsList.size() / 8];
        for (int i = 0; i < bitsList.size(); i++) {
            if (bitsList.get(i)) {
                bits.set(i);
            }
        }
        byte[] fromBitset = bits.toByteArray();
        for (int i = 0; i < fromBitset.length; i++) {
            result[i] = fromBitset[i];
        }
        return result;
    }

    public static String bitsToString(List<Boolean> array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i)) builder.append('1');
            else builder.append('0');
        }
        return builder.toString();
    }

    @SneakyThrows
    public static int calculateProbabilities(Path path, Map<Character, Integer> wordsProb) {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        String line = reader.readLine();
        int k = 0;
        boolean isFirst = true;
        while (line != null) {
            if (!isFirst) {
                k++;
                addValue(Constants.TRANSLATE, wordsProb);
            }
            isFirst = false;
            char c;
            for (int i = 0; i < line.length(); i++) {
                c = line.charAt(i);
                k++;
                addValue(c, wordsProb);
            }
            line = reader.readLine();
        }
        return k;
    }

    @SneakyThrows
    public static void writeWordsTable(HashMap<Character, List<Boolean>> codes, BufferedWriter writer) {
        HashMap<String, Character> allWords = addAllWords(codes);
        for (Map.Entry<String, Character> entry : allWords.entrySet()) {
            if (entry.getValue() == Constants.TRANSLATE) writer.write(entry.getKey() + Constants.BLOCK_DIVIDER);
            else writer.write(entry.getKey() + Constants.TABLE_DIVIDER + entry.getValue() + Constants.BLOCK_DIVIDER);
        }
        writer.write(Constants.TRANSLATE);
        writer.flush();
    }

    private static HashMap<String, Character> addAllWords(HashMap<Character, List<Boolean>> codes) {
        HashMap<String, Character> allWords = new HashMap<>();
        for (Map.Entry<Character, List<Boolean>> entry : codes.entrySet()) {
            allWords.put(bitsToString(entry.getValue()), entry.getKey());
        }
        return allWords;
    }

    public static boolean[] bytesToBitsArray(byte[] bytes, int size) {
//        System.out.println("toBooleanArray bytes size: " + size);
        if (size <= 0) return new boolean[0];
        BitSet bits = BitSet.valueOf(bytes);
        boolean[] bools = new boolean[size * 8];
        for (int i = bits.nextSetBit(0); i != -1 && i < bools.length; i = bits.nextSetBit(i + 1)) {
            bools[i] = true;
        }
        return bools;
    }

    public static List<Boolean> bytesToBitsList(byte[] bytes, int size) {
//        System.out.println("toBooleanArray bytes size: " + size);
        if (size <= 0) return new ArrayList<>();
        BitSet bits = BitSet.valueOf(bytes);
        List<Boolean> bools = new ArrayList<>(size * 8);
        for (int i = 0; i < size; i++) {
            bools.add(false);
        }
        for (int i = bits.nextSetBit(0); i != -1 && i < bools.size(); i = bits.nextSetBit(i + 1)) {
            bools.set(i, true);
        }
        System.out.println("Completed");
        return bools;
    }

    @SneakyThrows
    public static int readWordsTable(HashMap<String, Character> source, InputStream input) {
        BufferedReader reader = new BufferedReader(new InputStreamReader((input), StandardCharsets.UTF_8));
        String line = reader.readLine();
        if (line == null) return -1;
        int size = line.getBytes(StandardCharsets.UTF_8).length + 1;
        String[] entries = line.split(Constants.BLOCK_DIVIDER);
        String[] word;
        for (String entry : entries) {
            word = entry.split(Constants.TABLE_DIVIDER);
            if (word.length == 1) {
                source.put(word[0], Constants.TRANSLATE);
            } else {
//                System.out.println("word0 :" + word[0]);
                source.put(word[0], word[1].charAt(0));
            }
        }
//        System.out.println("Words source: " + source);
        reader.close();
        return size;
    }

    @SneakyThrows
    public static Info readWordsInterval(List<Word> firstInterval, InputStream input) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader((input), StandardCharsets.UTF_8)
        )) {
            String line = reader.readLine();
            if (line == null || line.isEmpty())
                return Info.builder()
                        .inputSize(0)
                        .codeSize(0)
                        .skip(0)
                        .build();
            int skip = line.getBytes(StandardCharsets.UTF_8).length + 1;
            String[] entries = line.split(Constants.BLOCK_DIVIDER);
            String[] word;
            for (String entry : entries) {
                word = entry.split(Constants.TABLE_DIVIDER);
                if (word.length == 1) {
                    firstInterval.add(
                            Word.builder()
                                    .word(Constants.TRANSLATE)
                                    .probability(Double.parseDouble(word[0]))
                                    .build()
                    );
                } else {
                    firstInterval.add(
                            Word.builder()
                                    .word(word[1].charAt(0))
                                    .probability(Double.parseDouble(word[0]))
                                    .build()
                    );
                }
            }
            String lastLine = Constants.EMPTY_STRING;
            line = reader.readLine();
            while (line != null) {
                lastLine = line;
                line = reader.readLine();
            }
            int inputSize = Integer.parseInt(lastLine.split(Constants.SPACE)[0]);
            int codeSize = Integer.parseInt(lastLine.split(Constants.SPACE)[1]);
            reader.close();
            return Info.builder()
                    .skip(skip)
                    .inputSize(inputSize)
                    .codeSize(codeSize)
                    .build();
        }
    }

    private static void addValue(char c, Map<Character, Integer> wordsProb) {
        Integer value;
        value = wordsProb.get(c);
        if (value == null) value = 0;
        value++;
        wordsProb.put(c, value);
    }
}
