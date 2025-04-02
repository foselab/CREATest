package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import createst.ysc.reading.IYscReader;
import createst.ysc.reading.YscReader;

public class WriteBenchmarkInfo {
	private static final String BENCHMARK_CSV = "../data/benchmark-info.csv";
	private static final String TARGET_DIR = "../benchmark";

	/**
	 * Create a benchmark-info.csv file with the information about states number,
	 * average depth and maximum depth for each .ysc file in the benchmark
	 * directory. If the file already exists, append rows to it.
	 * 
	 */
	public static void main(String[] args)
			throws NullPointerException, ParserConfigurationException, SAXException, IOException {
		System.out.println("----------------------------");
		System.out.println("Starting data collection from benchmark...");
		visit(TARGET_DIR, BENCHMARK_CSV, "");
		System.out.println("----------------------------");
	}

	/**
	 * Visit recursively a directory while writing to file
	 * 
	 */
	private static void visit(String baseDirectoryPath, String csvPath, String statechartDirectory)
			throws IOException, NullPointerException, ParserConfigurationException, SAXException {
		// For each file, read the data
		for (File file : new File(baseDirectoryPath).listFiles()) {
			if (file.getAbsolutePath().endsWith(".ysc")) {
				String statechartPath = file.getAbsolutePath();
				try {
					// Re-use YscReader from the main tool
					IYscReader yscReader = new YscReader(statechartPath);

					String statechartName = yscReader.getStatechartName();

					FileWriter writer;

					System.out.println("----------------------------");
					System.out.println("Retrieving data from " + statechartName + " statechart.");

					if (!Files.exists(Paths.get(csvPath))) {
						System.out.println("-> Initializing  " + csvPath + ".");
						writer = new FileWriter(csvPath, false);
						writer.append("File,StatechartName,NumStates,AvgDepth,MaxDepth\n");
						writer.close();
					}

					int numStates = 0;
					int maxDepth = 0;
					int sumDepth = 0;

					int depth;
					System.out.println("-> Reading  " + statechartPath + ".");
					for (String stateHierarchy : yscReader.getStatesNames().values()) {
						if (stateHierarchy.contains("_final_"))
							continue;
						depth = getDepth(stateHierarchy);
						maxDepth = Math.max(maxDepth, depth);
						sumDepth += depth;
						numStates++;
					}

					double avgDepth = ((double) sumDepth) / numStates;

					String fileName = statechartPath.substring(statechartPath.lastIndexOf('\\') + 1,
							statechartPath.lastIndexOf('.')) + ".ysc";
					String csv_row = fileName + "," + statechartName + "," + numStates + "," + avgDepth + "," + maxDepth + "\n";

					System.out.println("-> Writing  " + csvPath + ".");
					writer = new FileWriter(csvPath, true);
					writer.append(csv_row);

					writer.close();
					System.out.println("----------------------------");
				} catch (NullPointerException e) {
					System.err.println("Error with" + statechartPath + " " + e.getMessage());
				}
			}
		}
		// For each directory, recursively call visit
		for (File dir : new File(baseDirectoryPath).listFiles(File::isDirectory)) {
			System.out.println("\n\nVisiting " + statechartDirectory + "/" + dir.getName());
			visit(dir.getAbsolutePath(), csvPath, statechartDirectory + "/" + dir.getName());
		}
	}

	/**
	 * Given the hierarchy of a state in a statechart, return its depth
	 * 
	 */
	private static int getDepth(String stateHierarchy) {
		int depth = 0;
		String subStr = stateHierarchy;
		while (subStr.contains(".")) {
			subStr = subStr.substring(subStr.indexOf('.') + 1);
			depth++;
		}
		depth++;
		// The pattern is region.state.region.state etc., so the actual depth is
		// obtained by dividing by 2
		if (depth % 2 != 0)
			System.err.println("Error with " + stateHierarchy + ", odd number of states");
		return depth / 2;
	}
}
