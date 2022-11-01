import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Group5 {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException, ExecutionException {

		if (args.length < 2) {
			System.out.println(
					"Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];
		
		//runTests();

		String[] data = readData(inputFileName); // read data as strings
		
		String[] toSort = data.clone(); // clone the data

		sort(toSort); // call the sorting method once for JVM warmup
		
		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		toSort = sort(toSort); // sort again

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
	private static String[] sort(String[] toSort) throws InterruptedException, ExecutionException {
		ArrayList<String> positiveArray = new ArrayList<String>();
		ArrayList<String> negativeArray = new ArrayList<String>();

		for(String s: toSort) {
			if(s.contains("-")) {
				negativeArray.add(s);
			}
			else {
				positiveArray.add(s);
			}
		}

		//Create thread and sort positive array
		ExecutorService pool =  Executors.newFixedThreadPool(1);
		final String[] pos =  positiveArray.toArray(new String[positiveArray.size()]);
		final int posLength = pos.length;
		Future<String[]> positiveSortedArray = pool.submit(()->{
		  return KevinArhelgerSort.positiveBucketSort(pos, posLength);
		});

		//Sort negative array
		String[] neg =  negativeArray.toArray(new String[negativeArray.size()]);
		final int negLength = neg.length;
		KevinArhelgerSort.negativeBucketSort(neg, negLength);

		//Wait for positve sort to finish
		while(!positiveSortedArray.isDone()) {
			
		}
		
		//Shutdown executor pool
		 pool.shutdown();

		toSort = Arrays.copyOf(neg, negLength+posLength);
		System.arraycopy(pos,0,toSort,negLength,posLength);
	
		return toSort;
	}

	public static class SortingCompetitionComparator implements Comparator<String> {

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
					return -compareFractionAndDecimal(o2, o1);
				}else{
					return compareDecimals(o1,o2);
				}
			}

		}


		public static int myComparison(String fraction, String decimal) {
			String[] saFrac = fraction.split("/");
			String[] decRHS = decimal.split("\\.");
			BigDecimal numerator = new BigDecimal(Integer.valueOf(saFrac[0]));
			BigDecimal denominator = new BigDecimal(Integer.valueOf(saFrac[1]));

			if(decRHS.length == 1) {
				BigDecimal fraction2 = numerator.divide(denominator, decRHS[0].length() + 300, RoundingMode.HALF_EVEN);
				BigDecimal decimal2 = new BigDecimal(decRHS[0]);
				switch(fraction2.compareTo(decimal2)) {
					case 1: return 1;
					case 0: return 1;
					case -1: return -1;
					default: break;
				}
			}

			else {
				BigDecimal fraction2 = numerator.divide(denominator, decRHS[1].length() + 3, RoundingMode.HALF_EVEN);
				BigDecimal decimal2 = new BigDecimal(decimal);
				switch(fraction2.compareTo(decimal2)) {
					case 1: return 1;
					case 0: return 1;
					case -1: return -1;
					default: break;
				}
			}
			return 0;
		}
		
		private int compareFractionAndDecimal(String fraction,String decimal) {
			String[] saFrac = fraction.split("/");
			BigInteger numerator1 = new BigInteger(saFrac[0]);
			BigInteger denominator1 = new BigInteger(saFrac[1]);
			
			//find the length of the decimal's fractional part
			String[] saDec = decimal.split("\\."); // need \\ because . is a special symbol in regex
			
			BigInteger numerator2 = null;
			BigInteger denominator2 = null; 				
			
			if (saDec.length == 1) { // an integer, positive or negative
				numerator2 = new BigInteger(saDec[0]);
				denominator2 = new BigInteger("1");
			} else {
				// find the length of the decimal part
				int n = saDec[1].length();
				denominator2 = new BigInteger("1"); 
				// raising 10 to the power n
				for (int i = 0; i < n; ++i) {
					denominator2 = denominator2.multiply(new BigInteger("10"));
				}
				numerator2 = new BigInteger(saDec[1]);
				// adding the integer part
				BigInteger intPart = new BigInteger(saDec[0]);
				if (saDec[0].charAt(0) == '-') { // the number is negative 
					numerator2 = (intPart.multiply(denominator2)).subtract(numerator2);
				} else {
					numerator2 = numerator2.add(intPart.multiply(denominator2));
				}
			}
			
			BigInteger crossMult1 = numerator1.multiply(denominator2);
			BigInteger crossMult2 = numerator2.multiply(denominator1);
			
			int res = crossMult1.compareTo(crossMult2);
			
			if (res != 0) return res;
			
			if (numerator1.compareTo(new BigInteger("0")) >= 0) {
				return 1; // for positive, the decimal is smaller
			} else {
				return -1; // for negative, the decimal is larger
			}
		}

		private int compareFractions(String fraction1, String fraction2) {
			// compare fraction by multiplication as big integers,
			// to make sure we are not losing precision
			
			String[] sa1 = fraction1.split("/");
			BigInteger numerator1 = new BigInteger(sa1[0]);
			BigInteger denominator1 = new BigInteger(sa1[1]);
			
			String[] sa2 = fraction2.split("/");
			BigInteger numerator2 = new BigInteger(sa2[0]);
			BigInteger denominator2 = new BigInteger(sa2[1]);
			
			BigInteger crossMult1 = numerator1.multiply(denominator2);
			BigInteger crossMult2 = numerator2.multiply(denominator1);
			
			int res = crossMult1.compareTo(crossMult2);	
			
			if (res != 0) return res;
			
			return numerator1.compareTo(numerator2); // note: the numerator may be negative, that would reverse the ordering for negatives
		}


		private int compareDecimals(String o1, String o2) {
			return (new BigDecimal(o1)).compareTo(new BigDecimal(o2));
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