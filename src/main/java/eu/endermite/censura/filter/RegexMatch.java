package eu.endermite.censura.filter;

import java.util.regex.Pattern;

public class RegexMatch implements MatchType {
    private final Pattern pattern;

    public RegexMatch(String input) {
        this.pattern = Pattern.compile(input);
    }

    @Override
    public boolean match(String message, FilterCache cache) {
        return pattern.matcher(message).matches();
    }
}
