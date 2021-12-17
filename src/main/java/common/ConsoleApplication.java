package common;

import arithmetic.decoder.ArithmeticDecoder;
import arithmetic.encoder.ArithmeticEncoder;
import bwtmtf.decoder.BWTDecoder;
import bwtmtf.encoder.BWTEncoder;
import hemming.decoder.HemmingDecoder;
import hemming.encoder.HemmingEncoder;
import huffman.decoder.HuffmanDecoder;
import huffman.encoder.HuffmanEncoder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(
                        "Enter digit to choose algorithm:\n" +
                                "1)Huffman\n" +
                                "2)Arithmetic\n" +
                                "3)BwtMtf\n" +
                                "4)Hemming\n\n\n" +
                                "0)Exit\n"
                );
                int algorithmChoice = Integer.parseInt(scanner.nextLine().trim());
                if (algorithmChoice == 0) return;

                System.out.println(
                        "1)Encode\n" +
                                "2)Decode\n"
                );

                int actionChoice = Integer.parseInt(scanner.nextLine().trim());
                if (actionChoice != 1 && actionChoice != 2) return;
                boolean isEncode = actionChoice == 1;
                switch (algorithmChoice) {
                    //Huffman
                    case 1: {
                        if (isEncode) encode(new HuffmanEncoder(), scanner);
                        else decode(new HuffmanDecoder(), scanner);
                        break;
                    }
                    //Arithmetic
                    case 2: {
                        if (isEncode) encode(new ArithmeticEncoder(), scanner);
                        else decode(new ArithmeticDecoder(), scanner);
                        break;
                    }
                    //BwtMtf
                    case 3: {
                        if (isEncode) encode(new BWTEncoder(), scanner);
                        else decode(new BWTDecoder(), scanner);
                        break;
                    }
                    //Hemming
                    case 4: {
                        if (isEncode) encode(new HemmingEncoder(), scanner);
                        else decode(new HemmingDecoder(), scanner);
                    }
                    default:
                        return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void encode(Encoder encoder, Scanner scanner) {
        System.out.println("\nEnter source file path\n");
        String fileSourcePath = scanner.nextLine().trim();
        Path source = Paths.get(fileSourcePath);
        System.out.println("Enter encode file path");
        String fileEncodePath = scanner.nextLine().trim();
        Path encode = Paths.get(fileEncodePath);
        encoder.encode(source, encode);
        System.out.println("\nCompleted\n");
    }

    private static void decode(Decoder decoder, Scanner scanner) {
        System.out.println("\nEnter encode file path\n");
        String fileEncodePath = scanner.nextLine().trim();
        Path encode = Paths.get(fileEncodePath);
        System.out.println("Enter decode file path");
        String fileDecodePath = scanner.nextLine().trim();
        Path decode = Paths.get(fileDecodePath);
        decoder.decode(encode, decode);
        System.out.println("\nCompleted\n");
    }
}
