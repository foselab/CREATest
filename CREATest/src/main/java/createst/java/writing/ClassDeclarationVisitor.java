package createst.java.writing;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class ClassDeclarationVisitor.
 */
public class ClassDeclarationVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * Visit a class decalration, adding "Simplified" at the end of the name if it
	 * is the public class, else (it's a nested class) change the type of the field
	 * named "parent".
	 *
	 * @param the class declaration
	 * @param arg none
	 */
	@Override
	public void visit(ClassOrInterfaceDeclaration node, Void arg) {
		// To ensure child nodes of the current node are also visited
		super.visit(node, arg);
		// There is no reason to change interface name. Interfaces are used if there are
		// operations in the statechart
		if (node.isInterface())
			return;
		// Changes class name only if the class is public and not static (i.e. not a
		// nested class) and add a proceedCycles() method if necessary,
		// else, change the type of the field named "parent"
		if (node.isPublic() && !node.isStatic()) {
			node.setName(node.getNameAsString() + "Simplified");
			// Checks if the statechart execution is cycle based, i.e. if the class do NOT
			// contains a triggerWithoutEvent method, if so, add the proceedCycles() method
			List<MethodDeclaration> methodDeclarations = node.findAll(MethodDeclaration.class);
			boolean hasTrigger = false;
			for (MethodDeclaration methodDeclaration : methodDeclarations) {
				if (methodDeclaration.getName().toString().equals("triggerWithoutEvent")) {
					hasTrigger = true;
					break;
				}
			}
			if (!hasTrigger)
				addProceedCyclesMethod(node);
		} else {
			List<VariableDeclarator> fields = node.findAll(VariableDeclarator.class);
			for (VariableDeclarator field : fields) {
				if (field.getName().toString().equals("parent")) {
					field.setType(field.getTypeAsString() + "Simplified");
					break;
				}
			}
		}
	}

	/**
	 * Add the following method:
	 * 
	 * <pre>
	 * public void proceedCycles(int nCycles) { for (int i = 0; i < nCycles; i++) {
	 * runCycle(); } }
	 * 
	 * <pre>
	 * 
	 * @param the node representing the class to which the method will be added
	 */
	private void addProceedCyclesMethod(ClassOrInterfaceDeclaration node) {
		// Adds a new method to the class
		MethodDeclaration newMethod = node.addMethod("proceedCycles", Keyword.PUBLIC);
		newMethod.addParameter(int.class, "nCycles");
		newMethod.setType(void.class);

		// Creates a for statement
		VariableDeclarationExpr initialization = new VariableDeclarationExpr(new VariableDeclarator(
				new PrimitiveType(PrimitiveType.Primitive.INT), "i", new IntegerLiteralExpr("0")));
		BinaryExpr compare = new BinaryExpr(new NameExpr("i"), new NameExpr("nCycles"), BinaryExpr.Operator.LESS);
		UnaryExpr update = new UnaryExpr(new NameExpr("i"), UnaryExpr.Operator.POSTFIX_INCREMENT);
		BlockStmt body = new BlockStmt();
		body.addStatement(new ExpressionStmt(new MethodCallExpr("runCycle")));
		ForStmt forStmt = new ForStmt(new NodeList<>(initialization), compare, new NodeList<>(update), body);

		// Adds the for loop to the method body
		BlockStmt methodBody = new BlockStmt();
		methodBody.addStatement(forStmt);
		newMethod.setBody(methodBody);
	}

}
