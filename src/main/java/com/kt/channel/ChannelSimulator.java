package com.kt.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChannelSimulator {
	private final double errorProbability;
	private final Random random;
	private final List<Integer> errorPositions;

	/* Konstruktorius, kuriame nustatome klaidos tikimybę ir atsitiktinį generatorių
		@param errorProbability - klaidos tikimybė
		@param randomValue - atsitiktinė generatoriaus reikšmė
		@return - grąžina modifikuotą vektorių
	*/
	public ChannelSimulator(double errorProbability, Random randomValue) {
		this.errorProbability = errorProbability;
		this.random = randomValue; // Naudojame perduotą generatorių
		this.errorPositions = new ArrayList<>();
	}

	// Simuliuojame blogą kanalą
	public String simulateChannel(String data) {
		StringBuilder transmittedData = new StringBuilder();
		errorPositions.clear();

		for (int i = 0; i < data.length(); i++) {
			char bit = data.charAt(i);
			double randomValue = random.nextDouble();
			if (randomValue < errorProbability) {
				// Apverčiame bitą
				transmittedData.append(bit == '0' ? '1' : '0');
				// Išsaugome klaidingos pozicijos indeksą
				errorPositions.add(i);
			} else {
				// Paliekame originalų bitą
				transmittedData.append(bit);
			}
		}

		return transmittedData.toString();
	}

	// @return - grąžina klaidingų pozicijų sąrašą
	public List<Integer> getErrorPositions() {
		return errorPositions;
	}
}