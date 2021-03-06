
public class QuicksortDireita{
	private static Integer[] numbers;
	private static int number;
	private static int init,end;

	public static void main(String[] olocoSTR) {
		Integer[] olocoINT = new Integer[olocoSTR.length];
		int i=0;
		for (String str : olocoSTR) {
			olocoINT[i] = Integer.parseInt(str);
			i++;
		}
		sort(olocoINT);
	}

	public static void sort(Integer[] values) {
		// check for empty or null array
		if (values ==null || values.length==0){
			return;
		}
		numbers = values;
		number = values.length;
		quicksortInit(0, number - 1);
		for (int i = init; i <= end; i++) {
		//	System.out.print(numbers[i]+",");
		}
	}

	public static void quicksortInit(int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		int pivot = numbers[low + (high-low)/2];

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list
			while (numbers[i] < pivot) {
				i++;
			}
			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (numbers[j] > pivot) {
				j--;
			}

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		// Recursion
		init = i;
		end = high;
		if (i < high){
			quicksort(i, high);
		}
	}

	public static void quicksort(int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		int pivot = numbers[low + (high-low)/2];

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list
			while (numbers[i] < pivot) {
				i++;
			}
			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (numbers[j] > pivot) {
				j--;
			}

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}

	public static void exchange(int i, int j) {
		int temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;
	}
} 
