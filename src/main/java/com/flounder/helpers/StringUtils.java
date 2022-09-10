package com.flounder.helpers;

public class StringUtils {
	public static int findCharPos(String line, char c) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == c) {
				return i;
			}
		}

		return 0;
	}
}
