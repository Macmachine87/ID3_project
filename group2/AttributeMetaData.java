package group2;

import java.util.HashMap;
import java.util.Map;
/**
 * Data describing a column in the dataset
 * @author Scott
 *
 */
public class AttributeMetaData  implements java.io.Serializable {
	private int id; // Sequence
	private String name;
	private Map<String, Integer> knownValues;
	private Map<Integer, String> reverseValues;
	private Map<String,Integer> knownValueMap = new HashMap<String,Integer>();
	private AttributeType type;
	private double nonZeroCount = 0;
	private double sumValue = 0;
	private double splitPoint;
	private double maxValue;
	private double minValue;
	private boolean inUse = false; // Is this an active attribute for this tree
	private double[] buckets;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttributeType getType() {
		return type;
	}

	public void setType(AttributeType type) {
		this.type = type;
	}

	public double getSplitPoint() {
		return splitPoint;
	}

	public void setSplitPoint(double splitPoint) {
		this.splitPoint = splitPoint;
	}

	public double getNonZeroCount() {
		return nonZeroCount;
	}

	public void setNonZeroCount(double nonZeroCount) {
		this.nonZeroCount = nonZeroCount;
	}

	public double getSumValue() {
		return sumValue;
	}

	public void setSumValue(double sumValue) {
		this.sumValue = sumValue;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public String toString() {
		String string = "META:" + getName() + " " + getId() + " " + getType();
		if (type.equals(AttributeType.continuous)) {
			string += " total " + sumValue + " nonZeroCount " + nonZeroCount
					+ " split " + splitPoint + " max " + maxValue + " min "
					+ minValue;
		}
		if (isInUse()) {
			string += " in use ";
		} else {
			string += " not in use ";
		}
		return string;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double[] getBuckets() {
		return buckets;
	}

	public void setBuckets(double[] buckets) {
		this.buckets = buckets;
	}


	public Map<String, Integer> getKnownValues() {
		return knownValues;
	}
	

	public void setKnownValues(Map<String, Integer> knownValues) {
		this.knownValues = knownValues;
	}

	public Map<String,Integer> getKnownValueMap() {
		return knownValueMap;
	}

	public void setKnownValueMap(Map<String,Integer> knownValueMap) {
		this.knownValueMap = knownValueMap;
	}

	public Map<Integer, String> getReverseValues() {
		return reverseValues;
	}

	public void setReverseValues(Map<Integer, String> reverseValues) {
		this.reverseValues = reverseValues;
	}
}