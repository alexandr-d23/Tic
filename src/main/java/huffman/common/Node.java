package huffman.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private Node leftChild;
    private Node rightChild;
    private Character word;
    private Integer probability;
}
