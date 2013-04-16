package br.ufla.dcc.plugin.model.analysis.flowgraphanalysis;

import java.util.ArrayList;

import org.eclipse.jdt.core.IMethod;

public class OutsideRelation implements Comparable<OutsideRelation>{
	
	private IMethod currentMethod;
	private ArrayList<IMethod> nextMethods;
	
	public OutsideRelation(IMethod currentMethod, IMethod nextMethod){
		this.currentMethod = currentMethod;
		this.nextMethods = new ArrayList<IMethod>();
		this.nextMethods.add(nextMethod);
	}
	
	public IMethod getCurrentMethod() {
		return currentMethod;
	}
	public void setCurrentMethod(IMethod currentMethod) {
		this.currentMethod = currentMethod;
	}
	public ArrayList<IMethod> getNextMethods() {
		return nextMethods;
	}
	public void setNextMethods(ArrayList<IMethod> nextMethods) {
		this.nextMethods = nextMethods;
	}

	@Override
	public int compareTo(OutsideRelation other) {
		
		return this.getNextMethods().size() - other.getNextMethods().size();
		
	}
	
	
}
