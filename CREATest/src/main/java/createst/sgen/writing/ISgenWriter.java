package createst.sgen.writing;

import java.io.IOException;

public interface ISgenWriter {
	
	public void writeSgen(String projectName, String statechartName, String sgenPath, String targetDir,
			String targetPackage) throws IOException;
	
}
