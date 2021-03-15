package eu.endermite.censura.filter;

import javax.annotation.Nullable;
import java.text.ParseException;

public interface MatchType {
	boolean match(String message);

	static @Nullable MatchType fromString(@Nullable String type, String input) {
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
