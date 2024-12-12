package com.kt.decoding;

import java.util.Arrays;

public class Decoder {

	private final int m;
	private final int[][][] hadamardMatrices;

	/* Konstruktorius, kuris nustato m reikšmę ir Hadamardo matricas
		@param m - m reikšmė
		@param hadamardMatrices - Hadamardo matricos
	 */
	public Decoder(int m, int[][][] hadamardMatrices) {
		this.m = m;
		this.hadamardMatrices = hadamardMatrices;
	}
	/*
		@param receivedData - gauti duomenys
		@return - grąžina dekoduotą žinutę
	 */
	public String decode(String receivedData) {
		int n = (int) Math.pow(2, m);
		double[] w = new double[n];

		// Keičiame 0 į -1
		for (int i = 0; i < receivedData.length(); i++) {
			w[i] = receivedData.charAt(i) == '0' ? -1 : 1;
		}
		System.out.println("Initial w vector: " + Arrays.toString(w));

		// Dauginame w_i su atitinkamomis Hadamardo matricomis
		double[] currentW = w;
		for (int i = 0; i < m; i++) {
			currentW = multiplyVectorWithMatrix(currentW, hadamardMatrices[i]);
			System.out.println("w_" + (i + 1) + ": " + Arrays.toString(currentW));
		}

		// Ieškome didžiausio komponento pozicijos (absoliučios reikšmės)
		int maxIndex = 0;
		double maxValue = Math.abs(currentW[0]);
		for (int i = 1; i < currentW.length; i++) {
			if (Math.abs(currentW[i]) > maxValue) {
				maxValue = Math.abs(currentW[i]);
				maxIndex = i;
			}
		}
		System.out.println("Max component at index: " + maxIndex + ", value: " + currentW[maxIndex]);

		// Konvertuojame indeksą į dvejetainę reikšmę
		String binaryRepresentation = Integer.toBinaryString(maxIndex);
		while (binaryRepresentation.length() < m) {
			// Užpildome nuliais iki m bitų
			binaryRepresentation = "0" + binaryRepresentation;
		}
		// Apverčiame dvejetainę reikšmę
		binaryRepresentation = new StringBuilder(binaryRepresentation).reverse().toString();
		System.out.println("Binary representation of index (low-order digits first): " + binaryRepresentation);

		// Nustatome bei prirašome ženklą didžiausio komponento reikšmei (teigiamas 1, neigiamas 0)
		String message = (currentW[maxIndex] > 0 ? "1" : "0") + binaryRepresentation;

		return message;
	}

	/* Dauginame vektorių iš matricos
		@param vector - vektorius
		@param matrix - Hadamardo matrica
		@return - grąžina rezultatą
	 */
	private double[] multiplyVectorWithMatrix(double[] vector, int[][] matrix) {
		int n = matrix[0].length;
		double[] result = new double[n];

		for (int j = 0; j < n; j++) {
			for (int i = 0; i < vector.length; i++) {
				result[j] += vector[i] * matrix[i][j];
			}
		}
		return result;
	}
}
