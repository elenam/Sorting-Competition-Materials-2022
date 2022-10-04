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
			try {
				BigDecimal i = new BigDecimal(o1);
				try {
					BigDecimal o = new BigDecimal(o2);
					return i.compareTo(o);
				} catch (Exception e) {
					return(compareNormalAndFraction(i,o2));
				}
			} catch (Exception e) {
				try {
					BigDecimal o = new BigDecimal(o2);
					return(compareFractionAndNormal(o1, o));
				} catch (Exception e1) {
					return(compareFractions(o1,o2));
				}
			}
		}



		private int compareNormalAndFraction(BigDecimal bi, String fraction) {
			if (Double.compare(bi.doubleValue(),getValueFromFraction(fraction))==0){
				return -1;
			}else{
				return Double.compare(bi.doubleValue(),getValueFromFraction(fraction));
			}
		}
		
		private int compareFractionAndNormal(String fraction,BigDecimal bi) {
			if(Double.compare(getValueFromFraction(fraction),bi.doubleValue()) == 0){
				return 1;
			}else{
				return Double.compare(getValueFromFraction(fraction),bi.doubleValue());
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

	}
	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}
}
