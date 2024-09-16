package createst.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * The Class CLIManager.
 */
public class CliManager implements ICliManager {

	/**
	 * Parses the command line arguments.
	 *
	 * @param args the command line arguments.
	 * @return the input values obtained parsing and adapting the arguments, null if
	 *         some required option is missing, if some option is wrong or in case
	 *         of -h option
	 * @throws IOException if any IO errors occur.
	 */
	@Override
	public InputValues parse(String[] args) throws IOException {
		Options options = initOptions();

		// For printing the help message
		HelpFormatter formatter = new HelpFormatter();

		// Parse the arguments according to the specified options
		CommandLineParser parser = new DefaultParser(false);
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
			formatter.printHelp(" ", options);
			return null;
		}

		return manageOptions(cmd, formatter, options);
	}

	/**
	 * Initialises the option.
	 *
	 * @return the initialized options
	 */
	private static Options initOptions() {
		Options options = new Options();
		Option sccPath = Option.builder("s").longOpt("sccPath").argName("arg").hasArg()
				.desc("absolute path of the scc file (.bat in Windows),"
						+ " it is a script file contained in the stand-alone Itemis Create installation directory."
						+ " Required")
				.build();
		Option yscPath = Option.builder("y").longOpt("yscPath").argName("arg").hasArg()
				.desc("absolute path of the CREATE statechart file. It must have .ysc extension."
						+ " Required")
				.build();
		Option evoSearchBudget = Option.builder("b").longOpt("evoSearchBudget").argName("arg").hasArg()
				.desc("the search budget to impose to Evosuite, it must be a positive integer").build();
		Option genArtifacts = new Option("g", "genArtifacts", false, "Generate a .zip containing"
				+ " all the artifacts produced during the process.");
		Option help = new Option("h", "help", false, "print this message.");

		options.addOption(sccPath);
		options.addOption(yscPath);
		options.addOption(evoSearchBudget);
		options.addOption(genArtifacts);
		options.addOption(help);

		return options;
	}

	/**
	 * Manages the parsed options, adapting their values with the aim of simplifying
	 * their use;returns, if possible, their value
	 *
	 * @param cmd       the list of atomic option and value tokens parsed against
	 *                  the specified options
	 * @param formatter the help formatter
	 * @param options   the options
	 * @return the input values, null if some required option is missing, if some
	 *         option is wrong or in case of -h option
	 * @throws IOException if any IO errors occur.
	 */
	private static InputValues manageOptions(CommandLine cmd, HelpFormatter formatter, Options options)
			throws IOException {
		if (cmd.hasOption("h")) {
			formatter.printHelp(" ", options);
			return null;
		} else if (!cmd.hasOption("s") || !cmd.hasOption("y")) {
			System.out.println("Missing required options\n");
			formatter.printHelp(" ", options);
			return null;
		} else {
			return getInputValues(cmd);
		}
	}

	/**
	 * Gets the values from specified options, managing default values for not
	 * present options. All the required options must be present and the -h option
	 * musts not.
	 *
	 * @param cmd the list of atomic option and value tokens
	 * @return the input values, null if some value is not correct (e.g. a path does
	 *         not exist)
	 * @throws IOException if any IO errors occur.
	 */
	private static InputValues getInputValues(CommandLine cmd) throws IOException {
		InputValues parsedArgs = new InputValues();

		// Obtain the canonical path of the scc script and check if it represent a file
		String sccPath = cmd.getOptionValue("s");
		if (Files.exists(Paths.get(sccPath))) {
			File f = new File(sccPath);
			if (f.isFile()) {
				parsedArgs.setSccPath(f.getCanonicalPath());
			} else {
				System.out.println("Error with -s option: \"" + sccPath + "\" is not a file.");
				return null;
			}
		} else {
			System.out.println("Error with -s option: the file \"" + sccPath + "\" does not exist.");
			return null;
		}
		
		// Obtain the canonical path of the ysc script and check if it represent a file
		String yscPath = cmd.getOptionValue("y");
		if (Files.exists(Paths.get(yscPath))) {
			File f = new File(yscPath);
			if (f.isFile() && yscPath.substring(yscPath.lastIndexOf('.')).equals(".ysc")) {
				parsedArgs.setYscPath(f.getCanonicalPath());
			} else {
				System.out.println("Error with -y option: \"" + yscPath + "\" is not a file with .ysc extension.");
				return null;
			}
		} else {
			System.out.println("Error with -y option: the file \"" + yscPath + "\" does not exist.");
			return null;
		}

		// Try to obtain a usable value for searchBudget
		if (cmd.hasOption("b")) {
			try {
				int value = Integer.parseInt(cmd.getOptionValue("evoSearchBudget"));
				if (value <= 0) {
					System.out.println("Error with -b option: the value must be a positive integer");
					return null;
				}
				parsedArgs.setEvoSearchBudget(value);
			} catch (NumberFormatException e) {
				System.out.println("Error with -b option: the value must be a positive integer");
				return null;
			}
		}
		
		// Set the generation of artifacts to true
		if (cmd.hasOption("g"))
			parsedArgs.setGenArtifacts();

		return parsedArgs;
	}

}
