package huffman.encoder;

import common.Encoder;
import common.Constants;
import huffman.common.Node;
import common.UtilClass;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class HuffmanEncoder implements Encoder {

    private Map<Character, Integer> probabilities = new HashMap<>();
    private Node rootNode;
    private HashMap<Character, List<Boolean>> codes;

    @Override
    public void encode(Path source, Path encoded) {
        try {
            UtilClass.calculateProbabilities(source, probabilities);
            if (probabilities.size() == 0) {
                Files.newBufferedWriter(encoded).write(Constants.EMPTY_STRING);
                return;
            }
            calculateHuffmanCodes();
            writeAllBytes(source, encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateHuffmanCodes() {
        List<Node> nodes = probabilities
                .entrySet()
                .stream()
                .map((Map.Entry<Character, Integer> entry) ->
                        Node.builder()
                                .word(entry.getKey())
                                .probability(entry.getValue())
                                .build()
                ).collect(Collectors.toList());
        codes = new HashMap<>(probabilities.size());
        nodes.sort(Comparator.comparingInt(Node::getProbability).reversed());
        if (nodes.size() == 1) {
            setCode(nodes.get(0), new ArrayList<>(), false);
        } else {
            while (nodes.size() > 1) {
                Node nodeFirst = nodes.get(nodes.size() - 1);
                nodes.remove(nodes.size() - 1);
                Node nodeSecond = nodes.get(nodes.size() - 1);
                nodes.remove(nodes.size() - 1);
                Node newNode = Node
                        .builder()
                        .leftChild(nodeFirst)
                        .rightChild(nodeSecond)
                        .probability(nodeFirst.getProbability() + nodeSecond.getProbability())
                        .build();
                int addIndex = 0;
                for (int i = nodes.size() - 1; i >= 0; i--) {
                    if (nodes.get(i).getProbability() >= newNode.getProbability()) {
                        addIndex = i + 1;
                        break;
                    }
                }
                nodes.add(addIndex, newNode);
            }
            if (nodes.size() == 0) return;
            rootNode = nodes.get(0);
            if (rootNode != null) {
                setCode(rootNode.getLeftChild(), new ArrayList<>(), false);
                setCode(rootNode.getRightChild(), new ArrayList<>(), true);
            }
        }
    }

    @SneakyThrows
    public void writeAllBytes(Path source, Path encoded) {
        //биты, которые не поместились в байт
        List<Boolean> modes = new ArrayList<>();
        try (FileOutputStream fos = new FileOutputStream(encoded.toFile());
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw);
             BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8);) {
            UtilClass.writeWordsTable(codes, writer);
            String line = reader.readLine();
            boolean isFirst = true;
            while (line != null) {
                if (!isFirst) {
                    line = Constants.EMPTY_STRING + Constants.TRANSLATE + line;
                }
                isFirst = false;
                byte[] bytes = getByteListFromWord(line, modes);
                fos.write(bytes);
                fos.flush();
                line = reader.readLine();
            }
            //в последнем байте запишем размер сколько нужно считать с предыдущего байта
            byte[] size = new byte[]{(byte) (modes.size() == 0 ? 8 : modes.size())};
            byte[] bytes = getByteListFromWord(Constants.EMPTY_STRING, modes);
            fos.write(bytes);
            fos.write(size);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCode(Node node, List<Boolean> parentCode, Boolean nextValue) {
        if (node == null) return;
        List<Boolean> currentCode = new ArrayList<>(parentCode.size() + 1);
        currentCode.addAll(parentCode);
        currentCode.add(nextValue);
        if (node.getWord() != null) {
            codes.put(node.getWord(), currentCode);
        }
        setCode(node.getLeftChild(), currentCode, false);
        setCode(node.getRightChild(), currentCode, true);
    }

    public byte[] getByteListFromWord(String line, List<Boolean> modes) {
        List<Boolean> bools = new ArrayList<>(line.length());
        bools.addAll(modes);
        modes.clear();
        char c;
        for (int i = 0; i < line.length(); i++) {
            c = line.charAt(i);
            bools.addAll(codes.get(c));
        }

        BitSet bits = new BitSet(bools.size() - bools.size() % 8);
        for (int i = 0; i < bools.size() - bools.size() % 8; i++) {
            if (bools.get(i)) {
                bits.set(i);
            }
        }
        //заполняем элементы которые меньше бита
        byte[] bytes;
        for (int i = bools.size() - bools.size() % 8; i < bools.size(); i++) {
            modes.add(bools.get(i));
        }
        if (bools.size() < 8) {
            if (!line.isEmpty())
                bytes = new byte[0];
            else {
                bits = new BitSet(bools.size());
                for (int i = 0; i < bools.size(); i++) {
                    if (bools.get(i)) {
                        bits.set(i);
                    }
                }
                bytes = bits.toByteArray();
                //Если байт состоит только из нулей bitset окажется пустым
                if (bytes.length == 0 && modes.size() != 0) bytes = new byte[]{(byte) 0};
            }
        } else {
            bytes = bits.toByteArray();
        }
        return bytes;
    }

}
