package uk.co.ionas.roitrack.util;

import java.util.ArrayList;
import java.util.List;

public class Strings {

	public static List<String> toList(String str) {
		List<String> list = null;
		if (!isNotNullOrEmpty(str)) return list;
		list = new ArrayList<String>();
		for(String token : str.split(",")) {
			if (isNotNullOrEmpty(token)) {
				list.add(token.trim());
			}
		}
		return list;
	}
	
	public static boolean isNotNullOrEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}
}
