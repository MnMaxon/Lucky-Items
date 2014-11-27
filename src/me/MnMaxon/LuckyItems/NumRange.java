package me.MnMaxon.LuckyItems;

public class NumRange {
	public double min;
	public double max;

	public NumRange(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public boolean contains(double num) {
		if (num > this.min && num <= this.max)
			return true;
		return false;
	}
}
