/**
 * Created by vip on 4/27/17.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HuffmanEncoder {
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> result = new HashMap<>();
        for (char element : inputSymbols) {
            if (result.containsKey(element)) {
                result.put(element, result.get(element) + 1);
            } else {
                result.put(element, 1);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ObjectWriter ow = new ObjectWriter(args[0] + ".huf");
        char[] charseq = FileUtils.readFile(args[0]);
        Map<Character, Integer> frequencytable = buildFrequencyTable(charseq);
        BinaryTrie wordtrie = new BinaryTrie(frequencytable);
        ow.writeObject(wordtrie);
        Map<Character, BitSequence> lookuptable = wordtrie.buildLookupTable();
        ArrayList<BitSequence> bitSequences = new ArrayList<>();
        for (char symbol : charseq) {
            bitSequences.add(lookuptable.get(symbol));
        }
        BitSequence huge = BitSequence.assemble(bitSequences);
        ow.writeObject(huge);
    }

}
