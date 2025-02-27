package createst.java.writing;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class MethodDeclarationVisitor.
 */
public class MethodDeclarationVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * Visit a method declaration, set the visibility to private if it is protected
	 * or if it is a variable public set method.
	 *
	 * @param the method declaration
	 * @param arg none
	 */
	@Override
	public void visit(MethodDeclaration node, Void arg) {
		// To ensure child nodes of the current node are also visited
		super.visit(node, arg);
		// Do nothing if the method is setOperationCallback or setTimerService or if it
		// is a method inside an Interface or an ObjectCreationExpr
		String methodName = node.getNameAsString();
		Node parent = node.getParentNode().get();
		if (methodName.equals("setOperationCallback") || methodName.equals("setTimerService")
				|| parent instanceof ObjectCreationExpr
				|| parent instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration) parent).isInterface())
			return;
		// Changes method visibility from protected to private
		if (node.isProtected()) {
			node.setProtected(false);
			node.setPrivate(true);
		}
		// Changes method visibility from public to private if it is a set or get method
		if (node.isPublic() && (methodName.startsWith("set") || methodName.startsWith("get"))) {
			// For set methods, change visibility only if the parameter's type is an Itemis
			// Create built-in type or if the method name is setIsExecuting
			if (methodName.startsWith("set") && !methodName.equals("setIsExecuting")) {
				String type = node.getParameter(0).getTypeAsString();
				if (!type.matches("long|double|boolean|String"))
					return;
			}
			node.setPublic(false);
			node.setPrivate(true);
		}
	}

}
