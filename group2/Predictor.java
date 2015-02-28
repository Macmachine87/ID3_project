package group2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Predictor {

	public static void main(String[] args) {
		/*
		 * args 0 path
		 * args 1 forest
		 * args 2 data to test
		 */
		Forest forest = Forest.deserializeRandomForestFromFile(args[0]+args[1]);
		forest.getAttributes().setAllAttribuesAsUsed();	
		BufferedReader reader = null;
		List<int[]> data = new ArrayList<int[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]+args[2])), 20000);
			String line = "";
			double counter = 0;
			while ((line = reader.readLine()) != null) {
				if (line != null && line.length() > 0) {
					
					String score = forest.classify(line);
					if (counter % 50000 == 0) {
						System.out.println("tested row " + counter + " result = " + score);
					}
					counter++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
	}
	
}
