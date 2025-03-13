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
	 * TODO
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("----------------------------");
		System.out.println("Starting csv merging...");
		System.out.println("----------------------------");
		FileWriter writer = new FileWriter(MERGED_CSV, false);
		writer.append("File,StatechartName,NumStates,AvgDepth,MaxDepth,StandardEvosuiteCoverage,"
				+ "StandardSCTUnitCoverage,StandardSCTUnitStatus,SimplifiedEvosuiteCoverage,"
				+ "SimplifiedSCTUnitCoverage,SimplifiedSCTUnitStatus" + "\n");
		FileReader evoCoverageFileReader = new FileReader(EVO_COVERAGE_CSV);
		CSVReader evoCoverageCsvReader = new CSVReader(evoCoverageFileReader);
		evoCoverageCsvReader.readNext();
		// Read the csv from evosuite row by row, adding a new merged row ad each
		// iteration
		String[] evoNextRecord = evoCoverageCsvReader.readNext();
		while (evoNextRecord != null) {
			// Get the name of the statechart
			String evoStatechart = evoNextRecord[0].substring(evoNextRecord[0].lastIndexOf('.') + 1);
			// Skip if it ends with Simplified
			if (evoStatechart.endsWith("Simplified")) {
				evoNextRecord = evoCoverageCsvReader.readNext();
				continue;
			}
			String[] mergedRecord = new String[11];
			mergedRecord[5] = evoNextRecord[2]; // StandardEvosuiteCoverage
			mergedRecord[1] = evoStatechart; // StatechartName
			// Search again in the evosuite csv for the corresponding simplified row
			FileReader evoCoverageFileReader_bis = new FileReader(EVO_COVERAGE_CSV);
			CSVReader evoCoverageCsvReader_bis = new CSVReader(evoCoverageFileReader_bis);
			String[] evoNextRecord_bis = evoCoverageCsvReader_bis.readNext();
			while (evoNextRecord_bis != null) {
				String evoStatechart_bis = evoNextRecord_bis[0].substring(evoNextRecord_bis[0].lastIndexOf('.') + 1);
				if (evoStatechart.equals(evoStatechart_bis.replace("Simplified", ""))) {
					mergedRecord[8] = evoNextRecord_bis[2]; // SimplifiedEvosuiteCoverage
					break;
				}
				evoNextRecord_bis = evoCoverageCsvReader_bis.readNext();
			}
			evoCoverageCsvReader_bis.close();
			// Search in the benchmark csv the corresponding row
			FileReader benchmarkFileReader = new FileReader(BENCHMARK_CSV);
			CSVReader benchmarkCsvReader = new CSVReader(benchmarkFileReader);
			String[] benchmarkNextRecord = benchmarkCsvReader.readNext();
			while (benchmarkNextRecord != null) {
				String benchmarkStatechart = benchmarkNextRecord[1];
				if (benchmarkStatechart.equals(evoStatechart)) {
					mergedRecord[0] = benchmarkNextRecord[0] + ".ysc"; // File
					mergedRecord[2] = benchmarkNextRecord[2]; // NumStates
					mergedRecord[3] = benchmarkNextRecord[3]; // AvgDepth
					mergedRecord[4] = benchmarkNextRecord[4]; // MaxDepth
					break;
				}
				benchmarkNextRecord = benchmarkCsvReader.readNext();
			}
			benchmarkCsvReader.close();
			System.out.println("Writing " + evoStatechart + " row");
			writer.append(String.join(",", mergedRecord) + "\n");
			evoNextRecord = evoCoverageCsvReader.readNext();
		}
		System.out.println("\nFinished");
		evoCoverageCsvReader.close();
		writer.close();
		System.out.println("----------------------------");
	}

}
