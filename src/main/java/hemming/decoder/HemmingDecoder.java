package hemming.decoder;

import common.Decoder;
import common.UtilClass;
import hemming.common.HemmingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HemmingDecoder implements Decoder {

    private int mistakesCount = 0;

    @Override
    public void decode(Path encoded, Path decoded) {
        mistakesCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(encoded, StandardCharsets.UTF_8);
             OutputStream writer = Files.newOutputStream(decoded);
        ) {
            char[] buffer = new char[14];
            int readedCount;
            readedCount = reader.read(buffer);
            while (readedCount != -1) {
                writer.write(decodeBlock(buffer));
                readedCount = reader.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] decodeBlock(char[] charBits) {
        List<Boolean> bits = new ArrayList<>();
        for (int i = 0; i < charBits.length; i++) {
            bits.add(charBits[i] == '1');
        }
        return UtilClass.bitsToByte(resolveErrors(bits));
    }

    public List<Boolean> resolveErrors(List<Boolean> source) {
        List<Boolean> resultList = new ArrayList<>();
        for (int i = 0; i < source.size(); i += 7) {
            HemmingResult res = HemmingResult.builder()
                    .A(source.get(i))
                    .B(source.get(i + 1))
                    .C(source.get(i + 2))
                    .D(source.get(i + 3))
                    .build();
            int a = booleanToDigit(res.isA());
            int b = booleanToDigit(res.isB());
            int c = booleanToDigit(res.isC());
            int d = booleanToDigit(res.isD());
            int x = booleanToDigit(source.get(i + 4));
            int y = booleanToDigit(source.get(i + 5));
            int z = booleanToDigit(source.get(i + 6));
            int x1 = calculateAddingBit(a, b, c);
            int y1 = calculateAddingBit(a, b, d);
            int z1 = calculateAddingBit(a, c, d);
            findAndSolveMistakes(res, x, x1, y, y1, z, z1);
            resultList.addAll(res.toList());
        }
        return resultList;
    }

    private int booleanToDigit(boolean b) {
        return b ? 1 : 0;
    }

    private int calculateAddingBit(int first, int second, int third) {
        return (first + second + third) % 2 == 1 ? 1 : 0;
    }

    private void findAndSolveMistakes(HemmingResult res, int x, int x1, int y, int y1, int z, int z1) {
        int mistakes = 0;
        boolean xComp = x != x1;
        boolean yComp = y != y1;
        boolean zComp = z != z1;
        if (xComp) mistakes++;
        if (yComp) mistakes++;
        if (zComp) mistakes++;
        if (mistakes > 0) mistakesCount++;
        if (mistakes >= 2) {
            if (mistakes == 3) {
                res.reverseA();
            } else {
                if (xComp && yComp) res.reverseB();
                if (xComp && zComp) res.reverseC();
                if (yComp && zComp) res.reverseD();
            }
        }
    }

}
