import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Group6 {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		SortingCompetitionComparator comp = new SortingCompetitionComparator();

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

		sort(toSort); //Preliminary sort based on length of given number, not yet correctly sorted

		Arrays.sort(toSort, comp); //Final Timsort to make array correctly and fully sorted

		long end = System.currentTimeMillis();

		System.out.println(end - start);


		writeOutResult(toSort, outFileName); // write out the results

		//System.out.println("New sort is correct: " + Test.testCorrectness());

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
	private static void sort(String[] arr) {
		SortingCompetitionComparator comp = new SortingCompetitionComparator();

		//Sorts all numbers by their lengths
		//When combined with Timsort, it manages to sort quicker than just Timsort on completely unsorted array
		//Implemented from https://www.geeksforgeeks.org/sort-array-large-numbers/
		
		Arrays.sort(arr, (left, right) -> {
			if(left.length() != right.length())
			/* If length of left != right, then return
               the diff of the length else  use compareTo
               function to compare values.*/
				return left.length() - right.length();
			return comp.compare(left, right);
		});


	}

	public static class SortingCompetitionComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {

			//Checks whether or not one of the inputs is
			//negative, making an easy comparison
			if (o1.contains("-") && !o2.contains("-")){
				return -1;
			}else{
				if (!o1.contains("-") && o2.contains("-")){
					return 1;
				}
			} 

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
					return compareDecimals(o1, o2);
				}
			}

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

		private int newCompareDecimals(String o1, String o2) {
			if(o1.length() <= 15 && o2.length() <= 15) {
				return Double.compare(Double.parseDouble(o1),Double.parseDouble(o2));
			}
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


	/* static void threeWayQuicksort(String[] arr, int start, int end, Comparator<String> comparator){
		if(end - start + 1 <= 5) {
			insertionSort(arr, start, end, comparator);
			return;
		}
		
		int i = start; int j = end; int n = start;
		String pivot = arr[end]; 

		while (i < j) {
			int comparison = comparator.compare(arr[i], pivot);

			if(comparison < 0) {
				String temp = arr[i];
				arr[i] = arr[n];
				arr[n] = temp;
				i++;
				n++;
			} else if(comparison == 0) {
				j--;
				String temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			} else {
				i++;
			}

			int min = Math.min(j-n, end-j+1);
			for(int k = 0; k < min; k++) {
				String temp = arr[n+k];
				arr[n+k] = arr[end-min+k+1];
				arr[end-min+k+1] = temp;
			}

			threeWayQuicksort(arr, start, n - 1, comparator);
			threeWayQuicksort(arr, n + (end - j + 1), end, comparator);

		}
	}

	private static final void insertionSort(String[] arr, int start, int end, Comparator<String> comparator) {
		for(int i = start + 1; i <= end; i++) {
			String key = arr[i];
			int j = i - 1;
			while(j >= start && comparator.compare(arr[j], key) > 0) {
				arr[j+1] =  arr[j];
				j--;
			}
			arr[j+1] = key;
		}
	} */

}

	
