package ID3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** Entropy & Information Gain calculations **/

public class InfoGain {

	private double entropy;

	/** constructor **/
	public InfoGain() {
		entropy = 0;
	}

	public double calculateEntropy(
			HashMap<Integer, HashMap<Integer, Double>> data) {

		if (data.size() == 0) {
			// data hasn't been initialized or is empty
			return -1;
		}
		final Set<Integer> rows = data.keySet();

		final double[] valueCounts = new double[4];

		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer i = it.next();
			valueCounts[data.get(i).get(5).intValue()]++;
		}
		// double[] valueCounts = splitSetData(data, new Integer(5));
		for (final double valueCount : valueCounts) {
			final int count = (int) valueCount;
			final double probability = count / (double) data.size();
			if (count > 0) {
				entropy += -probability
						* (Math.log10(probability) / Math.log10(2));
			}
		}
		return entropy;
	}

	public Integer calculateInformationGain(
			HashMap<Integer, HashMap<Integer, Double>> data,
			ArrayList<Integer> unavailableTraits) {
		// broken up already!
		if ((data.size() == 0) || (unavailableTraits.size() == 4)) {
			return null;
		}

		Integer currTrait = 1;
		Integer bestTrait = 0;
		double bestGain = 0;
		double gain = calculateEntropy(data);
		while (currTrait < 5) {
			gain = calculateEntropy(data);
			final double splitPointForTrait = determineSplitPoint(data,
					currTrait);
			final ArrayList<HashMap<Integer, HashMap<Integer, Double>>> potentialChildren = split(
					data, currTrait, splitPointForTrait);
			final Iterator<HashMap<Integer, HashMap<Integer, Double>>> it = potentialChildren
					.iterator();
			while (it.hasNext()) {
				final HashMap<Integer, HashMap<Integer, Double>> chunk = it
						.next();
				gain -= (chunk.size() / data.size()) * calculateEntropy(chunk);
			}
			if ((gain > bestGain) && !unavailableTraits.contains(currTrait)) {
				bestGain = gain;
				bestTrait = currTrait;
			}
			currTrait++;
		}
		// perfectly sorted means entropy = 0
		if (gain == 0) {
			return 0;
		}

		return bestTrait;
	}

	public int calculateMajorityClass(
			HashMap<Integer, HashMap<Integer, Double>> data) {
		final int[] classCounts = new int[4];
		final Set<Integer> rows = data.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			classCounts[data.get(r).get(5).intValue()]++;
		}
		int most = 0;
		int best = 0;
		for (int i = 0; i < classCounts.length; i++) {
			if (classCounts[i] == 0) {
				continue;
			} else if (classCounts[i] > most) {
				most = classCounts[i];
				best = i;
				continue;
			} else {
				continue;
			}
		}
		return best;
	}

	public double calculatePurity(
			HashMap<Integer, HashMap<Integer, Double>> data) {
		final int[] classCounts = new int[4];
		final Set<Integer> rows = data.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			classCounts[data.get(r).get(5).intValue()]++;
		}
		double most = 0;
		for (final int classCount : classCounts) {
			if (classCount == 0) {
				continue;
			} else if (classCount > most) {
				most = classCount;
				continue;
			} else {
				continue;
			}
		}
		return ((most / data.size()) * 100);
	}

	public double determineSplitPoint(
			HashMap<Integer, HashMap<Integer, Double>> data, Integer attribute) {
		double min = findMinValue(attribute, data);
		double max = findMaxValue(attribute, data);
		double midpoint = findMid(max, min);
		double purity = calculatePurity(data);
		while ((max != midpoint) && (min != midpoint)) {
			final ArrayList<HashMap<Integer, HashMap<Integer, Double>>> output = split(
					data, attribute, midpoint);
			final double tempPurity = calculatePurity(output.get(0));
			if (tempPurity >= purity) {
				max = midpoint;
				midpoint = findMid(max, min);
				purity = tempPurity;
				// System.out.println(min+". " + midpoint+ ", " + purity);
			} else if (tempPurity <= purity) {
				min = midpoint;
				midpoint = findMid(max, min);
				// System.out.println(min+". " + midpoint+ ", " + max);
			} else {
				break;
			}

		}
		return midpoint;
	}

	public double findMaxValue(Integer attribute,
			HashMap<Integer, HashMap<Integer, Double>> data) {
		double max = 0;
		final Set<Integer> rows = data.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			if (max == 0) {
				max = data.get(r).get(attribute);
				continue;
			} else if (max < data.get(r).get(attribute)) {
				max = data.get(r).get(attribute);
				continue;
			} else {
				continue;
			}
		}
		return max;
	}

	public double findMid(double max, double min) {
		final double mid = Math.round(((max + min) / 2) * 10);

		return mid / 10;
	}

	/**
	 * traverses the data and finds the minimum value for a given attribute
	 *
	 * @param attribute
	 * @param data
	 * @return
	 */
	public double findMinValue(Integer attribute,
			HashMap<Integer, HashMap<Integer, Double>> data) {
		double min = 0;
		final Set<Integer> rows = data.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			if (min == 0) {
				min = data.get(r).get(attribute);
				continue;
			} else if (min > data.get(r).get(attribute)) {
				min = data.get(r).get(attribute);
				continue;
			} else {
				continue;
			}
		}
		return min;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public ArrayList<HashMap<Integer, HashMap<Integer, Double>>> split(
			HashMap<Integer, HashMap<Integer, Double>> data, Integer attribute,
			double splitPoint) {
		final HashMap<Integer, HashMap<Integer, Double>> child1 = new HashMap<Integer, HashMap<Integer, Double>>();
		final HashMap<Integer, HashMap<Integer, Double>> child2 = new HashMap<Integer, HashMap<Integer, Double>>();
		final ArrayList<HashMap<Integer, HashMap<Integer, Double>>> children = new ArrayList<HashMap<Integer, HashMap<Integer, Double>>>();
		final Set<Integer> rows = data.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			if (data.get(r).get(attribute) <= splitPoint) {
				child1.put(r, data.get(r));
			} else {
				child2.put(r, data.get(r));
			}
		}
		children.add(child1);
		children.add(child2);
		return children;
	}
}
