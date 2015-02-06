package ID3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Main {

	public static int kFolds = 10;

	/** Main class to read in data, test and create the tree **/

	public static void main(String args[]) throws Exception {

		if (args.length != 1) {
			System.err.println("Invalid file location. Please try again!");
			return;
		}

		final DataInput data = new DataInput(args[0]);
		final HashMap<Integer, HashMap<Integer, Double>> dataset = data
				.getDataset();
		HashMap<Integer, HashMap<Integer, Integer>> confusionMatrix = new HashMap<Integer, HashMap<Integer, Integer>> ();
		
		
		//for randomly picking datapoints incase data is sorted by classification
		final int[] testPicked = new int[151];

		// Capture best's
		Double best = 0.0;
		int bestRun = 0;
		Double average = 0.0;
		
		System.out.
			println("K");
		
		// k-fold validation
		for (int i = 1; i <= kFolds; i++) {
			final int[] picked = new int[15];
			int loop = 0;

			for (; loop < 15;) {
				final int r = randInt(1, 150);

				if (testPicked[r] == 0) {
					testPicked[r] = 1;
					picked[loop++] = r;
				}

			}

			// split data
			final DataSplits ds = new DataSplits(dataset);

			ds.splitData(picked);

			final HashMap<Integer, HashMap<Integer, Double>> trainSet = new HashMap<Integer, HashMap<Integer, Double>>();
			ArrayList<HashMap<Integer, HashMap<Integer, Double>>> temp = ds
					.getTrainSet();

			for (int count = 0; count < temp.size(); count++) {
				trainSet.putAll(temp.get(count));
			}

			final HashMap<Integer, HashMap<Integer, Double>> testSet = new HashMap<Integer, HashMap<Integer, Double>>();
			temp = ds.getTestSet();

			for (int count = 0; count < temp.size(); count++) {
				testSet.putAll(temp.get(count));
			}

			// create Tree
			final Tree t = new Tree(trainSet);

			t.clearTrainedData(t.getRoot());

			// Test tree
			final TreeTest tt = new TreeTest(trainSet, testSet, t.getRoot());
			tt.test();
			
			//get tree's accuracy 
			final Double acc = tt.calculateAccuracy(tt.getConfusionMatrix());
			
			average+=acc;
			
			if (acc >= best) {
				best = acc;
				bestRun = i;
				confusionMatrix=tt.getConfusionMatrix();
			}
			
			// Output results
			System.out.
					println(i+"."+"Testing with datapoints: "
					+ Arrays.toString(picked)
					+ " left out of the training process. Accuracy Rate: "
					+ String.format("%.2f", acc) + "%");

		}
		
		System.out.println();

		System.out
				.println("Average accuracy rate of all runs "
				+ String.format("%.2f", (average/10)) + "%");

		System.out.println();

		// Calculate best fold
		System.out
				.println("Best run was " + bestRun + ", yielding accuracy rate of:"
						+ String.format("%.2f", best) + "%");
		
		System.out.println();
		// output confusion matrix
		System.out.
				println("                   Iris-virginica | Iris-setosa | Iris-versicolor | ");
		System.out.
		println("  Iris-virginica |"+"       "+confusionMatrix.get(1).get(1)+"        |      "+confusionMatrix.get(2).get(1)+"      |       "+confusionMatrix.get(3).get(1)+ "         |");
		System.out.
		println("  Iris-setosa    |"+"       "+confusionMatrix.get(1).get(2)+"        |      "+confusionMatrix.get(2).get(2)+"      |       "+confusionMatrix.get(3).get(2)+ "         |");
		System.out.
		println("  Iris-versicolor|"+"       "+confusionMatrix.get(1).get(3)+"        |      "+confusionMatrix.get(2).get(3)+"      |       "+confusionMatrix.get(3).get(3)+ "         |");
	}

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		final Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		final int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

}
