import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class DataGenerator {

	public static void main(String[] args) throws FileNotFoundException {
		int seed = 1001; // change the seed to get different data
		Random r = new Random(seed);

		if (args.length < 2) {
			System.out.println(
					"Please run with two command line arguments: output file name and the number of items");
			System.exit(0);
		}
		String outFileName = args[0];
		int n = Integer.parseInt(args[1]);


		String[] data = new String[n];


		for (int i = 0; i < n; ++i) {
			switch (r.nextInt(3)) {
				case 0:
					data[i] = Integer.toString((r.nextInt(10) - 5)); // This might need to change
					break;
				case 1:
					data[i] = Double.toString(r.nextDouble()*10 - 5); // This needs to change
					break;
				case 2:
					data[i] = Integer.toString((r.nextInt(10) - 5)) + "/" + Integer.toString((r.nextInt(1000))+1); // This needs to change
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

}
