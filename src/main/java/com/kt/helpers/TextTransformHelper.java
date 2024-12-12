package com.kt.helpers;

import java.util.ArrayList;
import java.util.List;

public class TextTransformHelper {

	/* Teksto skaidymas į vektorius
	@param text - tekstas
	@param vectorLength - vektoriaus ilgis
	@return - grąžina vektorių sąrašą
	 */
	public static List<String> splitTextIntoVectors(String text, int vectorLength) {
		List<String> vectors = new ArrayList<>();
		StringBuilder binaryString = new StringBuilder();

		// Konvertuojame kiekvieną simbolį į 16 bitų (UTF-16) binarinį atvaizdavimą ir pridedame prie binaryString
		//UTF-16 naudojamas lietuviškų simbolių atvaizdavimui
		for (char c : text.toCharArray()) {
			//Pridedame trūkstamus nulius iki 16 simbolių
			String binary = String.format("%16s", Integer.toBinaryString(c)).replace(' ', '0');
			binaryString.append(binary);
		}

		// Atskiriame binaryString į vektorius su nurodytu ilgiu
		for (int i = 0; i < binaryString.length(); i += vectorLength) {
			// Vektorių skaidome į vectorLength ilgio segmentus (m+1)
			String vector = binaryString.substring(i, Math.min(i + vectorLength, binaryString.length()));
			// Jei vektorius yra per trumpas, pridedame nulius (labiau aktualu paskutiniam vektoriui)
			while (vector.length() < vectorLength) {
				vector += "0"; // Pildome nuliais, jei reikia
			}
			vectors.add(vector);
		}

		return vectors;
	}


	/* Rekonstruojame vektorius į tekstą
	@param vectors - vektorių sąrašas
	@return - grąžina rekonstruotą tekstą
	 */
	public static String reconstructTextFromVectors(List<String> vectors) {
		StringBuilder binaryString = new StringBuilder();

		// Sudedame visus vektorius į vieną binarinį string'ą
		for (String vector : vectors) {
			binaryString.append(vector);
		}

		StringBuilder text = new StringBuilder();

		// Konvertuojame kiekvieną 16 bitų segmentą į simbolį
		for (int i = 0; i < binaryString.length(); i += 16) {
			String byteString = binaryString.substring(i, Math.min(i + 16, binaryString.length()));
			// Konvertuojame į binarinį skaičių ir iš jo į char simbolį
			int charCode = Integer.parseInt(byteString, 2);
			text.append((char) charCode);
		}

		return text.toString();
	}
}
