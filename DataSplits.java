package ID3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** splits the data into a bucket of 15 random data point **/

public class DataSplits {

	private HashMap<Integer, HashMap<Integer, Double>> dataset;
	private ArrayList<HashMap<Integer, HashMap<Integer, Double>>> testSet;
	private ArrayList<HashMap<Integer, HashMap<Integer, Double>>> trainSet;

	//constructor
	public DataSplits(HashMap<Integer, HashMap<Integer, Double>> ds) {

		dataset = new HashMap<Integer, HashMap<Integer, Double>>();
		testSet = new ArrayList<HashMap<Integer, HashMap<Integer, Double>>>();
		trainSet = new ArrayList<HashMap<Integer, HashMap<Integer, Double>>>();

		setDataset(ds);
	}

	private Boolean Contains(int[] picked, int n) {
		for (final int element : picked) {
			if (element == n) {
				return true;
			}

		}
		return false;
	}

	public ArrayList<HashMap<Integer, HashMap<Integer, Double>>> getTestSet() {
		return testSet;
	}

	public ArrayList<HashMap<Integer, HashMap<Integer, Double>>> getTrainSet() {
		return trainSet;
	}

	public void setDataset(HashMap<Integer, HashMap<Integer, Double>> dataset) {
		this.dataset = dataset;
	}

	public void setTestSet(
			ArrayList<HashMap<Integer, HashMap<Integer, Double>>> testSet) {
		this.testSet = testSet;
	}

	public void setTrainSet(
			ArrayList<HashMap<Integer, HashMap<Integer, Double>>> trainSet) {
		this.trainSet = trainSet;
	}

	public void splitData(int[] picked) {

		Arrays.sort(picked);
		Integer counter = 1;

		for (int i = 0; i < dataset.size(); i++) {

			final HashMap<Integer, HashMap<Integer, Double>> temp = new HashMap<Integer, HashMap<Integer, Double>>();
			temp.put(counter, dataset.get(counter));

			if (Contains(picked, counter)) {
				testSet.add(temp);
			} else {
				trainSet.add(temp);
			}
			counter++;

		}

	}
}
