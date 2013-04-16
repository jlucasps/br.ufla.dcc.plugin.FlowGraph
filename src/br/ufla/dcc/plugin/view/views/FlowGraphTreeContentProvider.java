package br.ufla.dcc.plugin.view.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class FlowGraphTreeContentProvider  extends ArrayContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
