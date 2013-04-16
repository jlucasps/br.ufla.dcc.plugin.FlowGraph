package br.ufla.dcc.plugin.model.analysis.flowgraphanalysis;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import java.util.ArrayList;

public class MethodVisitor extends ASTVisitor{

	ArrayList<MethodDeclaration> methodDeclarations = new ArrayList<MethodDeclaration>();
	
	
	@Override
	public boolean visit(MethodDeclaration node){
		this.methodDeclarations.add(node);

		
		return super.visit(node);
	}


	public ArrayList<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarations;
	}


	public void setMethodDeclarations(
			ArrayList<MethodDeclaration> methodDeclarations) {
		this.methodDeclarations = methodDeclarations;
	}
	
	
	
}
