import java.util.ArrayList;

/**
 * Created by vip on 4/27/17.
 */
public class HuffmanDecoder {
    public static void main(String[] args) {
        ObjectReader or = new ObjectReader(args[0]);
        BinaryTrie trie = (BinaryTrie) or.readObject();
        BitSequence hugebits = (BitSequence) or.readObject();
        ArrayList<Match> matchlist = new ArrayList<>();
        while (hugebits != null && hugebits.length() != 0) {
            Match matched = trie.longestPrefixMatch(hugebits);
            matchlist.add(matched);
            BitSequence getout = matched.getSequence();
            int index = getout.length();
            hugebits = hugebits.allButFirstNBits(index);
        }
        char[] result = new char[matchlist.size()];
        for (int i = 0; i < matchlist.size(); i++) {
            result[i] = matchlist.get(i).getSymbol();
        }
        FileUtils.writeCharArray(args[1], result);
    }
}
