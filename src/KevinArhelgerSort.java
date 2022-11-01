
import java.util.*;
  
class KevinArhelgerSort{
  
    //Sort positive array
    static String[] positiveBucketSort(String arr[], int n)
    {
        if (n <= 0)
            return null;
  
        // Create n empty buckets
        @SuppressWarnings("unchecked")
        ArrayList<String>[] buckets = new ArrayList[n+1];
  
        for (int i = 0; i <= n; i++) {
            buckets[i] = new ArrayList<String>();
        }
  
        // Put array elements in different buckets
        for (int i = 0; i < n; i++) {
            if(arr[i].contains("/")) {
                String[] saFrac = arr[i].split("/");
                Double value = Double.parseDouble(saFrac[0]) / Double.parseDouble(saFrac[1]);
                double index = value * n/5;
                 buckets[(int)index].add(arr[i]);
            }
            else{
                double value = Double.parseDouble(arr[i]);
                double index = value * n/5;
                buckets[(int)index].add(arr[i]);
            }
        }
  
        // Sort individual buckets
        for (int i = 0; i <= n; i++) {
            insertionSort(buckets[i], new Group5.SortingCompetitionComparator());
        }
  
        // Concatenate all buckets into arr[]
        int index = 0;
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j < buckets[i].size(); j++) {
                arr[index++] = buckets[i].get(j);
            }
        }
        return arr;
    }
  
    //Sort negative array
    static String[] negativeBucketSort(String arr[], int n)
    {
        if (n <= 0)
            return null;
  
        // Create n empty buckets
        @SuppressWarnings("unchecked")
        ArrayList<String>[] buckets = new ArrayList[n+1];
  
        for (int i = 0; i <= n; i++) {
            buckets[i] = new ArrayList<String>();
        }

        // Put array elements in different buckets
        for (int i = 0; i < n; i++) {
            if(arr[i].contains("/")) {
                String[] saFrac = arr[i].split("/");
                // Double value = Double.parseDouble(saFrac[0]) / Double.parseDouble(saFrac[1]);
                double index = -(Double.parseDouble(saFrac[0]) / Double.parseDouble(saFrac[1])) * n/5;
                 buckets[(int)index].add(arr[i]);
            }
            else{
                double value = Double.parseDouble(arr[i]);
                double index = -value * n/5;
                buckets[(int)index].add(arr[i]);
            }
        }
  
        // Sort individual buckets
        for (int i = 0; i <= n; i++) {
            insertionSort(buckets[i], new Group5.SortingCompetitionComparator());
        }
  
        // Concatenate all buckets into arr[]
        int index = 0;
        for (int i = n; i >= 0; i--) {
            for (int j = 0; j < buckets[i].size(); j++) {
                arr[index++] = buckets[i].get(j);
            }
        }

        return arr;
    }

    private static void insertionSort(ArrayList<String> buckets, Comparator<String> comparator) {
        for(int i = 1; i < buckets.size(); i++) {
            String currentElement = buckets.get(i);
            int j = i;
            while(j > 0) {
                String compareElement = buckets.get(j-1);
                if(comparator.compare(currentElement, compareElement) >= 0) {
                    break;
                }
                buckets.set(j, compareElement);
                j--;
            }
            buckets.set(j, currentElement);
        }
    }

}
