import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Group1 {

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
					return -compareFractionAndDecimal(o2, o1);
				}else{
					return compareDecimals(o1,o2);
				}
			}
		}

		
		private int compareFractionAndDecimal(String fraction,String decimal) {
			String[] saFrac = fraction.split("/");
			fraction = Double.toString((Integer.parseInt(saFrac[0]) / Double.parseDouble(saFrac[1])));
			int compare = compareDecimals(fraction, decimal);

            if(compare == 0) {
				if(saFrac[0].charAt(0) == '-' ) {
					return -1;
				} else {
					return 1;
				}
            }
            return compare;
		}

		private int compareFractions(String fraction1, String fraction2) {
			String[] sa1 = fraction1.split("/");		
			String[] sa2 = fraction2.split("/");
			
            int compare = compareDecimals(Double.toString((Integer.parseInt(sa1[0]) / Double.parseDouble(sa1[1]))), Double.toString((Integer.parseInt(sa2[0]) / Double.parseDouble(sa2[1]))));
            

            if(compare == 0) {
				compare = sa1[0].compareTo(sa2[0]);
				if(sa1[0].charAt(0) == '-') {
					return -compare;
				} else {
					return compare;
				}
            }
            return compare;
		}


		private int compareDecimals(String o1, String o2) {
            int o1length = o1.length();
            int o2length = o2.length();
			if(o1.charAt(0) == '-') {
				if(o2.charAt(0) == '-') {
					for(int i=1; i<o1length && i<o2length; i++) {
                		int compare = Character.compare(o1.charAt(i),o2.charAt(i));
                		if(compare != 0) {
                    		return -compare;
                		}
            		}
					return -Integer.compare(o1length, o2length);
				}
				else {
					return -1;
				}
			} else if(o2.charAt(0) == '-') {
				return 1;
			} else {
				for(int i=0; i<o1length && i<o2length; i++) {
                	int compare = Character.compare(o1.charAt(i),o2.charAt(i));
                	if(compare != 0) {
                    	return compare;
                	}
            	}
				return Integer.compare(o1length, o2length);
			}
        }
	}

    private static class OldSortingCompetitionComparator implements Comparator<String> {

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
	
	private static void runTests() {
		SortingCompetitionComparator comp = new SortingCompetitionComparator();
		
		// Two fractions, positive and negative
		System.out.println("-1/2 and 1/4:" + comp.compareFractions("-1/2","1/4"));
		System.out.println("1/2 and 1/3:" + comp.compareFractions("1/2","1/3"));
		System.out.println("1/2 and 2/4:" + comp.compareFractions("1/2","2/4"));
		System.out.println("-1/2 and -2/4:" + comp.compareFractions("-1/2","-2/4"));
		
		// Fraction and a decimal
		System.out.println("1/4 and 0.5:" + comp.compareFractionAndDecimal("1/4","0.5"));
		System.out.println("2/4 and 1.5:" + comp.compareFractionAndDecimal("2/4","1.5"));
		System.out.println("-2/4 and -1.5:" + comp.compareFractionAndDecimal("-2/4","-1.5"));
		System.out.println("-2/4 and 0:" + comp.compareFractionAndDecimal("-2/4","0"));
		System.out.println("1/2 and -0.5:" + comp.compareFractionAndDecimal("1/2","-0.5"));
		System.out.println("1/2 and 0.5:" + comp.compareFractionAndDecimal("1/2","0.5"));
		System.out.println("-1/2 and -0.5:" + comp.compareFractionAndDecimal("-1/2","-0.5"));
		System.out.println("1/3 and -0.5:" + comp.compareFractionAndDecimal("1/3","-0.5"));
		
		System.out.println("-4.9999999999999999999999999999999999 and -5:" + comp.compareDecimals("-4.9999999999999999999999999999999999", "-5"));
		System.out.println("4.9999999999999999999999999999999999 and 5:" + comp.compareDecimals("4.9999999999999999999999999999999999", "5"));
		
	}
}