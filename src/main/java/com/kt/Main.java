package com.kt;

import com.kt.encoding.Encoder;
import com.kt.channel.ChannelSimulator;
import com.kt.decoding.Decoder;
import com.kt.helpers.HadamardMatrixGenerator;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		Random randomValue = new Random();

		int m;

		while (true) {
			System.out.println("Enter the value of m (positive integer, m >= 1): ");
			String input = scanner.nextLine();
			if (isValidPositiveInteger(input)) {
				m = Integer.parseInt(input);
				if (m >= 1) {
					break;
				} else {
					System.out.println("Error: m must be at least 1.");
				}
			} else {
				System.out.println("Error: enter a positive integer.");
			}
		}

		int vectorLength = m + 1;
		String vector;

		while (true) {
			System.out.println("Enter a binary vector of length " + vectorLength + ": ");
			vector = scanner.nextLine();
			if (isValidBinaryVector(vector, vectorLength)) {
				break;
			} else {
				System.out.println("Error: enter a binary vector (only 0 and 1, length " + vectorLength + ").");
			}
		}

		double errorProbability;
		while (true) {
			System.out.println("Enter the error probability (0 <= p <= 1, e.g., 0.1): ");
			String input = scanner.nextLine();
			if (isValidProbability(input)) {
				errorProbability = Double.parseDouble(input);
				break;
			} else {
				System.out.println("Error: enter a real number between 0 and 1.");
			}
		}

		int[][][] hadamardMatrices = HadamardMatrixGenerator.generateHadamardMatrices(m);
		HadamardMatrixGenerator.printHadamardMatrices(hadamardMatrices);

		Encoder encoder = new Encoder(m);
		encoder.displayMatrix();
		String encodedData = encoder.encode(vector);
		System.out.println("Encoded data: " + encodedData);

		ChannelSimulator channelSimulator = new ChannelSimulator(errorProbability, randomValue);
		String transmittedData = channelSimulator.simulateChannel(encodedData);
		System.out.println("Data after transmission through the channel: " + transmittedData);

		List<Integer> errorPositions = channelSimulator.getErrorPositions();
		System.out.println("Number of errors: " + errorPositions.size());
		System.out.println("Error positions: " + errorPositions);

		String userEditedData;
		while (true) {
			System.out.println("Edit the transmitted vector (binary vector of length " + transmittedData.length() + ") or press Enter to keep it unchanged: ");
			System.out.println("Original: " + transmittedData);
			String input = scanner.nextLine();
			if (input.isEmpty()) {
				userEditedData = transmittedData;
				break;
			} else if (isValidBinaryVector(input, transmittedData.length())) {
				userEditedData = input;
				break;
			} else {
				System.out.println("Error: enter a binary vector (only 0 and 1, length " + transmittedData.length() + ").");
			}
		}

		Decoder decoder = new Decoder(m, hadamardMatrices);
		String decodedData = decoder.decode(userEditedData);
		System.out.println("Decoded data: " + decodedData);
	}

	private static boolean isValidPositiveInteger(String input) {
		try {
			int value = Integer.parseInt(input);
			return value > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isValidBinaryVector(String vector, int requiredLength) {
		if (vector.length() != requiredLength) {
			return false;
		}
		for (char c : vector.toCharArray()) {
			if (c != '0' && c != '1') {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidProbability(String input) {
		try {
			double value = Double.parseDouble(input);
			return value >= 0 && value <= 1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
