package createst.java.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import createst.java.writing.IJavaWriter;
import createst.java.writing.JavaWriter;

public class JavaWriterTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String JAVA_NAME = "Javawriting_statechart";
	
	private static IJavaWriter writer;
	
	private static String rootPath;
	private static String javaPath;
	private static String simplifiedJavaPath;
	
	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		writer = new JavaWriter();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		javaPath = RESOURCES_DIR + "\\" + JAVA_NAME + ".java";
		simplifiedJavaPath = rootPath + "\\" + JAVA_NAME + "Simplified.java";
	}
	
	@After
	public void deleteFile() {
		if (Files.exists(Paths.get(simplifiedJavaPath)))
			new File(simplifiedJavaPath).delete();
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullArguments() throws IOException {
		String wrongJavaPath = null;
		writer.writeSimplifiedVersion(wrongJavaPath, simplifiedJavaPath);
	}

	@Test(expected = IOException.class)
	public void testWrongPath() throws IOException {
		String wrongJavaPath = "";
		writer.writeSimplifiedVersion(wrongJavaPath, simplifiedJavaPath);
	}
	
	@Test
	public void testCorrectPath() throws IOException {
		assertTrue(Files.exists(Paths.get(javaPath)));
		assertFalse(Files.exists(Paths.get(simplifiedJavaPath)));
		writer.writeSimplifiedVersion(javaPath, simplifiedJavaPath);
		assertTrue(Files.exists(Paths.get(simplifiedJavaPath)));
		
		CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(simplifiedJavaPath));
		
		for (ClassOrInterfaceDeclaration decl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
			if (!decl.isInterface() && !decl.isStatic() && decl.isPublic()) {
				assertTrue(decl.getNameAsString().endsWith("Simplified"));
			}else {
				assertFalse(decl.getNameAsString().endsWith("Simplified"));
				for (VariableDeclarator field : decl.findAll(VariableDeclarator.class)) {
					if (field.getName().toString().equals("parent")) {
						assertTrue(field.getTypeAsString().endsWith("Simplified"));
					}
				}
			}
		}
		
		for (ConstructorDeclaration decl : cu.findAll(ConstructorDeclaration.class)) {
			if (decl.getParameters().isEmpty()) {
				assertTrue(decl.getNameAsString().endsWith("Simplified"));
			}else {
				assertFalse(decl.getNameAsString().endsWith("Simplified"));
				assertTrue(decl.getParameter(0).getType().asString().endsWith("Simplified"));
			}
		}
		
		for (MethodDeclaration decl : cu.findAll(MethodDeclaration.class)) {
			if (decl.getNameAsString().equals("setOperationCallback") || decl.getNameAsString().equals("setTimerService")
					|| ((ClassOrInterfaceDeclaration) decl.getParentNode().get()).isInterface()) {
				assertTrue(decl.isPublic());
			}else if(decl.getNameAsString().equals("setIsExecuting")) {
				assertTrue(decl.isPrivate());
			}else if(decl.getNameAsString().startsWith("get")) {
				assertTrue(decl.isPrivate());
			}else if(decl.getNameAsString().startsWith("set")) {
				String type = decl.getParameter(0).getTypeAsString();
				if (type.matches("long|double|boolean|String"))
					assertTrue(decl.isPrivate());
				else
					assertTrue(decl.isPublic());
			}
		}
		
		cu.findAll(FieldDeclaration.class);
	}

}
