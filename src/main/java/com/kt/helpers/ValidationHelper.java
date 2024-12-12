package com.kt.helpers;

public class ValidationHelper {
	/* Patikrina, ar įvestas skaičius yra teigiamas sveikasis skaičius didesnis arba lygus 1
	@param input - įvestas skaičius
	@return - grąžina true, jei skaičius yra teigiamas sveikasis skaičius didesnis arba lygus 1, kitu atveju - false
	 */
	public static boolean isPositiveInteger(String input) {
		try {
			int value = Integer.parseInt(input);
			return value >= 1;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/* Patikrina, ar įvestas skaičius telpa į intervalą [0; 1]
	@param input - įvestas skaičius
	@return - grąžina true, jei skaičius telpa į intervalą [0; 1], kitu atveju - false
	 */
	public static boolean isProbability(String input) {
		try {
			double value = Double.parseDouble(input);
			return value >= 0 && value <= 1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	/*
	@param vector - vektorius
	@param requiredLength - reikalingas vektoriaus ilgis
	@return - grąžina true, jei vektorius yra tinkamo ilgio ir sudarytas tik iš 0 ir 1, kitu atveju - false
	 */
	public static boolean isValidBinaryVector(String vector, int requiredLength) {
		// Patikrina, ar vektorius yra tinkamo ilgio
		if (vector.length() != requiredLength) {
			return false;
		}

		// Patikrina, ar vektorius yra sudarytas tik iš 0 ir 1
		for (char c : vector.toCharArray()) {
			if (c != '0' && c != '1') {
				return false;
			}
		}
		return true;
	}
}