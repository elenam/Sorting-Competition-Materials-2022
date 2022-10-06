import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class DataGenerator {
	private static int seed = 1001; // change the seed to get different data
	private static Random r = new Random(seed);

	public static void main(String[] args) throws FileNotFoundException {

		if (args.length < 2) {
			System.out.println(
					"Please run with two command line arguments: output file name and the number of items");
			System.exit(0);
		}
		String outFileName = args[0];
		int n = Integer.parseInt(args[1]);


		String[] data = new String[n];

		// Figure out the kind of a number:
		for (int i = 0; i < n; ++i) {
			switch (r.nextInt(2)) {
				case 0:
					data[i] = generateDecimal(); 
					break;
				case 1:
					data[i] = generateRational(); // This needs to change
					break;
				default:
					break;
			}
		}


		PrintWriter out = new PrintWriter(outFileName);
		for (String s : data) {
			out.println(s);
		}
		out.close();
	}
	
	private static int CHANCE = 25; 
	
	private static String generateDecimal() {
		StringBuffer digits = new StringBuffer();
		
		int intPart = r.nextInt(10) - 5; 
		digits.append(intPart);
		digits.append(".");
		
		while(true) {
			// roll the dice
			int roll = r.nextInt(CHANCE);
			if (roll == 0) { // ending
				if (digits.length() == 2) {
					digits.deleteCharAt(1); // positive integer, remove the decimal point
				}
				else if ((digits.length() == 3) && (digits.charAt(0) == '-')) {
					digits.deleteCharAt(2); // negative integer, remove the decimal point
				}
				// adding a digit after a zero, except when it's an integer
				else if (digits.charAt(digits.length() - 1) == '0') { 
					// need to add the last non-zero digit
					digits.append(r.nextInt(9) + 1);
				}
				break;
			}
			// adding a digit 
			digits.append(r.nextInt(10));
		}
		
		return digits.toString();
	}
	
	private static int RANGE_RATIONAL = 100000;
	
	private static String generateRational() {
		int intPart = r.nextInt(10) - 5;
		int denominator = r.nextInt(RANGE_RATIONAL) + 2; // to avoid 0 and 1
		int numerator = r.nextInt(denominator - 1) + 1;
		
		int numeratorWithInt = (intPart * denominator) + numerator;
		
		return "" + numeratorWithInt + "/" + denominator; 
	}

}
