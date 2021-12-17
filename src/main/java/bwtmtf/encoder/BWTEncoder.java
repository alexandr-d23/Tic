package bwtmtf.encoder;

import common.Encoder;
import common.Constants;
import bwtmtf.common.BwtEncodeResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BWTEncoder implements Encoder {

    HashSet<Character> symbols = new HashSet<>();

    @Override
    public void encode(Path source, Path encoded) {
        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(encoded, StandardCharsets.UTF_8)) {
            char[] buffer = new char[100];
            int res = reader.read(buffer);
            List<Character> symbolsList;
            BwtEncodeResult result;
            while (res != -1) {
                for (int i = 0; i < buffer.length; i++) {
                    if (buffer[i] != Constants.TRANSLATE) symbols.add(buffer[i]);
                }
                symbolsList = new ArrayList<>(symbols);
                symbolsList.add(Constants.TRANSLATE);
                result = getBwtResult(buffer, res);
                res = reader.read(buffer);
//                System.out.println("SymbolsList: " + symbolsList);
                List<Integer> mtfCode = getMtfDigits(result.getLastSymbols(), symbolsList);
                writer.write(result.getIndexSource() + Constants.TRANSLATE_STRING);
                for (int i = 0; i < symbolsList.size(); i++) {
                    if (symbolsList.get(i) != '\n') {
                        writer.write(symbolsList.get(i));
//                        System.out.println("Symbol: '" + symbolsList.get(i) + "'");
                    }
                }
                writer.write(Constants.TRANSLATE);
                for (int i = 0; i < mtfCode.size(); i++) {
                    writer.write(mtfCode.get(i) + Constants.SPACE);
                }
//                System.out.println("BWT LAST SYMBOLS: " + result.getLastSymbols());
                writer.write(Constants.TRANSLATE_STRING);
                symbols = new HashSet<>();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BwtEncodeResult getBwtResult(char[] charsArray, int size) {
        LinkedList<Character> chars = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            chars.add(charsArray[i]);
        }
        LinkedList<Character> sourceString = new LinkedList<>(chars);
        List<LinkedList<Character>> strings = new ArrayList<>();
        for (int i = 0; i < chars.size(); i++) {
            chars.add(chars.pollFirst());
            strings.add(new LinkedList<>(chars));
        }
        strings = strings.stream().sorted((o1, o2) -> {
            for (int i = 0; i < o1.size(); i++) {
                int comparing = o1.get(i) - o2.get(i);
                if (comparing != 0) return comparing;
            }
            return 0;
        }).collect(Collectors.toList());
        int sourceStringIndex = 0;
        List<Character> lastSymbols = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals(sourceString)) sourceStringIndex = i;
            lastSymbols.add(strings.get(i).peekLast());
        }
        BwtEncodeResult result = new BwtEncodeResult(sourceStringIndex, lastSymbols);
        return result;
    }

    public List<Integer> getMtfDigits(List<Character> string, List<Character> symbols) {
        LinkedList<Character> chars = new LinkedList<>(symbols);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < string.size(); i++) {
            int res = chars.indexOf(string.get(i));
            result.add(res);
            chars.addFirst(chars.remove(res));
        }
        return result;
    }
}