import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Group7 {
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
    private static void sort(String[] toSort) {
        sort(toSort, 0, toSort.length);
    }
    public static void sort(String[] a, int fromIndex, int toIndex){
        SortingCompetitionComparator s = new SortingCompetitionComparator();
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex " + fromIndex + " > toIndex " + toIndex);
        if (fromIndex < 0)
            throw new ArrayIndexOutOfBoundsException();
        for (int chunk = fromIndex; chunk < toIndex; chunk += 6){
            int end = Math.min(chunk + 6, toIndex);
            for (int i = chunk + 1; i < end; i++){
                if (s.compare(a[i - 1], a[i]) > 0){
                    int j = i;
                    String elem = a[j];
                    do{
                        a[j] = a[j - 1];
                        j--;
                    }while (j > chunk && s.compare(a[j - 1], elem) > 0);
                    a[j] = elem;
                }
            }
        }
        int len = toIndex - fromIndex;
        if (len <= 6)
            return;

        String[] src = a;
        String[] dest = new String[len];
        String[] t;

        int srcDestDiff = -fromIndex;
        for (int size = 6; size < len; size <<= 1){
            for (int start = fromIndex; start < toIndex; start += size << 1){
                int mid = start + size;
                int end = Math.min(toIndex, mid + size);
                if (mid >= end ||s.compare(src[mid - 1], src[mid]) <= 0){
                    System.arraycopy(src, start, dest, start + srcDestDiff, end - start);
                }
                else if (s.compare(src[start], src[end - 1]) > 0){
                    System.arraycopy(src, start, dest, end - size + srcDestDiff, size);
                    System.arraycopy(src, mid, dest, start + srcDestDiff, end - mid);
                }
                else{
                    int p1 = start;
                    int p2 = mid;
                    int i = start + srcDestDiff;
                    while (p1 < mid && p2 < end){
                        dest[i++] = src[(s.compare(src[p1], src[p2]) <= 0 ? p1++ :p2++)];
                    }
                    if (p1 < mid)
                        System.arraycopy(src, p1, dest, i, mid - p1);
                    else
                        System.arraycopy(src, p2, dest, i, end - p2);
                }
            }
            t = src;
            src = dest;
            dest = t;
            fromIndex += srcDestDiff;
            toIndex += srcDestDiff;
            srcDestDiff = -srcDestDiff;
        }
        if (src != a) {
            System.arraycopy(src, 0, a, srcDestDiff, toIndex);
        }
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
            BigInteger numerator1 = new BigInteger(saFrac[0]);
            BigInteger denominator1 = new BigInteger(saFrac[1]);
            //find the length of the decimal's fractional part
            String[] saDec = decimal.split("\\."); // need \\ because . is a special symbol in regex
            BigInteger numerator2;
            BigInteger denominator2;

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