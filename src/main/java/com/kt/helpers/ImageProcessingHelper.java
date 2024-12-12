package com.kt.helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessingHelper {

	/* Skaidome paveikslėlį į vektorius
	 * @param img - paveikslėlio objektas
	 * @param vectorLength - vektoriaus ilgis
	 * @return - grąžina paveikslėlio vektorių sąrašą
	 */
	public static List<String> splitImageToVectors(BufferedImage img, int vectorLength) {
		List<String> vectors = new ArrayList<>();
		StringBuilder binaryString = new StringBuilder();

		// Iteruoja per kiekvieną pikselį ir ištraukia RGB reikšmes
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int pixel = img.getRGB(x, y);
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;

				// Konvertuojame RGB reikšmes į 8 bitų binarinius skaičius ir pridedame prie binaryString
				// Užpildome nuliais, tuščias vietas iki 8 bitų
				binaryString.append(String.format("%8s", Integer.toBinaryString(red)).replace(' ', '0'));
				binaryString.append(String.format("%8s", Integer.toBinaryString(green)).replace(' ', '0'));
				binaryString.append(String.format("%8s", Integer.toBinaryString(blue)).replace(' ', '0'));
			}
		}

		// Skaidome binaryString į vektorius su nurodytu ilgiu
		for (int i = 0; i < binaryString.length(); i += vectorLength) {
			String vector = binaryString.substring(i, Math.min(i + vectorLength, binaryString.length()));
			while (vector.length() < vectorLength) {
				vector += "0"; // Užpildome nuliais, jei reikia (labiau aktualu paskutiniam vektoriui)
			}
			vectors.add(vector);
		}

		return vectors;
	}

	/* Rekonstruoja paveikslėlį iš vektorių
	 * @param vectors - vektorių sąrašas
	 * @param width - paveikslėlio plotis
	 * @param height - paveikslėlio aukštis
	 * @return - grąžina paveikslėlio objektą
	 */
	public static BufferedImage reconstructImageFromVectors(List<String> vectors, int width, int height) {
		StringBuilder binaryString = new StringBuilder();

		// Kombinuojame visus vektorius į vieną binarinį string'ą
		for (String vector : vectors) {
			binaryString.append(vector);
		}

		// Sukuuriamas tuščias RGB paveikslėlis
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (index + 24 <= binaryString.length()) {
					// Ištraukiamos 8 bitų binarinės eilutės raudonos, žalios ir mėlynos spalvos reikšmės
					String redString = binaryString.substring(index, index + 8);
					String greenString = binaryString.substring(index + 8, index + 16);
					String blueString = binaryString.substring(index + 16, index + 24);

					int red = Integer.parseInt(redString, 2);
					int green = Integer.parseInt(greenString, 2);
					int blue = Integer.parseInt(blueString, 2);

					// Kuriamas RGB pikselis
					int rgb = (red << 16) | (green << 8) | blue;
					img.setRGB(x, y, rgb);

					index += 24;
				} else {
					// Jei nepakanka bitų, užpildome nuliais
					img.setRGB(x, y, 0);
				}
			}
		}

		return img;
	}

	/* Konvertuoja Image į BufferedImage
	 * @param img - Image objektas
	 * @return - grąžina BufferedImage objektą
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Sukuuriamas BufferedImage su tuo pačiu dydžiu kaip ir Image
		BufferedImage bufferedImage = new BufferedImage(
				img.getWidth(null),
				img.getHeight(null),
				BufferedImage.TYPE_INT_RGB // Užtikrina, kad paveikslėlis būtų RGB formatu
		);

		// Nupiešiame Image ant BufferedImage
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		return bufferedImage;
	}
}