package eu.endermite.censura.filter;

import javax.annotation.Nullable;

public interface MatchType {
    boolean match(String message, FilterCache cache);

    /**
     * @return the snippet this match is checking for.
     */
    String getSnippet();

    String getType();

    @Nullable
    static MatchType fromString(@Nullable String type, String input) {
        if (type == null) return new FullwordMatch(input);
        switch (type) {
            case "contain":
                return new ContainMatch(input);
            case "regex":
                return new RegexMatch(input);
            case "fullword":
                return new FullwordMatch(input);
            default:
                return null;
        }
    }
}
