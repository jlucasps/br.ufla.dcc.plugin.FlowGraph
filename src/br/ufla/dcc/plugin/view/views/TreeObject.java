package br.ufla.dcc.plugin.view.views;

import java.util.ArrayList;

import org.eclipse.jdt.core.IMethod;

public class TreeObject implements Comparable<TreeObject>{

	private IMethod method;
	private ArrayList<IMethod> callers;
	private ArrayList<IMethod> nextMethods;
	
	
	public TreeObject(IMethod method){
		this.method = method;
		this.callers = new ArrayList<IMethod>();
		this.nextMethods = new ArrayList<IMethod>();
	}

	public TreeObject(IMethod method, ArrayList<IMethod> callers){
		this.method = method;
		this.callers = callers;
		this.nextMethods = new ArrayList<IMethod>();
		
	}

	public IMethod getMethod() {
		return method;
	}


	public void setMethod(IMethod method) {
		this.method = method;
	}


	public ArrayList<IMethod> getCallers() {
		return callers;
	}


	public void setCallers(ArrayList<IMethod> callers) {
		this.callers = callers;
	}


	public ArrayList<IMethod> getNextMethods() {
		return nextMethods;
	}

	public void setNextMethods(ArrayList<IMethod> nextMethods) {
		this.nextMethods = nextMethods;
	}

	@Override
	public int compareTo(TreeObject other) {
		
		return (this.getCallers().size() + this.getNextMethods().size())/2 - 
			   (other.getCallers().size() + other.getNextMethods().size())/2;
		
		
	}
	
	
}
