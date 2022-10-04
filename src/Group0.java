import java.io.FileNotFoundException;

public class Group0 {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		
		if (args.length < 2) {
			System.out.println("Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];
		
		// read data as strings

		
		// clone the data 

		
		// call the sorting method once for JVM warmup
		
		// clone again
		
		Thread.sleep(10); //to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();
		
		// sort again
		
		long end = System.currentTimeMillis();
		
		System.out.println(end - start);
		
		// write out the results

	}
	
	// YOUR SORTING METHOD GOES HERE. 
	// You may call other methods and use other classes. 
	// Note: you may change the return type of the method. 
	// You would need to provide your own function that prints your sorted array to 
	// a file in the exact same format that my program outputs
	private static void sort(Integer[] toSort) {
		//Arrays.sort(toSort, new BinaryComparator());
	}
}
