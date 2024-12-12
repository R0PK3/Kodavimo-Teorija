package com.kt.encoding;

import com.kt.helpers.MatrixGenerator;

public class Encoder {
	private final int m;
	private final int[][] encodingMatrix;

	// Konstruktorius, kuris nustato m reikšmę ir sugeneruoja kodavimo matricą
	public Encoder(int m) {
		this.m = m;
		this.encodingMatrix = MatrixGenerator.generateEncodingMatrix(m);
	}

	/* Koduojame naudojant Reed-Muller matricą
		@param data - pradinis vektorius
		@return - grąžina užkoduotą vektorių
	 */
	public String encode(String data) {
		int[] inputBits = new int[data.length()];
		// Konvertuojame įvestį į masyvą
		for (int i = 0; i < data.length(); i++) {
			// Konvertuojame simbolį į skaičių
			inputBits[i] = data.charAt(i) - '0';
		}

		int cols = (int) Math.pow(2, m);
		int[] encodedBits = new int[cols];

		// Masyvo elementus dauginame su matrica ir sudedame
		for (int j = 0; j < cols; j++) {
			int sum = 0;
			for (int i = 0; i < m + 1; i++) {
				sum += inputBits[i] * encodingMatrix[i][j];
			}
			encodedBits[j] = sum % 2; // mod 2
		}

		// Konvertuojame užkoduotus bitus į simbolius (kadangi nurodytas String tipo grąžinimas)
		StringBuilder encodedData = new StringBuilder();
		for (int bit : encodedBits) {
			encodedData.append(bit);
		}

		return encodedData.toString();
	}

	// Parodoma kodavimo matrica
	public void displayMatrix() {
		System.out.println("Reed-Muller (1, " + m + ") encoding matrix:");
		for (int[] row : encodingMatrix) {
			for (int value : row) {
				System.out.print(value + " ");
			}
			System.out.println();
		}
	}
}