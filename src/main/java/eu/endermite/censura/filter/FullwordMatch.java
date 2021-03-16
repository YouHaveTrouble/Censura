package eu.endermite.censura.filter;

public class FullwordMatch implements MatchType {
    private final char[] snippetChars;

    public FullwordMatch(String input) {
        this.snippetChars = input.toCharArray();
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
        int state = 0;
        boolean wasSpacer = true; //start of string counts as spacer

        for (char c : message.toCharArray()) {
            if (state >= snippetChars.length) {
                if (isSpacer(c)) {
                    return true; //We've reached the end and reached a spacer
                }
            } else if (match(c, snippetChars[state])) {
                if (state == 0) {
                    //Can only match the first letter of the snippet after a space
                    if (wasSpacer) state++;
                } else {
                    state++;
                }
            }
            if (state > 0 && !match(c, snippetChars[state - 1]) && !isSpacer(c)) {
                //This is not a repeated character. We should reset
                state = 0;
            }

            wasSpacer = isSpacer(c);
        }

		return state >= snippetChars.length;
	}

    private static boolean isSpacer(char c) {
        return !Character.isAlphabetic(c);
    }

    private static boolean match(char a, char b) {
        if (b == '*') return true;
        return a == b;
    }
}
