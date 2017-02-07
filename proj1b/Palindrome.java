public class Palindrome {
    public static Deque<Character> wordToDeque(String word) {
        Deque<Character> result = new ArrayDequeSolution<Character>();
        for (int i = 0; i < word.length(); i++) {
            result.addLast(word.charAt(i));
        }
        return result;
    }

    public static boolean isPalindrome(String word) {
        if (word.length() == 0 || word.length() == 1) {
            return true;
        } else if (word.charAt(0) != word.charAt(word.length() - 1)) {
            return false;
        } else {
            return isPalindrome(word.substring(1, word.length() - 1));
        }
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        if (word.length() == 0 || word.length() == 1) {
            return true;
        } else if (!cc.equalChars(word.charAt(0), word.charAt(word.length() - 1))) {
            return false;
        } else {
            return isPalindrome(word.substring(1, word.length() - 1), cc);
        }
    }
}
