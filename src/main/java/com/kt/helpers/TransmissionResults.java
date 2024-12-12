package com.kt.helpers;

import java.util.ArrayList;
import java.util.List;

// Eksperimentas
public class TransmissionResults {
	private final List<Integer> mValues = new ArrayList<>();
	private final List<Long> durations = new ArrayList<>();

	public void addResult(int m, long duration) {
		mValues.add(m);
		durations.add(duration);
	}

	public List<Integer> getMValues() {
		return mValues;
	}

	public List<Long> getDurations() {
		return durations;
	}
}