package br.ufla.dcc.plugin.view.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import br.ufla.dcc.plugin.controller.FlowGraphController;

public class MethodsCountView extends ViewPart {

	public static final String ID_FLOWGRAPH_METHODS_COUNT_VIEW = "br.ufla.dcc.plugin.flowGraph.methodsCountView";
	
//	private TreeViewer treeViewer;
	private FlowGraphController flowGraphController;
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private GridData gd;

	private List listMethods;
	
	private Label lblInsideRelations;
	private Text txtInsideRelations;
	private Label lblTotalInsideRelations;
	private Text txtTotalInsideRelations;
	private Label lblClassesInsideRelations;
	private Text txtClassesInsideRelations;
	
	private Label lblOutsideRelations;
	private Text txtOutsideRelations;
	private Label lblTotalOutsideRelations;
	private Text txtTotalOutsideRelations;
	private Label lblClassesOutsideRelations;
	private Text txtClassesOutsideRelations;
	
	private Button btnGraph;
	
	private static Map<String, TreeObject> stringToTreeObject;
	
	public MethodsCountView() {
	}

	@Override
	public void createPartControl(Composite parent) {
//		this.treeViewer = new TreeViewer(parent, SWT.SINGLE);
//		this.treeViewer.setContentProvider(new FlowGraphTreeContentProvider());
//		this.treeViewer.setLabelProvider(new FlowGraphLabelProvider());
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		toolkit.decorateFormHeading(form.getForm());
		form.setText("Análise do Grafo de Fluxo de Controle");
		GridLayout gridLayout = new GridLayout(3, false);
		form.getBody().setLayout(gridLayout);
		
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		
		
		listMethods = new List(this.form.getBody(), SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL );
		gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.verticalSpan = 7;
		int listHeight = listMethods.getItemHeight() * 12;
		Rectangle trim = listMethods.computeTrim(0, 0, 0, listHeight);
		gd.heightHint = trim.height;
		listMethods.setLayoutData(gd);
		
//		listMethods.setItems(new String[] { "Best of Breed", "Prettiest Female",
//				"Handsomest Male", "Best Dressed", "Fluffiest Ears",
//				"Most Colors", "Best Performer", "Loudest Bark",
//				"Best Behaved", "Prettiest Eyes", "Most Hair", "Longest Tail",
//				"Cutest Trick","Best of Breed", "Prettiest Female",
//				"Handsomest Male", "Best Dressed", "Fluffiest Ears",
//				"Most Colors", "Best Performer", "Loudest Bark",
//				"Best Behaved", "Prettiest Eyes", "Most Hair", "Longest Tail",
//				"Cutest Trick" });
		
		form.pack(true);
		listMethods.pack(true);
		
		this.lblInsideRelations = toolkit.createLabel(this.form.getBody(), "Relações Internas: ");
		this.txtInsideRelations = new Text(this.form.getBody(), SWT.NONE);
		this.lblTotalInsideRelations = toolkit.createLabel(this.form.getBody(), "Total: ");
		this.txtTotalInsideRelations = new Text(this.form.getBody(), SWT.BORDER);
		this.lblClassesInsideRelations = toolkit.createLabel(this.form.getBody(), "Classes: ");
		this.txtClassesInsideRelations = new Text(this.form.getBody(), SWT.BORDER);
		
		this.lblOutsideRelations = toolkit.createLabel(this.form.getBody(), "Relações Externas: ");
		this.txtOutsideRelations = new Text(this.form.getBody(), SWT.NONE);
		this.lblTotalOutsideRelations = toolkit.createLabel(this.form.getBody(), "Total: ");
		this.txtTotalOutsideRelations = new Text(this.form.getBody(), SWT.BORDER);
		this.lblClassesOutsideRelations = toolkit.createLabel(this.form.getBody(), "Classes: ");
		this.txtClassesOutsideRelations = new Text(this.form.getBody(), SWT.BORDER);
		
		btnGraph = toolkit.createButton(form.getBody(), "Visualizar grafo", SWT.PUSH);
		
		listMethods.addListener(SWT.Selection, new ClickListener(this, listMethods));
		
		
		
		
	}

	@Override
	public void setFocus() {
//		this.treeViewer.getControl().setFocus();

	}

//	public TreeViewer getTreeViewer() {
//		return treeViewer;
//	}
//
//	public void setTreeViewer(TreeViewer treeViewer) {
//		this.treeViewer = treeViewer;
//	}

	public FlowGraphController getFlowGraphController() {
		return flowGraphController;
	}

	public void setFlowGraphController(FlowGraphController flowGraphController) {
		this.flowGraphController = flowGraphController;

		btnGraph.addListener(SWT.Selection, new GraphListener(this.flowGraphController, listMethods));
		
	}



	public void update(){
		
		ArrayList<TreeObject> input = this.getFlowGraphController().getFlowGraphAnalysis().getInput();

		MethodsCountView.stringToTreeObject = new HashMap<String, TreeObject>();
		
		String[] itens = new String[input.size()];
		
		for(int i=0; i<input.size(); i++){
			String label = input.get(i).getMethod().getDeclaringType().getElementName() + "."+input.get(i).getMethod().getElementName();
			itens[i] = label;
			MethodsCountView.stringToTreeObject.put(label, input.get(i));
		}
		
		this.listMethods.setItems(itens);
		
//		this.treeViewer.setInput(input);
//		this.treeViewer.refresh();
		
	}
	
	private void updateFields(String methodName){
		TreeObject parent = MethodsCountView.stringToTreeObject.get(methodName);
		
		
		this.txtTotalInsideRelations.setText(String.valueOf(parent.getCallers().size()));
		this.txtClassesInsideRelations.setText(String.valueOf(this.countNumberDiffClassesInside(methodName)));
		
		this.txtTotalOutsideRelations.setText(String.valueOf(parent.getNextMethods().size()));
		this.txtClassesOutsideRelations.setText(String.valueOf(this.countNumberDiffClassesOutside(methodName)));
		
	}
	
	private int countNumberDiffClassesInside(String methodName){
		TreeObject parent = MethodsCountView.stringToTreeObject.get(methodName);
		
		Set<String> set = new HashSet<String>();
		for(int i=0; i<parent.getCallers().size(); i++){
			set.add(parent.getCallers().get(i).getDeclaringType().getFullyQualifiedName());
		}
		
		return set.size();
	}
	
	private int countNumberDiffClassesOutside(String methodName){
		TreeObject parent = MethodsCountView.stringToTreeObject.get(methodName);
		
		Set<String> set = new HashSet<String>();
		for(int i=0; i<parent.getNextMethods().size(); i++){
			set.add(parent.getNextMethods().get(i).getDeclaringType().getFullyQualifiedName());
		}
		
		return set.size();
	}
	
	public void updateToolBar(String message, int type){
		form.getToolBarManager().update(true);
		form.setMessage(message, type);
	}
	
	class ClickListener implements Listener{

		private MethodsCountView methodsCountView;
		private List list;
		
		public ClickListener(MethodsCountView methodsCountView, List list){
			this.methodsCountView = methodsCountView;
			this.list = list;
			
		}
		
		@Override
		public void handleEvent(Event event) {
			if(event.type == SWT.Selection){
				this.methodsCountView.updateFields(list.getItem(list.getSelectionIndex()));
			}
			
		}
		
	}
	
	class GraphListener implements Listener{

		FlowGraphController flowGraphController;
		List list;
		
		public GraphListener(FlowGraphController flowGraphController, List list){
			this.flowGraphController = flowGraphController;
			this.list = list;
		}
		
		@Override
		public void handleEvent(Event event) {
			if(event.type == SWT.Selection){
				
				TreeObject treeObject = MethodsCountView.stringToTreeObject.get(list.getItem(list.getSelectionIndex()));
				
				
//				System.out.println("\n"+treeObject.getMethod().getElementName());
//				
//				for(TreeObject child : treeObject.getChildren()){
//					System.out.print(child.getMethod().getElementName() + " ");
//				}
				
				/*
				 * Acho q a alteração no outro view deve ser feita aqui. 
				 */
				if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(FlowGraphView.ID_FLOWGRAPH_GRAPHVIEW) != null){
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(FlowGraphView.ID_FLOWGRAPH_GRAPHVIEW));
				}
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(FlowGraphView.ID_FLOWGRAPH_GRAPHVIEW);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
				
				FlowGraphView graphView = (FlowGraphView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(FlowGraphView.ID_FLOWGRAPH_GRAPHVIEW);
				graphView.update(treeObject);
				/*Fim da manipulação do outro view*/
				
				
				if(this.flowGraphController.getFlowGraphAnalysis().getAllDeclaredMethods().keySet().contains(treeObject.getMethod())){
					if(this.flowGraphController.getMarkers().containsKey(treeObject.getMethod())){
//						System.out.println(treeObject.getMethod().getDeclaringType().getElementName() + "."+treeObject.getMethod().getElementName());
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//						IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
						IMarker marker = this.flowGraphController.getMarkers().get(treeObject.getMethod());
						
						
						IFile file = (IFile) treeObject.getMethod().getDeclaringType().getResource();
//						System.out.println(file.getName());
						try {
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
						IDE.gotoMarker(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), marker );
					}
					
				}else{
					String title = "FanIn Information!";
					String message = "The method " + treeObject.getMethod().getElementName() + " wasn't declared by you!";
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
				
				}
				
			}
			
			
		}
		
	}
	
	
	

	
	
}
