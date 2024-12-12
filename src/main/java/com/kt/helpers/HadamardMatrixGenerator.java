package com.kt.helpers;

import java.util.Arrays;

// Generuojame Hadamardo matricas
public class HadamardMatrixGenerator {

	/* Pradinė Hadamardo matrica H
	 @param m - m reikšmė
	 @return - grąžina Hadamardo matricas
	 */
	public static int[][][] generateHadamardMatrices(int m) {
		int[][] H = {
				{1, 1},
				{1, -1}
		};

		int[][][] hadamardMatrices = new int[m][][];
		for (int i = 1; i <= m; i++) {
			hadamardMatrices[i - 1] = generateIntermediateMatrix(m, i, H);
		}
		return hadamardMatrices;
	}

	/* Generuojame tarpines Hadamardo matricas
	 @param m - m reikšmė
	 @param i - i reikšmė
	 @param H - Hadamardo matrica
	 */
	private static int[][] generateIntermediateMatrix(int m, int i, int[][] H) {
		int[][] I_m_i = generateIdentityMatrix((int) Math.pow(2, m - i));
		int[][] I_i_1 = generateIdentityMatrix((int) Math.pow(2, i - 1));
		return kroneckerProduct(kroneckerProduct(I_m_i, H), I_i_1);
	}

	/* Generuojame vienetines matricas
	/ Pildome įstrižainę vienetais
	@param n - matricos dydis
	@return - grąžina vienetinę matricą
	 */
	private static int[][] generateIdentityMatrix(int n) {
		int[][] identity = new int[n][n];
		for (int i = 0; i < n; i++) {
			identity[i][i] = 1;
		}
		return identity;
	}

	/* Skaičiuojame dviejų matricų Kroneckerio produktą (sąndaugą)
	@param A - matrica A
	@param B - matrica B
	@return - grąžina Kroneckerio sąndaugą
	 */
	private static int[][] kroneckerProduct(int[][] A, int[][] B) {
		int rowsA = A.length, colsA = A[0].length;
		int rowsB = B.length, colsB = B[0].length;

		int[][] result = new int[rowsA * rowsB][colsA * colsB];

		// Iteruojame per matricas A ir B eilutes ir stulpelius bei atliekame Kroneckerio sąndaugą
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsA; j++) {
				for (int k = 0; k < rowsB; k++) {
					for (int l = 0; l < colsB; l++) {
						result[i * rowsB + k][j * colsB + l] = A[i][j] * B[k][l];
					}
				}
			}
		}
		return result;
	}

	// Spausdiname Hadamardo matricas
	public static void printHadamardMatrices(int[][][] hadamardMatrices) {
		for (int i = 0; i < hadamardMatrices.length; i++) {
			System.out.println("Hadamard matrix H^" + (i + 1) + "_" + hadamardMatrices.length + ":");
			for (int[] row : hadamardMatrices[i]) {
				System.out.println(Arrays.toString(row));
			}
			System.out.println();
		}
	}
}