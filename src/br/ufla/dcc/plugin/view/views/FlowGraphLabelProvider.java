package br.ufla.dcc.plugin.view.views;

import org.eclipse.jface.viewers.LabelProvider;

public class FlowGraphLabelProvider extends LabelProvider{

	
	public String getText(Object element){
		TreeObject treeObject = (TreeObject) element;
		
		return treeObject.getMethod().getDeclaringType().getElementName() + "." +
			   treeObject.getMethod().getElementName() + " ( " +
			   treeObject.getCallers().size() + " / " +
			   treeObject.getNextMethods().size() +" )";
		
		
	}
}
