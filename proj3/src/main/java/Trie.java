/**
 * Created by vip on 4/16/17.
 */


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Trie {
    private TrieNode root;

    private class TrieNode {
        char c;
        HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
        boolean isLeaf;

        TrieNode() {
        }

        TrieNode(char c) {
            this.c = c;
        }
    }

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        HashMap<Character, TrieNode> descendants = root.children;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode x;
            if (descendants.containsKey(c)) {
                x = descendants.get(c);
            } else {
                x = new TrieNode(c);
                descendants.put(c, x);
            }
            descendants = x.children;
            if (i == word.length() - 1)
                x.isLeaf = true;
        }
    }

    public boolean search(String word) {
        TrieNode x = searchNode(word);
        if (x != null && x.isLeaf)
            return true;
        else
            return false;
    }

    public TrieNode searchNode(String str) {
        Map<Character, TrieNode> children = root.children;
        TrieNode t = null;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (children.containsKey(c)) {
                t = children.get(c);
                children = t.children;
            } else {
                return null;
            }
        }
        return t;
    }

    public LinkedList<String> autocompletion(String prefix) {
        LinkedList<String> result = new LinkedList<>();
        HashMap<Character, TrieNode> descendants = root.children;
        char[] prefixparts = prefix.toCharArray();
        TrieNode temp = new TrieNode();
        for (int i = 0; i < prefixparts.length; i++) {
            if (!descendants.containsKey(prefixparts[i])) {
                return new LinkedList<>();
            }
            temp = descendants.get(prefixparts[i]);
            descendants = temp.children;
        }
        completionhelper(temp, result, prefix);
        return result;
    }

    public void completionhelper(TrieNode t, LinkedList<String> x, String prefix) {
        for (Character c : t.children.keySet()) {
            String longer = prefix + c;
            TrieNode next = t.children.get(c);
            if (next.isLeaf) {
                String changed = MapServer.cleansed.get(longer);
                x.add(changed);
                if (next.children.size() != 0) {
                    completionhelper(next, x, longer);
                }
            } else {
                completionhelper(next, x, longer);
            }
        }
    }

    public static void main(String[] args) {
        Trie x = new Trie();
        x.insert("Chaparral");
        x.insert("Chaparral Peak");
        System.out.println(x.search("Chaparral Peak"));
        System.out.println(MapServer.clean("Oak Grove & College"));
    }
}
