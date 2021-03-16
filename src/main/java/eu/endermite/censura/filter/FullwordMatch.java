package eu.endermite.censura.filter;

public class FullwordMatch implements MatchType {
    private final char[] snippetChars;

    public FullwordMatch(String input) {
        this.snippetChars = (" "+input+" ").toCharArray();
    }

// The snippet must start and end on a word boundary. Besides that the snippet may be interrupted by any non-alphabetic character. A character may also be repeated.
//
// When matching "test":
// "test": true
// "aa test aa": true
// "aatestaa": false
// "aa te st aa" true
// "aa t^^e0s--t aa" true
// "teeessstttt" true

    @Override
    public boolean match(String message, FilterCache cache) {
        int state = 1;

        for (char c : message.toCharArray()) {
            if (state >= snippetChars.length) {
                return true;
            }
            if (compare(c, snippetChars[state])) {
                state++;
            } else if (isSpacer(c)) {
                continue;
            } else if (state > 0 && compare(c, snippetChars[state-1])) {
                continue;
            } else {
                state = 0;
            }
        }

        return state >= snippetChars.length-1;
    }

    private static boolean isSpacer(char c) {
        return !Character.isAlphabetic(c);
    }

    private static boolean compare(char a, char b) {
        if (b == '*') return true;
        if (b == ' ') return isSpacer(a);
        return a == b;
    }

    @Override
    public String getSnippet() {
        return new String(snippetChars);
    }

    @Override
    public String getType() {
        return "fullword";
    }
}