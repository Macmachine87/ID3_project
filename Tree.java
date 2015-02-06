package ID3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** Tree data object **/

public class Tree {

	private final HashMap<String, Double> classification;
	private HashMap<Integer, HashMap<Integer, Integer>> confusionMatrix;
	private Node root;

	// Constructor
	public Tree(HashMap<Integer, HashMap<Integer, Double>> trainSet) {

		setRoot(new Node(trainSet));
		root.setDepth(1);

		classification = new HashMap<String, Double>();
		classification.put("Iris-setosa", 1.0);
		classification.put("Iris-versicolor", 2.0);
		classification.put("Iris-virginica", 3.0);

		setConfusionMatrix(new HashMap<Integer, HashMap<Integer, Integer>>());

		createTree(root);

		prune(root);

	}
	
	// Calculates entropy

	public double calculateEntropy(
			ArrayList<HashMap<Integer, HashMap<Integer, Double>>> data) {

		double entropy = 0;

		final HashMap<Integer, HashMap<Integer, Double>> ds = new HashMap<Integer, HashMap<Integer, Double>>();

		if (data.size() == 0) {
			// data hasn't been initialized or is empty
			return -1;
		}

		final Iterator<HashMap<Integer, HashMap<Integer, Double>>> r = data
				.iterator();

		while (r.hasNext()) {
			HashMap<Integer, HashMap<Integer, Double>> temp;
			temp = r.next();
			ds.putAll(temp);
		}

		final Set<Integer> rows = ds.keySet();

		final double[] valueCounts = new double[4];

		final Iterator<Integer> it = rows.iterator();
		while (it.hasNext()) {
			final Integer i = it.next();
			valueCounts[ds.get(i).get(5).intValue()]++;
		}
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
	
	//clear training data between folds, while maintaining tree structure
	public void clearTrainedData(Node n) {
		if (n.getLeft() != null) {
			clearTrainedData(n.getLeft());
		}
		if (n.getRight() != null) {
			clearTrainedData(n.getRight());
		}
		n.setData(new HashMap<Integer, HashMap<Integer, Double>>());

	}
	
	//Creates the tree structure 
	public void createTree(Node n) {
		final HashMap<Integer, HashMap<Integer, Double>> data = n.getData();

		final InfoGain ig = new InfoGain();

		n.setEntropy(ig.calculateEntropy(data));

		if ((n.getEntropy() == 0) || (n.getTraits().size() == 4)) {

			setLeaf(n);
			return;
		} else {
			final Integer trait = ig.calculateInformationGain(data,
					n.getTraits());
			final ArrayList<Integer> usedTraits = n.getTraits();
			usedTraits.add(trait);
			final double splitPoint = ig.determineSplitPoint(data, trait);
			n.setRule(trait.intValue() + "," + splitPoint);
			final ArrayList<HashMap<Integer, HashMap<Integer, Double>>> futureChildren = ig
					.split(data, trait, splitPoint);
			final Node c1 = new Node(futureChildren.get(0), usedTraits, n);
			n.setLeft(c1);
			c1.setDepth(n.getDepth() + 1);
			// System.out.println("left child created");
			final Node c2 = new Node(futureChildren.get(1), usedTraits, n);
			n.setRight(c2);
			c2.setDepth(n.getDepth() + 1);
			// System.out.println("right child created");
			createTree(c1);
			createTree(c2);
		}

	}
	
	//get Confusion matrix
	public HashMap<Integer, HashMap<Integer, Integer>> getConfusionMatrix() {
		return confusionMatrix;
	}
	
	//get root node
	public Node getRoot() {
		return root;
	}
	
	//Reduced-error pruning
	public void prune(Node n) {
		final InfoGain ig = new InfoGain();
		if (n.getLeft() != null) {
			prune(n.getLeft());
		}
		if (n.getRight() != null) {
			prune(n.getRight());
		}
		final double purity = ig.calculatePurity(n.getData());
		if (purity >= 85) {
			n.setLeft(null);
			n.setRight(null);
			n.setRule(null);
			setLeaf(n);
		} else {
			return;
		}

	}

	//sets the confusion matrix
	public void setConfusionMatrix(
			HashMap<Integer, HashMap<Integer, Integer>> confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}

	//set the leaf node
	private void setLeaf(Node n) {
		final InfoGain ig = new InfoGain();
		n.setLabel(new Integer(ig.calculateMajorityClass(n.getData()))
				.intValue()
				+ ","
				+ classification.get(new Integer(ig.calculateMajorityClass(n
						.getData()))));
		return;
	}

	//set root
	private void setRoot(Node root) {
		this.root = root;
	}

}
