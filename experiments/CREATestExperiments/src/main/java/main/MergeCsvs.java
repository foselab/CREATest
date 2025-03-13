package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVReader;

public class MergeCsvs {
	private static final String EVO_COVERAGE_CSV = "../data/evosuite-stats.csv";
	private static final String BENCHMARK_CSV = "../data/benchmark-info.csv";
	private static final String MERGED_CSV = "../data/data.csv";

	/**
	 * Merges the csv containing the information on the statecharts (name, number of
	 * states, avg depth and max depth) in the benchmark with the csv containing the
	 * coverage info from evosuite
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("----------------------------");
		System.out.println("Starting csv merging...");
		System.out.println("----------------------------");
		FileWriter writer = new FileWriter(MERGED_CSV, false);
		writer.append("File,StatechartName,NumStates,AvgDepth,MaxDepth,StandardEvosuiteCoverage,"
				+ "StandardSCTUnitCoverage,StandardSCTUnitStatus,SimplifiedEvosuiteCoverage,"
				+ "SimplifiedSCTUnitCoverage,SimplifiedSCTUnitStatus" + "\n");

		// Read the benchmark csv row by row, adding a new merged row ad each
		// iteration
		FileReader benchmarkFileReader = new FileReader(BENCHMARK_CSV);
		CSVReader benchmarkCsvReader = new CSVReader(benchmarkFileReader);
		benchmarkCsvReader.readNext(); // Skip first row (headers)
		String[] benchmarkNextRecord = benchmarkCsvReader.readNext();
		while (benchmarkNextRecord != null) {
			// Get the name of the statechart
			String benchmarkStatechart = benchmarkNextRecord[1];
			String[] mergedRecord = new String[11];
			// Add the data to the merged record
			mergedRecord[0] = benchmarkNextRecord[0] + ".ysc"; // File
			mergedRecord[1] = benchmarkNextRecord[1]; // StatechartName
			mergedRecord[2] = benchmarkNextRecord[2]; // NumStates
			mergedRecord[3] = benchmarkNextRecord[3]; // AvgDepth
			mergedRecord[4] = benchmarkNextRecord[4]; // MaxDepth
			// Search in the evosuite csv for the corresponding standard and simplified row
			FileReader evoCoverageFileReader = new FileReader(EVO_COVERAGE_CSV);
			CSVReader evoCoverageCsvReader = new CSVReader(evoCoverageFileReader);
			evoCoverageCsvReader.readNext(); // Skip first row (headers)
			String[] evoNextRecord = evoCoverageCsvReader.readNext();
			boolean foundStandard = false;
			boolean foundSimplified = false;
			while (evoNextRecord != null && !(foundStandard && foundSimplified)) {
				String evoStatechart = evoNextRecord[0].substring(evoNextRecord[0].lastIndexOf('.') + 1);
				if (benchmarkStatechart.equals(evoStatechart.replace("Simplified", ""))) {
					mergedRecord[5] = evoNextRecord[2]; // StandardEvosuiteCoverage
					foundStandard = true;
				}
				if (benchmarkStatechart.equals(evoStatechart)) {
					mergedRecord[8] = evoNextRecord[2]; // SimplifiedEvosuiteCoverage
					foundSimplified = true;
				}
				evoNextRecord = evoCoverageCsvReader.readNext();
			}
			evoCoverageCsvReader.close();
			benchmarkNextRecord = benchmarkCsvReader.readNext();
			// Append the row to csv
			System.out.println("Writing " + benchmarkStatechart + " row");
			writer.append(String.join(",", mergedRecord) + "\n");
		}
		System.out.println("\nFinished");
		benchmarkCsvReader.close();
		writer.close();
		System.out.println("----------------------------");
	}

}
