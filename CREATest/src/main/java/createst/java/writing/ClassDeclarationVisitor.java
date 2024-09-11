package createst.java.writing;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
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
		// nested class) else, change the type of the field named "parent"
		if (node.isPublic() && !node.isStatic()) {
			node.setName(node.getNameAsString() + "Simplified");
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
}
