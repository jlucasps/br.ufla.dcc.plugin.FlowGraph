package br.ufla.dcc.plugin.controller;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ufla.dcc.plugin.model.analysis.flowgraphanalysis.FlowGraphAnalysis;
import br.ufla.dcc.plugin.view.views.TreeObject;

public class FlowGraphController {

	private static String ID_HIGH_FLOWGRAPH_MARKER = "br.ufla.dcc.plugin.flowGraph.view.highFlowGraphMarker";
	
	private FlowGraphAnalysis flowGraphAnalysis;
	private HashMap<IMethod, IMarker> markers;
	
	
	public FlowGraphController() {
		this.markers = new HashMap<IMethod, IMarker>();
	}

	public void runFlowGraph(IJavaProject project){
		this.flowGraphAnalysis = new FlowGraphAnalysis(project);
		
		this.flowGraphAnalysis.start();
		try {
		
			this.flowGraphAnalysis.join();
		
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		this.createMarkers();
		
	}
	
	public void createMarkers(){
		
		for(TreeObject treeObject : this.getFlowGraphAnalysis().getInput()){
			IMethod method = treeObject.getMethod();
			
			if(this.getFlowGraphAnalysis().getAllDeclaredMethods().keySet().contains(method)){
				
				try {
					
					IFile file = (IFile) method.getCompilationUnit().getCorrespondingResource();
					
					IMarker marker = file.createMarker(FlowGraphController.ID_HIGH_FLOWGRAPH_MARKER);
					
					marker.setAttribute(IMarker.MESSAGE, "Valor Flow Graph: " + ((treeObject.getCallers().size() + treeObject.getNextMethods().size())/2) );
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					
					CompilationUnit unit = this.getFlowGraphAnalysis().parseCompilationUnit(method.getCompilationUnit());
					MethodDeclaration methodDeclaration = this.getFlowGraphAnalysis().getMethodDeclaration(method, unit);

					ASTNode node = (ASTNode) methodDeclaration;

					marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
					marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
					
					marker.setAttribute(IMarker.LINE_NUMBER, unit.getLineNumber(node.getStartPosition()));
					
					this.markers.put(method, marker);
					
					
				} catch (CoreException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				
			}
		}
	}

	public FlowGraphAnalysis getFlowGraphAnalysis() {
		return flowGraphAnalysis;
	}
	
	public void setFlowGraphAnalysis(FlowGraphAnalysis flowGraphAnalysis) {
		this.flowGraphAnalysis = flowGraphAnalysis;
	}
	
	public HashMap<IMethod, IMarker> getMarkers() {
		return markers;
	}
	
	public void setMarkers(HashMap<IMethod, IMarker> markers) {
		this.markers = markers;
	}

	
	
}
