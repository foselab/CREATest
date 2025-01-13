package createst.cli;

import java.io.IOException;

public interface ICliManager {
	
	/**
	 * Parses the command line arguments.
	 *
	 * @param args the command line arguments.
	 * @return the input values obtained parsing and adapting the arguments, null if
	 *         some required option is missing, if some option is wrong or in case
	 *         of -h option
	 * @throws IOException if any IO errors occur.
	 */
	public InputValues parse(String[] args) throws IOException ;
	
}
