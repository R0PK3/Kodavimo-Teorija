package com.kt.helpers;

/* Generuojame Reed-Muller kodavimo matricą
 @param m - m reikšmė
 @return - grąžina kodavimo matricą
 */
public class MatrixGenerator {
	public static int[][] generateEncodingMatrix(int m) {
		// Matricos dydis: m + 1 eilutė, 2^m stulpelių
		int rows = m + 1;
		int cols = (int) Math.pow(2, m);

		// Matrica, kurioje saugomi duomenys
		int[][] matrix = new int[rows][cols];

		// Pirmoji eilutė yra užpildoma vienetais
		for (int j = 0; j < cols; j++) {
			matrix[0][j] = 1;
		}

		// Pildomos likusios eilutės ir stulpeliai
		// Iteruojame per kiekvieną eilutę nuo antros
		// Periodas, kas kiek stulpelių keičiasi reikšmė
		// Iteruojame per kiekvieną stulpelį
		// Keičiame reikšmę pagal periodą
		for (int i = 1; i <= m; i++) {
			int period = (int) Math.pow(2, i - 1);
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = (j / period) % 2;
			}
		}

		return matrix;
	}
}