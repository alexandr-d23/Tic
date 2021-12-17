package hemming.encoder;

import common.Encoder;
import common.UtilClass;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HemmingEncoder implements Encoder {

    @Override
    public void encode(Path source, Path encoded) {
        //биты, которые не поместились в байт
        try (BufferedWriter writer = Files.newBufferedWriter(encoded, StandardCharsets.UTF_8)) {
            InputStream byteReader = Files.newInputStream(source);
            int res = 0;
            StringBuilder encodedString;
            while (res != -1) {
                byte[] buffer = new byte[255];
                res = byteReader.read(buffer);
                if (res == -1) break;
                boolean[] bits = UtilClass.bytesToBitsArray(buffer, res);
                writer.write(UtilClass.bitsToString(getEncodedBits(buffer, res)));
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Boolean> getEncodedBits(byte[] bytes, int res) {
        boolean[] bits = UtilClass.bytesToBitsArray(bytes, res);
        List<Boolean> listBitsResult = new ArrayList<>();
//        System.out.println("BeforeBitsSize: " + bits.length);
        int k = 0;
        List<Boolean> sendedList = new ArrayList<>();
        for (int i = 0; i < bits.length; i++) {
            sendedList.add(bits[i]);
            if (i % 4 == 3) {
                listBitsResult.addAll(getEncodedBlock(sendedList));
                sendedList = new ArrayList<>();
            }
            //отправляем каждые 4 символа на кодировку
        }
        return listBitsResult;
    }

    public List<Boolean> getEncodedBlock(List<Boolean> bits) {
        List<Boolean> result = new ArrayList<>(bits);
        int a = bits.get(0) ? 1 : 0;
        int b = bits.get(1) ? 1 : 0;
        int c = bits.get(2) ? 1 : 0;
        int d = bits.get(3) ? 1 : 0;
        result.add((a + b + c) % 2 == 1);
        result.add((a + b + d) % 2 == 1);
        result.add((a + c + d) % 2 == 1);
        return result;
    }
}
