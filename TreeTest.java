package ID3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** Test the built tree with the held out set **/

public class TreeTest {

	private HashMap<Integer, HashMap<Integer, Integer>> confusionMatrix;
	private final Node root;
	private final HashMap<Integer, HashMap<Integer, Double>> testData;

	public TreeTest(HashMap<Integer, HashMap<Integer, Double>> trainData,
			HashMap<Integer, HashMap<Integer, Double>> testData, Node root) {
		this.testData = testData;
		this.root = root;
		setConfusionMatrix(new HashMap<Integer, HashMap<Integer, Integer>>());

	}

	public void buildConfusionMatrix(Node n) {
		if (n.getLeft() != null) {
			buildConfusionMatrix(n.getLeft());
		}
		if (n.getRight() != null) {
			buildConfusionMatrix(n.getRight());
		} else {
			final String leafName = n.getLabel();
			final String[] labelPieces = leafName.split(",");
			final Integer classifiedAs = new Integer(labelPieces[0]);
			final HashMap<Integer, HashMap<Integer, Double>> sortedData = n
					.getData();

			if (confusionMatrix.containsKey(classifiedAs)) {

				final Set<Integer> rows = sortedData.keySet();
				final Iterator<Integer> it = rows.iterator();
				while (it.hasNext()) {
					final Integer r = it.next();
					final Integer actual = sortedData.get(r).get(5).intValue();
					Integer count = confusionMatrix.get(classifiedAs).get(
							actual);
					count++;
					final HashMap<Integer, Integer> temp = confusionMatrix
							.get(classifiedAs);
					temp.put(actual, count);
					confusionMatrix.put(classifiedAs, temp);
				}
			} else {
				final HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
				temp.put(1, 0);
				temp.put(2, 0);
				temp.put(3, 0);
				confusionMatrix.put(classifiedAs, temp);
				final Set<Integer> rows = sortedData.keySet();
				final Iterator<Integer> it = rows.iterator();
				while (it.hasNext()) {
					final Integer r = it.next();
					final Integer actual = sortedData.get(r).get(5).intValue();
					Integer count = confusionMatrix.get(classifiedAs).get(
							actual);
					count++;
					final HashMap<Integer, Integer> temp1 = confusionMatrix
							.get(classifiedAs);
					temp1.put(actual, count);
					confusionMatrix.put(classifiedAs, temp1);
				}
			}
		}
	}

	public double calculateAccuracy(
			HashMap<Integer, HashMap<Integer, Integer>> bestTest) {
		final int size = 15;

		if (bestTest.size() == 2) {

			final HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
			temp.put(1, 0);
			temp.put(2, 0);
			temp.put(3, 0);
			confusionMatrix.put(3, temp);

		}
		final int correct = bestTest.get(1).get(1) + bestTest.get(2).get(2)
				+ bestTest.get(3).get(3);
		final Double acc = new Double(correct) / new Double(size);
		// System.out.println(acc.doubleValue());
		return acc * 100;

	}

	private void fitData(Integer rowN, HashMap<Integer, Double> content, Node n) {
		if (n.getRule() == null) {
			final HashMap<Integer, HashMap<Integer, Double>> storedData = n
					.getData();
			storedData.put(rowN, content);
			return;
		} else {
			// rule is in format intTraitSplit,doubleOnValue
			final String[] ruleSet = n.getRule().split(",");
			// System.out.println(n.getRule());
			if (content.get(new Integer(ruleSet[0])) <= new Double(ruleSet[1])) {
				fitData(rowN, content, n.getLeft());
			} else {
				fitData(rowN, content, n.getRight());
			}
		}

	}

	public HashMap<Integer, HashMap<Integer, Integer>> getConfusionMatrix() {
		return confusionMatrix;
	}

	public void setConfusionMatrix(
			HashMap<Integer, HashMap<Integer, Integer>> confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}

	public void test() {

		final Set<Integer> rows = testData.keySet();
		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer r = it.next();
			fitData(r, testData.get(r), root);
		}
		buildConfusionMatrix(root);
	}

}
