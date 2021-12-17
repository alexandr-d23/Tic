package bwtmtf.decoder;

import common.Decoder;
import common.Constants;
import javafx.util.Pair;
import bwtmtf.common.CharacterWithIndex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BWTDecoder implements Decoder {

    @Override
    public void decode(Path encoded, Path decoded) {
        System.out.println("DECODE____");
        try (BufferedReader reader = Files.newBufferedReader(encoded, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(decoded, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            List<Character> symbolsList;
            while (line != null && !line.isEmpty()) {
                int source = Integer.parseInt(line);
                symbolsList = new ArrayList<>();
                LinkedList<Integer> mtfCode = new LinkedList<>();
                line = reader.readLine();
                for (int i = 0; i < line.length(); i++) {
                    symbolsList.add(line.charAt(i));
                }
//                System.out.println("DECODED Symbols: " + symbolsList);
                symbolsList.add(Constants.TRANSLATE);
                line = reader.readLine();
                String[] stringDigits = line.split(Constants.SPACE);
                for (int i = 0; i < stringDigits.length; i++) {
                    mtfCode.add(Integer.parseInt(stringDigits[i]));
                }
//                System.out.println("DECODED MTF: " + mtfCode);
                List<Character> bwtString = getBwtString(mtfCode, symbolsList);
                writer.write(encodeBwt(source, bwtString));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Character> getBwtString(List<Integer> digits, List<Character> symbols) {
        LinkedList<Character> chars = new LinkedList<>(symbols);
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < digits.size(); i++) {
            Character res = chars.get(digits.get(i));
            result.add(res);
            chars.addFirst(chars.remove((int) digits.get(i)));
        }
//        System.out.println("DECODED BWT LAST SYMBOLS: " + result);
        return result;
    }

    public String encodeBwt(int sourceInd, List<Character> symbols) {
        List<Character> sorted = new ArrayList<>(symbols).stream().sorted(Comparator.comparingInt(o -> o)).collect(Collectors.toList());
        List<CharacterWithIndex> firstSymbols = new ArrayList<>();
        List<CharacterWithIndex> lastSymbols = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            firstSymbols.add(new CharacterWithIndex(sorted.get(i), i));
        }
        List<CharacterWithIndex> helperList = new ArrayList<>(firstSymbols);
        for (int i = 0; i < symbols.size(); i++) {
            int finalI = i;
            int index = helperList
                    .stream()
                    .filter(
                            (CharacterWithIndex ind) -> ind.getCharacter() == symbols.get(finalI)
                    )
                    .findFirst()
                    .get()
                    .getIndex();
            helperList.removeIf((CharacterWithIndex ch) -> ch.getIndex() == index);
            lastSymbols.add(new CharacterWithIndex(symbols.get(i), index));
        }
        List<Pair<CharacterWithIndex, CharacterWithIndex>> transforms = new ArrayList<>();
        for (int i = 0; i < symbols.size(); i++) {
            transforms.add(
                    new Pair<>(firstSymbols.get(i),
                            lastSymbols.get(i))
            );
        }
        transforms = transforms.stream().sorted(Comparator.comparingInt(o -> o.getValue().getIndex())).collect(Collectors.toList());
//        System.out.println(transforms);
        StringBuilder builder = new StringBuilder();
        int src = sourceInd;
        for (int i = 0; i < transforms.size(); i++) {
            src = transforms.get(src).getKey().getIndex();
            builder.append(symbols.get(src));
        }
        return builder.toString();
    }

}
