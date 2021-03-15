package eu.endermite.censura.filter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.regex.Pattern;

public class ContainMatch implements MatchType{
	private final String snippet;

	public ContainMatch(String input) {
		this.snippet = input;
	}

	@Override
	public boolean match(String message) {
		return false;
	}
}
