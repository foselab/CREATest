package createst.java.writing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

/**
 * The Class JavaWriter.
 */
public class JavaWriter implements IJavaWriter {
	
	/**
	 * Generates a simplified version of the java class, changing names and
	 * visibilities
	 *
	 * @param javaPath           the path of the starting .java file
	 * @param simplifiedJavaPath the path where the .java file containing the
	 *                           simplified version is put
	 * @throws IOException if any IO errors occur.
	 */
	@Override
	public void writeSimplifiedVersion(String javaPath, String simplifiedJavaPath) throws IOException {
		CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(javaPath));
		VoidVisitor<Void> classVisitor = new ClassDeclarationVisitor();
		classVisitor.visit(cu, null);
		VoidVisitor<Void> constructorVisitor = new ConstructorDeclarationVisitor();
		constructorVisitor.visit(cu, null);
		VoidVisitor<Void> methodVisitor = new MethodDeclarationVisitor();
		methodVisitor.visit(cu, null);
		VoidVisitor<Void> fieldVisitor = new FieldDeclarationVisitor();
		fieldVisitor.visit(cu, null);
		BufferedWriter writer = new BufferedWriter(new FileWriter(simplifiedJavaPath));
		writer.write(cu.toString());
		writer.close();
	}

	/**
	 * Calls Itemis Create code generators for each .sgen file in the specified
	 * project.
	 *
	 * @param projectPath    the path of the project
	 * @param itemisScc      the path of the scc.bat file
	 * @param sourceDir      the path of the source directory
	 * @param sourceFile     the name of the source file
	 * @param statechartName the name of the statechart
	 * @throws IOException if any IO errors occur.
	 */
	@Override
	public void callICGenerator(String projectPath, String itemisScc, String sourceDir, String sourceFile,
			String statechartName) throws IOException {
		Process p = new ProcessBuilder(itemisScc, "-m", sourceFile + "," + statechartName + ".sgen")
				.redirectErrorStream(true)
				.directory(new File(projectPath + "\\" + sourceDir))
				.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.contains("done."))
				break;
		}
		reader.close();
	}
}
