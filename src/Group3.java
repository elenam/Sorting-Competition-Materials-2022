import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;

public class Group3 {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		if (args.length < 2) {
			System.out.println(
					"Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];
		
		// runTests();

		String[] data = readData(inputFileName); // read data as strings
		
		String[] toSort = data.clone(); // clone the data

		sort(toSort); // call the sorting method once for JVM warmup
		
		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		sort(toSort); // sort again

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		writeOutResult(toSort, outFileName); // write out the results

	}

	private static String[] readData(String inputFileName) throws FileNotFoundException {
		ArrayList<String> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			input.add(in.next());
		}

		in.close();

		// the string array is passed just so that the correct type can be created
		return input.toArray(new String[0]);
	}

	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs
	private static void sort(String[] toSort) {
		// Total the number of positive decimals, negative decimals, and rationals in the input
		int positive_decimal_count = 0;
		int negative_decimal_count = 0;
		int rational_count = 0;

		for (String s: toSort) {
			if (s.contains("/")) {
				rational_count++;
			}
			else if (s.charAt(0) == '-') {
				negative_decimal_count++;
			}
			else {
				positive_decimal_count++;
			}
		}

		// Split the input into rational numbers, positive decimal numbers, and negative decimal numbers
		DecimalPair[] positive_decimals = new DecimalPair[positive_decimal_count];
		DecimalPair[] negative_decimals = new DecimalPair[negative_decimal_count];
		RationalPair[] rationals = new RationalPair[rational_count];
		int positive_decimal_index = 0;
		int negative_decimal_index = 0;
		int rational_index = 0;

		for (String s : toSort) {
			if (s.contains("/")) {
				rationals[rational_index++] = new RationalPair(s);
			}
			else if (s.charAt(0) == '-') {
				negative_decimals[negative_decimal_index++] = new DecimalPair(s);
			}
			else {
				positive_decimals[positive_decimal_index++] = new DecimalPair(s);
			}
		}

		// Sort each array of decimals in ascending order (ignoring sign)
		DecimalSorting.sort_decimals(negative_decimals);
		DecimalSorting.sort_decimals(positive_decimals);

		// Recombine the decimals into one array
		DecimalPair[] decimals = new DecimalPair[negative_decimal_count + positive_decimal_count];
		for (int i = 0; i < negative_decimals.length; i++) {
			decimals[i] = negative_decimals[negative_decimal_count - 1 - i];
		}
		for (int i = 0; i < positive_decimals.length; i++) {
			decimals[negative_decimal_count + i] = positive_decimals[i];
		}

		// Sort the rational numbers
		RationalSorting.sort_rationals(rationals);

		// Merge together the two arrays
		int decimal_index = 0;
		    rational_index = 0;

		for (int i = 0; i < toSort.length; i++) {
			if (decimal_index == decimals.length) {
				toSort[i] = rationals[rational_index++].get_string();
			}
			else if (rational_index == rationals.length) {
				toSort[i] = decimals[decimal_index++].get_string();
			}
			else {
				if (rationals[rational_index].compare(decimals[decimal_index]) < 0) {
					toSort[i] = rationals[rational_index++].get_string();
				}
				else {
					toSort[i] = decimals[decimal_index++].get_string();
				}
			}
		}
	}
	
	// Write the results of the sort out to the given file
	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}

	static class BigRational {
		public BigInteger numerator;
		public BigInteger denominator;
	
		static BigInteger ten = new BigInteger("10");
	
		BigRational(BigInteger num, BigInteger denom) {
			this.numerator = num;
			this.denominator = denom;
		}
	
		BigRational(String str) {
			int slash_index = -1;
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == '/') {
					slash_index = i;
					break;
				}
			}
	
			if (slash_index == -1) {
				int dot_index = -1;
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == '.') {
						dot_index = i;
						break;
					}
				}
				
				if (dot_index == -1) { // an integer, positive or negative
					numerator = new BigInteger(str);
					denominator = new BigInteger("1");
				} else {
					// find the length of the decimal part
					String dec = str.substring(dot_index + 1);
					int n = dec.length();
					denominator = ten.pow(n); 
					numerator = new BigInteger(dec);
					// adding the integer part
					BigInteger intPart = new BigInteger(str.substring(0, dot_index));
					if (str.charAt(0) == '-') { // the number is negative 
						numerator = (intPart.multiply(denominator)).subtract(numerator);
					} else {
						numerator = numerator.add(intPart.multiply(denominator));
					}
				}
			}
			else {
				String[] saFrac = new String[2];
	
				saFrac[0] = str.substring(0, slash_index);
				saFrac[1] = str.substring(slash_index + 1);
	
				numerator = new BigInteger(saFrac[0]);
				denominator = new BigInteger(saFrac[1]);
			}
		}
	
		int compare(BigRational other) {
			BigInteger crossMult1 = numerator.multiply(other.denominator);
			BigInteger crossMult2 = other.numerator.multiply(denominator);
			
			int res = crossMult1.compareTo(crossMult2);
			
			if (res != 0) return res;
			
			if (numerator.signum() >= 0) {
				return 1; // for positive, the decimal is smaller
			} else {
				return -1; // for negative, the decimal is larger
			}
		}
	}
	
	static class RationalPair {
		private String s = null;
		private Long num = null;
		private Long den = null;
		private Double d = null;
		private BigRational big_rat = null;
	
		public RationalPair(String s) {
			this.s = s;
		}
	
		public String get_string() {
			return s;
		}
	
		public void update() {
			int i = 0;
	
			for (; i < s.length(); i++) {
				if (s.charAt(i) == '/') {
					break;
				}
			}
	
			long num = Long.parseLong(s.substring(0, i));
			long den = Long.parseLong(s.substring(i + 1));
	
			this.num = num;
			this.den = den;
	
			this.d = (double)num / (double)den;
		}
	
		public double get_float() {
			if (this.d != null) {
				return this.d;
			}
			else {
				this.update();
				return this.d;
			}
		}
	
		public long get_num() {
			if (this.num != null) {
				return this.num;
			}
			else {
				this.update();
				return this.num;
			}
		}
	
		public BigRational get_big_rational() {
			if (this.big_rat != null) {
				return this.big_rat;
			}
			else {
				this.big_rat = new BigRational(BigInteger.valueOf((long)num.intValue()), BigInteger.valueOf((long)den.intValue()));
				return this.big_rat;
			}
		}
	
		public int compare(DecimalPair other) {
			double this_float = this.get_float();
	
			// <0 if rational is smaller
	
			double this_next_up = java.lang.Math.nextUp(this_float);
			double this_next_down = java.lang.Math.nextDown(this_float);
	
			
			if (this_next_up < other.get_bucket_low()) {
				return -1;
			}
			else if (this_next_down > other.get_bucket_high()) {
				return 1;
			} 
	
			double other_float = other.get_float();
	
			if (this_next_up < java.lang.Math.nextDown(other_float)) {
				return -1;
			}
			else if (this_next_down > java.lang.Math.nextUp(other_float)) {
				return 1;
			}
	
			return this.get_big_rational().compare(other.get_big_rational());
		}
	
		// Get the index of the bucket to place this rational in given the bucket multiplier
		public int get_bucket(int bucket_multiplier) {
			return (int)((this.get_float() + 6.0) * bucket_multiplier);
		}
	}
	
	static class RationalPairCompare implements Comparator<RationalPair> {
	
		@Override
		public int compare(RationalPair o1, RationalPair o2) {
			double f0 = o1.get_float();
			double f1 = o2.get_float();
	
			if (f0 < f1) {
				return -1;
			}
			else if (f0 > f1) {
				return 1;
			}
			else {
				if (o1.get_num() > o2.get_num()) {
					return 1;
				}
				else if (o1.get_num() < o2.get_num()) {
					return -1;
				}
	
				return 0;
			}
		}
		
	}
	
	static class DecimalPair {
		private String s = null;
		private BigRational big_rat = null;
		private Double d = null;
		private Double bucket = null;
		private Double bucket_width = null;
	
		public DecimalPair(String s) {
			this.s = s;
		}
	
		// Get the string representation of the decimal pair
		public String get_string() {
			return s;
		}
	
		// Get the big rational representation of the decimal pair
		public BigRational get_big_rational() {
			if (this.big_rat != null) {
				return this.big_rat;
			}
			else {
				this.big_rat = new BigRational(this.s);
				return this.big_rat;
			}
		}
	
		// Get the digit at the given index in the decimal number
		public int get_digit_at_index(int index) {
			int offset = 0;
			if (this.s.charAt(0) == '-') {
				offset++;
			}
	
			// Skip past the decimal point
			if (index > 0) {
				index++;
			}
	
			// Any nonexistant digits are replaced with '-'
			if (index + offset >= this.s.length()) {
				return 0;
			}
	
			return this.s.charAt(index + offset) - '0';
		}
	
		// Add this decimal's digits to a chunk total
		public void add_chunk_total(int[][] counts, int minimum_index) {
			for (int index = 0; index < DecimalSorting.CHUNK_SIZE; index++) {
				int v = get_digit_at_index(minimum_index + index);
				counts[index][v]++;
			}
		}
	
		// Get the floating point approximation of the number at hand
		public double get_float() {
			if (this.d != null) {
				return this.d;
			}
			else {
				this.d = Double.parseDouble(this.s);
				return this.d;
			}
		}
		
		// Get the index of the bucket to place this decimal in given the bucket multiplier
		public int inner_get_bucket(int bucket_multiplier) {
			if (bucket_multiplier == 10) {
				return (get_digit_at_index(0));
			}
			else if (bucket_multiplier == 100) {
				return (get_digit_at_index(0) * 10 + get_digit_at_index(1));
			}
			else if (bucket_multiplier == 1000) {
				return (get_digit_at_index(0) * 100 +
						get_digit_at_index(1) * 10 +
						get_digit_at_index(2));
			}
			else if (bucket_multiplier == 10000) {
				return (get_digit_at_index(0) * 1000 +
						get_digit_at_index(1) * 100 +
						get_digit_at_index(2) * 10 +
						get_digit_at_index(3));
			}
			else if (bucket_multiplier == 100000) {
				return (get_digit_at_index(0) * 10000 +
						get_digit_at_index(1) * 1000 +
						get_digit_at_index(2) * 100 +
						get_digit_at_index(3) * 10 +
						get_digit_at_index(4));
			}
			else if (bucket_multiplier == 1000000) {
				return (get_digit_at_index(0) * 100000 +
						get_digit_at_index(1) * 10000 +
						get_digit_at_index(2) * 1000 +
						get_digit_at_index(3) * 100 +
						get_digit_at_index(4) * 10 +
						get_digit_at_index(5));
			}
			else if (bucket_multiplier == 10000000) {
				return (get_digit_at_index(0) * 1000000 +
						get_digit_at_index(1) * 100000 +
						get_digit_at_index(2) * 10000 +
						get_digit_at_index(3) * 1000 +
						get_digit_at_index(4) * 100 +
						get_digit_at_index(5) * 10 +
						get_digit_at_index(6));
			}
			return -1;
		}
	
		/// Get the index of the bucket to place this decimal in given the bucket multiplier
		public int get_bucket(int bucket_multiplier) {
			int v = inner_get_bucket(bucket_multiplier);
	
			if (v == -1) {
				// We default to 1000 buckets
				return get_bucket(1000);
			}
	
			this.bucket_width = 5.0 / (double)bucket_multiplier;
	
			if (this.s.charAt(0) == '-') {
				this.bucket = (-10.0 * (double)v - 5.0) / (double)bucket_multiplier;
			}
			else {
				this.bucket = (10.0 * (double)v + 5.0) / (double)bucket_multiplier;
			}
	
			return v;
		}
	
		public Double get_bucket_high() {
			if (this.bucket != null) {
				return this.bucket + this.bucket_width;
			}
			else {
				return null;
			}
		}
	
		public Double get_bucket_low() {
			if (this.bucket != null) {
				return this.bucket - this.bucket_width;
			}
			else {
				return null;
			}
		}
	}
	
	static class DecimalSorting {
		public static int CHUNK_SIZE = 4;
	
		// Get the number of buckets to sort the decimals into for a given input size
		public static int get_bucket_count(int count) {
			if (count <= 75000) {
				return 10000;
			}
			else if (count <= 100000) {
				return 100000;
			}
			else if (count <= 500000) {
				return 1000000;
			} 
			else if (count <= 2000000) {
				return 1000000;
			} 
	
			return 10000000;
		}
	
		// Get the count totals for a chunk
		public static void count_total_chunk(int[][] counts, DecimalPair[] values, int minimum_index) {
			for (DecimalPair val: values) {
				val.add_chunk_total(counts, minimum_index);
			}
		}
	
		// Reorder an array of decimal pairs based on counting sort counts
		public static boolean counting_sort_reorder(int[] counts, DecimalPair[] values, DecimalPair[] output, int index) {
			// Total the counts
			if (counts[0] == values.length) {
				return false;
			}
			for (int i = 1; i < 10; i++) {
				if (counts[i] == values.length) {
					return false;
				}
				counts[i] += counts[i - 1];
			}
	
			// Go backwards through the values, moving them to the output array
			for (int i = values.length - 1; i >= 0; i--) {
				DecimalPair v = values[i];
				output[--counts[v.get_digit_at_index(index)]] = v;
			}
	
			return true;
		}
	
		// Perform radix sort on a chunk of the appropriate number digits
		public static void radix_sort_chunk(DecimalPair[] toSort, DecimalPair[] other, int minimum_index) {
			int[][] counts = new int[CHUNK_SIZE][10];
			for (int j = 0; j < CHUNK_SIZE; j++) {
				for (int k = 0; k < 10; k++) {
					counts[j][k] = 0;
				}
			}
	
			count_total_chunk(counts, toSort, minimum_index);
			int flag = 0;
	
			for (int i = CHUNK_SIZE - 1; i >= 0; i--) {
				if (flag % 2 == 1) {
					if (counting_sort_reorder(counts[i], other, toSort, minimum_index + i)) {
						flag++;
					}
				}
				else {
					if (counting_sort_reorder(counts[i], toSort, other, minimum_index + i)) {
						flag++;
					}
				}
			}
	
			if (flag % 2 == 1) {
				for (int i = 0; i < toSort.length; i++) {
					toSort[i] = other[i];
				}
			}
		}
	
		// Sort a bucket of decimals using radix sort
		public static void radix_sort_decimals_bucket(DecimalPair[] bucket) {
			if (bucket.length < 2) {
				return;
			}
	
			DecimalPair[] other = new DecimalPair[bucket.length];
	
			int max = 0;
	
			for (DecimalPair v: bucket) {
				int length = v.get_string().length();
				if (length > max) {
					max = length;
				}
			}
	
			other = bucket.clone();
	
			int start = (max + CHUNK_SIZE - 1) / CHUNK_SIZE;
			
			for (int i = start * CHUNK_SIZE; i >= 0; i -= CHUNK_SIZE) {
				radix_sort_chunk(bucket, other, i);
			}
		}
	
		// Sort the decimals
		public static void sort_decimals(DecimalPair[] decimals) {
			int bucket_count = get_bucket_count(decimals.length);
	
			ArrayList<ArrayList<DecimalPair>> buckets = new ArrayList<>(bucket_count);
	
			// Initialize the buckets
			for (int i = 0; i < bucket_count; i++) {
				buckets.add(new ArrayList<>(10 + decimals.length / bucket_count));
			}
	
			// Split the values up into buckets
			for (DecimalPair p : decimals) {
				int index = p.get_bucket(bucket_count);
				buckets.get(index).add(p);
			}
	
			// Convert each bucket into an array
			ArrayList<DecimalPair[]> array_buckets = new ArrayList<>(bucket_count);
			for (int i = 0; i < bucket_count; i++) {
				DecimalPair[] data = new DecimalPair[buckets.get(i).size()];
	
				for (int j = 0; j < buckets.get(i).size(); j++) {
					data[j] = buckets.get(i).get(j);
				}
				array_buckets.add(data);
			}
	
			// Sort each bucket
			for (int i = 0; i < bucket_count; i++) {
				DecimalPair[] array = array_buckets.get(i);
				radix_sort_decimals_bucket(array);
				array_buckets.set(i, array);
			}
	
			// Combine the buckets
			int index = 0;
			for (int i = 0; i < bucket_count; i++) {
				for (DecimalPair p : array_buckets.get(i)) {
					decimals[index++] = p;
				}
			}
		}
	}
	
	static class RationalSorting {
		// Sort the rationals
		public static void sort_rationals(RationalPair[] rationals) {
			int bucket_multiplier = 1000;
			int bucket_count = 12 * bucket_multiplier + 1;
			int expected_bucket_size = rationals.length / bucket_count;
			if (expected_bucket_size < 10) {
				expected_bucket_size = 10;
			}
			ArrayList<ArrayList<RationalPair>> buckets = new ArrayList<>(bucket_count);
	
			// Populate the buckets with empty array lists
			for (int i = 0; i < bucket_count; i++) {
				buckets.add(new ArrayList<>(expected_bucket_size));
			}
	
			// Place the pairs into buckets
			for (RationalPair p : rationals) {
				int index = p.get_bucket(bucket_multiplier);
				buckets.get(index).add(p);
			}
	
			// Sort each bucket using TimSort
			for (int i = 0; i < bucket_count; i++) {
				buckets.get(i).sort(new RationalPairCompare());
			}
	
			// Combine the results of sorting each bucket into the starting array
			int index = 0;
			for (int i = 0; i < bucket_count; i++) {
				for (RationalPair p: buckets.get(i)) {
					rationals[index++] = p;
				}
			}
		}
	}
}

