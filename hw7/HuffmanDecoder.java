import java.util.ArrayList;

/**
 * Created by vip on 4/27/17.
 */
public class HuffmanDecoder {
    public static void main(String[] args) {
        ObjectReader or = new ObjectReader(args[0]);
        ObjectWriter ow = new ObjectWriter(args[1]);
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
        ow.writeObject(matchlist);
    }
}
