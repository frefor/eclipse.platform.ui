/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
package org.eclipse.ui.navigator.resources.internal.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.INavigatorContentService;

/**
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is a guarantee neither that this API will
 * work nor that it will remain the same. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * @since 3.2
 */
public class ResourceOpenListener implements IOpenListener {
	OpenFileAction openFileAction ;
	private CommonNavigator commonNavigator;
	private INavigatorContentService contentService;
	
	public void initialize(CommonNavigator aCommonNavigator, INavigatorContentService aContentService) {
		commonNavigator = aCommonNavigator;
		contentService = aContentService;
		openFileAction = new OpenFileAction(commonNavigator.getSite().getPage());
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
	 */
	public void open(OpenEvent event) {
		ISelection selection = event.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			if (openFileAction != null ) {
				IStructuredSelection structureSelection = (IStructuredSelection) selection;
				Object element = structureSelection.getFirstElement();
		        if (element instanceof IFile) {
		            openFileAction.selectionChanged(structureSelection);
		            openFileAction.run();
		        }
			}
		}
	}

}
