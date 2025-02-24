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
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	@Override
	public void callICGenerator(String projectPath, String itemisScc, String sourceDir) throws IOException {
		Process p = new ProcessBuilder(itemisScc)
				.redirectErrorStream(true)
				.directory(new File(projectPath + File.separator + sourceDir))
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
