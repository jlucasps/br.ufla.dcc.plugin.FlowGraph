package br.ufla.dcc.plugin.model.analysis.flowgraphanalysis;

import java.util.ArrayList;

import org.eclipse.jdt.core.IMethod;

public class InsideRelation implements Comparable<InsideRelation>{

	private IMethod method;
	private ArrayList<IMethod> callers;
	
	public InsideRelation(IMethod method, IMethod caller){
		this.method = method;
		this.callers = new ArrayList<IMethod>();
		this.callers.add(caller);
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


	@Override
	public int compareTo(InsideRelation other) {
		return this.getCallers().size() - other.getCallers().size();
	}
	
	
	
}
