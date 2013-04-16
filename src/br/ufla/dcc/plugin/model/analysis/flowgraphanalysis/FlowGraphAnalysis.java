package br.ufla.dcc.plugin.model.analysis.flowgraphanalysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ufla.dcc.plugin.view.views.TreeObject;

public class FlowGraphAnalysis extends Thread {

	private static Logger log = Logger.getLogger(FlowGraphAnalysis.class);

	private IJavaProject project;
	private HashMap<IMethod, IMethodBinding> allDeclaredMethods;
	private ArrayList<InsideRelation> insides;
	private ArrayList<OutsideRelation> outsides;
	private ArrayList<TreeObject> input;

	public FlowGraphAnalysis(IJavaProject project) {
		this.project = project;
		this.allDeclaredMethods = new HashMap<IMethod, IMethodBinding>();

		this.insides = new ArrayList<InsideRelation>();
		this.outsides = new ArrayList<OutsideRelation>();

	}

	public void run() {
		try {

			long begin = System.currentTimeMillis();
			this.startFlowGraph();
			long end = System.currentTimeMillis();

			System.out.println("Execution time: " + (end - begin) / 1000);
		} catch (JavaModelException modelException) {
			System.out.println(modelException.getMessage());
		} catch (CoreException coreException) {
			System.out.println(coreException.getMessage());
		}
	}

	public void startFlowGraph() throws CoreException, JavaModelException {

		log.info("An�lise Iniciada: " + Calendar.getInstance().getTime());

		
		this.readProjectInfo(this.getProject());
	
		log.info("An�lise Concluida: " + Calendar.getInstance().getTime());
		
//		Collections.sort(this.getInsides());
//		Collections.reverse(this.getInsides());
		
		for(InsideRelation inside : this.getInsides()){
			System.out.print( inside.getMethod().getDeclaringType().getElementName() +"."+ inside.getMethod().getElementName() + " <- ");
			for(IMethod caller : inside.getCallers()){
				System.out.print(caller.getDeclaringType().getElementName() +"."+ caller.getElementName()+ " ");
			}
			System.out.println();
		}
		
//		Collections.sort(this.getOutsides());
//		Collections.reverse(this.getOutsides());
		
		for(OutsideRelation outside : this.getOutsides()){
			System.out.print( outside.getCurrentMethod().getDeclaringType().getElementName() +"."+ outside.getCurrentMethod().getElementName() + " -> ");

			for(IMethod next : outside.getNextMethods()){
				System.out.print(next.getDeclaringType().getElementName() +"."+ next.getElementName()+ " ");
			}
			System.out.println();
		}
		
		this.input = new ArrayList<TreeObject>();
		
		for(InsideRelation inside : this.getInsides()){
			this.input.add(new TreeObject(inside.getMethod(), inside.getCallers()));
		}

		for(OutsideRelation outside : this.getOutsides()){
			if(this.inputContains(outside.getCurrentMethod())){
				TreeObject tObj = this.getInputOf(outside.getCurrentMethod());
				tObj.setNextMethods(outside.getNextMethods());
			}
		}
		
		Collections.sort(this.getInput());
		Collections.reverse(this.getInput());
		System.out.println(this.getInput().size());
	}

	public boolean inputContains(IMethod method){
		for(TreeObject treeObject : this.getInput()){
			if(treeObject.getMethod().getDeclaringType().equals(method.getDeclaringType()) &&
			   treeObject.getMethod().equals(method)){
				return true;
			}
		}
		return false;
	}
	
	public TreeObject getInputOf(IMethod method){
		for(int i=0; i<this.getInput().size(); i++){
			
			if(this.getInput().get(i).getMethod().getDeclaringType().equals(method.getDeclaringType()) &&
					this.getInput().get(i).getMethod().equals(method)){
					
				return this.getInput().get(i);
			}
			
		}
		return null;
	}

	private void readProjectInfo(IJavaProject javaProject)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();

		System.out.println("Analisando m�todos invocados.....");

		for (IPackageFragment mypackage : packages) {

			/* Analisa pacotes apenas do tipo source, descosidera os bin�rios */
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {

				System.out.println("Package: " + mypackage.getElementName());

				/* Analisa as CompilationUnits do pacote em quest�o. */
				this.readIPackageInfo(mypackage);

			}

		}

	}

	private void readIPackageInfo(IPackageFragment mypackage)
			throws JavaModelException {

		/* Para todas as CompilationsUnit do pacote */
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {

			System.out.println("CompilationUnit: " + unit.getElementName());

			/* Parse da CompilationUnit */
			CompilationUnit compUnitParsed = this.parseCompilationUnit(unit);

			/* Para todas as classes da CompilationUnit */
			for (Object obj : compUnitParsed.types()) {

				/*
				 * Uma vez que a chamada compUnitParsed.types() pode retornar
				 * AnnotationTypeDeclaration, EnumDeclaration, TypeDeclaration,
				 * o if() abaixo considera apenas os types do tipo
				 * TypeDeclaration
				 */
				if (obj instanceof TypeDeclaration) {

					TypeDeclaration type = (TypeDeclaration) obj;
					System.out.println("\tType: " + type.getName());

					this.readTypeInfo(type);

				}
			}
		}
	}
	
	private void readTypeInfo(TypeDeclaration type){
		/* Para todos os m�todos e contrutores declarados classe type */
		for (Object ob : type.getMethods()) {

			MethodDeclaration callerDeclaration = (MethodDeclaration) ob;
			System.out.print("\t\tMethod body: " + callerDeclaration.getName() + " { ");
			
			IMethodBinding callerBinding = callerDeclaration.resolveBinding();
			
			if(callerBinding != null){
				IMethod callerMethod = (IMethod) callerBinding.getJavaElement();

				/* Armazena todos os m�todos e seus respectivos
				 * bindings em um HashMap */
				this.getAllDeclaredMethods().put(callerMethod, callerBinding);
				
				/*Pattern Visitor j� implementado pela API do Eclipse.*/
				InvocationVisitor visitor = new InvocationVisitor();
				/* O Visitor � executado no corpo do m�todo para
				 * recuperar todas as invoca��es de m�todos */
				callerDeclaration.accept(visitor);

				/* Para cada m�todo invocado em caller */
//				for (MethodInvocation invoked : visitor.getInvocations()) {
				for(int i=0; i<visitor.getInvocations().size(); i++){
				
					this.printFullMethodName(visitor.getInvocations().get(i));
					
					if(i == 0){
						this.addInsideRelation(callerMethod, visitor.getInvocations().get(i));
					}
					
					if(i < visitor.getInvocations().size()-1){
						this.addOutsideRelation(visitor.getInvocations().get(i), 
												visitor.getInvocations().get(i+1));
					}
				}
			}
			
			System.out.println(" } ");
		}
	}
	
	public void addInsideRelation(IMethod callerMethod, MethodInvocation invoked){
		/*Resolve o binding para o m�todo invocado*/
		IMethodBinding invokedBinding = invoked.resolveMethodBinding();
		
		if(invokedBinding != null){
			if(invokedBinding.getJavaElement() instanceof IMethod){
				IMethod invokedMethod = (IMethod) invokedBinding.getJavaElement();
				
				if(this.getInsideRelationOf(invokedMethod) == null){
					
					InsideRelation inside = new InsideRelation(invokedMethod, callerMethod);
					this.insides.add(inside);
					
				}else if(! this.getInsideRelationOf(invokedMethod).getCallers().contains(callerMethod)  ){

					this.getInsideRelationOf(invokedMethod).getCallers().add(callerMethod);
				
				}
			}
		}
	}
	
	public void addOutsideRelation(MethodInvocation current, MethodInvocation next){
		/*Resolve o binding para o m�todo invocado*/
		IMethodBinding currentBinding = current.resolveMethodBinding();
		IMethodBinding nextBinding = next.resolveMethodBinding();
		
		if(currentBinding != null && nextBinding != null){
			
			if(currentBinding.getJavaElement() instanceof IMethod &&
			   nextBinding.getJavaElement() instanceof IMethod){
				
				IMethod currentMethod = (IMethod) currentBinding.getJavaElement();
				IMethod nextMethod = (IMethod) nextBinding.getJavaElement();
				
				if(this.getOutsideRelationOf(currentMethod) == null){
					
					OutsideRelation outside = new OutsideRelation(currentMethod, nextMethod);
					this.getOutsides().add(outside);
					
				}else if(! this.getOutsideRelationOf(currentMethod).getNextMethods().contains(nextMethod) ){
					this.getOutsideRelationOf(currentMethod).getNextMethods().add(nextMethod);
				}
			}
		}
	}
	
	public void printFullMethodName(MethodInvocation invoked){
		/*Resolve o binding para o m�todo invocado*/
		IMethodBinding invokedBinding = invoked.resolveMethodBinding();
		
		if(invokedBinding != null){
			if(invokedBinding.getJavaElement() instanceof IMethod){
				IMethod invokedMethod = (IMethod) invokedBinding.getJavaElement();
				
				System.out.print(invokedMethod.getParent().getElementName() + "." + invokedMethod.getElementName() + " ");
				
			}
		}
	}
	
	/*Faz o parser da CompilationUnit*/
	public CompilationUnit parseCompilationUnit(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
	
	
	public InsideRelation getInsideRelationOf(IMethod method){
		for(int i=0; i<this.getInsides().size(); i++){
			if(this.getInsides().get(i).getMethod().getElementName().equals(method.getElementName()) &&
			   this.getInsides().get(i).getMethod().getDeclaringType().equals(method.getDeclaringType()) ){
				
				return this.getInsides().get(i);
			}
		}
		return null;
	}
	
	public OutsideRelation getOutsideRelationOf(IMethod method){
		for(int i=0; i<this.getOutsides().size(); i++){
			
			if(this.getOutsides().get(i).getCurrentMethod().getElementName().equals(method.getElementName()) &&
			   this.getOutsides().get(i).getCurrentMethod().getDeclaringType().equals(method.getDeclaringType()) ){
				
				return this.getOutsides().get(i);
				
			}
		}
		return null;
	}
	
	public MethodDeclaration getMethodDeclaration(IMethod method, CompilationUnit unit){
		
		for (Object obj : unit.types()) {
			/*
			 * Uma vez que a chamada compUnitParsed.types() pode retornar
			 * AnnotationTypeDeclaration, EnumDeclaration, TypeDeclaration, 
			 * o if() abaixo considera apenas os types do tipo TypeDeclaration 
			 */
			if(obj instanceof TypeDeclaration ){
				
				TypeDeclaration type = (TypeDeclaration) obj;
				
//				System.out.println(type.getName().toString() + " == " + method.getDeclaringType().getElementName());
				if(type.getName().toString().equals(method.getDeclaringType().getElementName())){
				
					for (Object ob : type.getMethods()) {
						MethodDeclaration methodDeclaration = (MethodDeclaration) ob;
						
						if(method.getElementName().equals(methodDeclaration.getName().toString())){
							return methodDeclaration;
						}
						
					}
				}
			}
		}
		return null;
	}
	
	public IJavaProject getProject() {
		return project;
	}

	public void setProject(IJavaProject project) {
		this.project = project;
	}

	public HashMap<IMethod, IMethodBinding> getAllDeclaredMethods() {
		return allDeclaredMethods;
	}

	public void setAllDeclaredMethods(
			HashMap<IMethod, IMethodBinding> allDeclaredMethods) {
		this.allDeclaredMethods = allDeclaredMethods;
	}

	public ArrayList<InsideRelation> getInsides() {
		return insides;
	}

	public void setInsides(ArrayList<InsideRelation> insides) {
		this.insides = insides;
	}

	public ArrayList<OutsideRelation> getOutsides() {
		return outsides;
	}

	public void setOutsides(ArrayList<OutsideRelation> outsides) {
		this.outsides = outsides;
	}

	public ArrayList<TreeObject> getInput() {
		return input;
	}

	public void setInput(ArrayList<TreeObject> input) {
		this.input = input;
	}
	

}
