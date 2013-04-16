package br.ufla.dcc.plugin.view.action;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import br.ufla.dcc.plugin.controller.FlowGraphController;
import br.ufla.dcc.plugin.view.views.MethodsCountView;


public class FlowGraphActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	private FlowGraphController flowGraphController;
	
	ListSelectionDialog dialog;
	
	@Override
	public void run(IAction action) {

		IStructuredSelection selection = (IStructuredSelection)this.getWindow().getActivePage().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		
		Object firstElement = selection.getFirstElement();

		if (firstElement instanceof IJavaProject) {
			
			IJavaProject project = (IJavaProject) firstElement;
			
			if(window.getActivePage().findViewReference(MethodsCountView.ID_FLOWGRAPH_METHODS_COUNT_VIEW) != null){
				window.getActivePage().hideView(window.getActivePage().findViewReference(MethodsCountView.ID_FLOWGRAPH_METHODS_COUNT_VIEW));
			}
			try {
				window.getActivePage().showView(MethodsCountView.ID_FLOWGRAPH_METHODS_COUNT_VIEW);
			} catch (PartInitException e) {
				System.out.println(e);
				e.printStackTrace();
			}
			
			MethodsCountView methodsCountView = (MethodsCountView) window.getActivePage().findView(MethodsCountView.ID_FLOWGRAPH_METHODS_COUNT_VIEW);
			

			this.flowGraphController = new FlowGraphController();
			methodsCountView.setFlowGraphController(this.flowGraphController);
			
			
			this.getFlowGraphController().runFlowGraph(project);
		
			
	        methodsCountView.update();
	        String message = "Análise no projeto " + project.getElementName() + " concluída.";
	        methodsCountView.updateToolBar(message , IMessageProvider.INFORMATION);
			
		}else{
			String title = "Flow Graph Analysis";
			String message = "Please, select one project on Package Explorer!";
			
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
			
		}
		
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
		BasicConfigurator.configure();
		
	}

	public FlowGraphController getFlowGraphController() {
		return flowGraphController;
	}

	public void setFlowGraphController(FlowGraphController flowGraphController) {
		this.flowGraphController = flowGraphController;
	}

	
	
}
