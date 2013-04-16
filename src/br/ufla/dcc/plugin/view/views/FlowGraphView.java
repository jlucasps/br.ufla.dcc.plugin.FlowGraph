package br.ufla.dcc.plugin.view.views;

import java.util.HashMap;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;


public class FlowGraphView extends ViewPart {

	public static final String ID_FLOWGRAPH_GRAPHVIEW = "br.ufla.dcc.plugin.flowGraph.flowGraphView";
	
	private Composite parent;
	private Graph graph;
	
	public FlowGraphView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		this.graph = new Graph(parent, SWT.NONE);
		this.parent = parent;
	}

	
	public void update(TreeObject treeObject){
		
		HashMap<IType, Color> colors = this.getContrastedColors(treeObject);
		
		GraphNode methodNode = new GraphNode(this.getGraph(), SWT.NONE,  treeObject.getMethod().getDeclaringType().getElementName() +"."+ treeObject.getMethod().getElementName());
		methodNode.setBackgroundColor(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
		
		GraphNode[] callerNodes = new GraphNode[treeObject.getCallers().size()];
		
		for(int i=0; i<callerNodes.length; i++){
			callerNodes[i] = new GraphNode(this.getGraph(), SWT.NONE, treeObject.getCallers().get(i).getDeclaringType().getElementName() +"."+ treeObject.getCallers().get(i).getElementName());
			GraphConnection graphConn = new GraphConnection(this.getGraph(), ZestStyles.CONNECTIONS_DIRECTED, callerNodes[i], methodNode);
			graphConn.setText("Inside Relations");
			
			callerNodes[i].setBackgroundColor(colors.get(treeObject.getCallers().get(i).getDeclaringType()));
		
		}
		
		GraphNode[] nextNodes = new GraphNode[treeObject.getNextMethods().size()];
		for(int i=0; i<nextNodes.length; i++){
			nextNodes[i] = new GraphNode(this.getGraph(), SWT.NONE, treeObject.getNextMethods().get(i).getDeclaringType().getElementName() +"."+ treeObject.getNextMethods().get(i).getElementName());
			GraphConnection graphConn = new GraphConnection(this.getGraph(), ZestStyles.CONNECTIONS_DIRECTED, methodNode, nextNodes[i]);
			graphConn.setText("Outside Relations");
			
			nextNodes[i].setBackgroundColor(colors.get(treeObject.getNextMethods().get(i).getDeclaringType()));
		}
		
		//HorizontalTree
		graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	public HashMap<IType, Color> getContrastedColors(TreeObject treeObject){
		
		HashMap<IType, Color> colors= new HashMap<IType, Color>();
		
		colors.put( treeObject.getMethod().getDeclaringType(), this.getParent().getDisplay().getSystemColor(SWT.COLOR_RED));
		
		int currentColor = 5;
		for(int i=0; i<treeObject.getCallers().size(); i++){
			if(!colors.containsKey(treeObject.getCallers().get(i).getDeclaringType())){
				colors.put(treeObject.getCallers().get(i).getDeclaringType(), this.getParent().getDisplay().getSystemColor(currentColor + 2));
				currentColor += 2;
			}
		}
		
		for(int i=0; i<treeObject.getNextMethods().size(); i++){
			if(!colors.containsKey(treeObject.getNextMethods().get(i).getDeclaringType())){
				colors.put(treeObject.getNextMethods().get(i).getDeclaringType(), this.getParent().getDisplay().getSystemColor(currentColor + 2));
				currentColor += 2;
			}
		}
		
		return colors;
	}
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public Composite getParent() {
		return parent;
	}

	public void setParent(Composite parent) {
		this.parent = parent;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	
	
}
