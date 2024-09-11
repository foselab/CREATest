package createst.java.writing;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldDeclarationVisitor extends VoidVisitorAdapter<Void> {
	/**
	 * Visit a field declaration, set the visibility to private if it is protected
	 * or public.
	 *
	 * @param the field declaration
	 * @param arg none
	 */
	@Override
	public void visit(FieldDeclaration node, Void arg) {
		// To ensure child nodes of the current node are also visited
		super.visit(node, arg);
		// Changes field visibility from protected and public to private
		if (node.isProtected() || node.isPublic()) {
			node.setProtected(false);
			node.setPublic(false);
			node.setPrivate(true);
		}
	}
}
