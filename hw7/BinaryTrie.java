
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by vip on 4/27/17.
 */
public class BinaryTrie implements Serializable {
    TrieNode root;

    private class TrieNode implements Comparable, Serializable {
        private char ch;
        private int freq;
        private TrieNode left, right;

        TrieNode(char c, int frequ, TrieNode x, TrieNode y) {
            ch = c;
            freq = frequ;
            left = x;
            right = y;
        }

        public int compareTo(Object o) {
            TrieNode x = (TrieNode) o;
            return this.freq - x.freq;
        }
    }

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        PriorityQueue<TrieNode> fringe = new PriorityQueue<>();
        for (Character c : frequencyTable.keySet()) {
            TrieNode toadd = new TrieNode(c, frequencyTable.get(c), null, null);
            fringe.add(toadd);
        }
        while (!fringe.isEmpty()) {
            TrieNode first = fringe.poll();
            TrieNode second = fringe.poll();
            if (second != null) {
                TrieNode bigger = new TrieNode('*', first.freq + second.freq, first, second);
                fringe.add(bigger);
            } else {
                root = first;
            }
        }
    }

    public Match longestPrefixMatch(BitSequence querySequence) {
        int index = 0;
        TrieNode temp = root;
        while (temp.ch == '*') {
            if (querySequence.bitAt(index) == 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
            index = index + 1;
        }
        return new Match(querySequence.firstNBits(index), temp.ch);
    }

    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> result = new HashMap<>();
        helpBuild(root, result, "");
        return result;
    }

    public void helpBuild(TrieNode x, Map y, String z) {
        if (x.ch == '*') {
            helpBuild(x.left, y, z + "0");
            helpBuild(x.right, y, z + "1");
        } else {
            y.put(x.ch, new BitSequence(z));
        }
    }
}
