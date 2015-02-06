package ID3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/** read in data **/

public class DataInput {

	private final HashMap<String, Double> classification;
	private final HashMap<Integer, HashMap<Integer, Double>> dataset;
	private final String filelocation;

	public DataInput(String file) throws IOException {

		dataset = new HashMap<Integer, HashMap<Integer, Double>>();
		filelocation = file;

		classification = new HashMap<String, Double>();
		classification.put("Iris-setosa", 1.0);
		classification.put("Iris-versicolor", 2.0);
		classification.put("Iris-virginica", 3.0);

		build();
	}

	// read in file and store data
	/**
	 * @throws IOException
	 */
	public void build() throws IOException {

		final BufferedReader reader = new BufferedReader(new FileReader(
				filelocation));
		int row = 1;
		String line = null;

		while ((line = reader.readLine()) != null) {
			final HashMap<Integer, Double> rowData = new HashMap<Integer, Double>();
			final String[] parts = line.split(",");
			int column = 1;

			for (int i = 0; i < parts.length; i++) {
				// if classification
				if (i == (parts.length - 1)) {
					rowData.put(column, classification.get(parts[i]));
				} else {
					rowData.put(column, Double.valueOf(parts[i].toString()));
				}
				column++;
			}
			dataset.put(row, rowData);
			row++;

		}

	}

	// return classification
	public HashMap<String, Double> getClassification() {
		return classification;
	}

	// return dataset
	public HashMap<Integer, HashMap<Integer, Double>> getDataset() {
		return dataset;
	}
}
