package createst.java.writing;

import java.io.IOException;

public interface IJavaWriter {

	public void writeSimplifiedVersion(String javaPath, String simplifiedJavaPath) throws IOException;

	public void callICGenerator(String projectPath, String itemisScc, String sourceDir, String sourceFile,
			String statechartName) throws IOException;

}
