import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Group0 {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		if (args.length < 2) {
			System.out.println(
					"Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];

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
		Arrays.sort(toSort, new SortingCompetitionComparator());
	}

	private static class SortingCompetitionComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			if(o1.contains("/")){
				if(o2.contains("/")){
					return compareFractions(o1, o2);
				}else{
					return compareFractionAndDecimal(o1, o2);
				}

			}else{
				if(o2.contains("/")){
					return compareDecimalAndFraction(o1, o2);
				}else{
					return compareDecimals(o1,o2);
				}
			}

		}

		private int compareDecimalAndFraction(String o1, String fraction) {
			if (Double.compare(Double.parseDouble(o1),getValueFromFraction(fraction))==0){
				return -1;
			}else{
				return Double.compare(Double.parseDouble(o1),getValueFromFraction(fraction));
			}
		}
		
		private int compareFractionAndDecimal(String fraction,String o2) {
			if(Double.compare(getValueFromFraction(fraction),Double.parseDouble(o2)) == 0){
				return 1;
			}else{
				return Double.compare(getValueFromFraction(fraction),Double.parseDouble(o2));
			}
		}

		private int compareFractions(String fraction1, String fraction2) {
			double value1 = getValueFromFraction(fraction1);
			double value2 = getValueFromFraction(fraction2);
			if(value1==value2){
				return Integer.compare(getDenominator(fraction1),getDenominator(fraction2));
			}else{
				return Double.compare(value1, value2);
			}
		}

		private int getDenominator(String fraction) {
			String[] sa = fraction.split("/",2);
			return Integer.parseInt(sa[1]);
		}

		private double getValueFromFraction(String fraction) {
			String[] sa = fraction.split("/",2);
			double numerator = Integer.parseInt(sa[0]);
			double denominator = Integer.parseInt(sa[1]);
			return (numerator/denominator);
		}
		private int compareDecimals(String o1, String o2) {
			return Double.compare(Double.parseDouble(o1), Double.parseDouble(o2));
		}

	}
	
	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}
}
