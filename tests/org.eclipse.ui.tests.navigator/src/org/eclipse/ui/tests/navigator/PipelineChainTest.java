/*******************************************************************************
 * Copyright (c) 2009 Fair Isaac Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fair Isaac Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.tests.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.tests.navigator.extension.TestPipelineProvider;

/**
 * @since 3.3
 *
 */
public class PipelineChainTest extends NavigatorTestBase {

	public PipelineChainTest() {
		_navigatorInstanceId = TEST_VIEWER_PIPELINE;
	}
	
	private void _initContent() {
		String[] EXTENSIONS = new String[] {
				COMMON_NAVIGATOR_RESOURCE_EXT,
				TEST_CONTENT_PIPELINE + ".A",
				TEST_CONTENT_PIPELINE + ".B",
				TEST_CONTENT_PIPELINE + ".C",
				TEST_CONTENT_PIPELINE + ".D",
				TEST_CONTENT_PIPELINE + ".E",
				TEST_CONTENT_PIPELINE + ".F",
				TEST_CONTENT_PIPELINE + ".G"
		};
		_contentService.bindExtensions(EXTENSIONS, false);
		_contentService.getActivationService().activateExtensions(EXTENSIONS, true);
	}
	
	private void _initContentWithLabel() {
		String[] EXTENSIONS = new String[] {
				COMMON_NAVIGATOR_RESOURCE_EXT,
				TEST_CONTENT_PIPELINE + ".A",
				TEST_CONTENT_PIPELINE + ".B",
				TEST_CONTENT_PIPELINE + ".C",
				TEST_CONTENT_PIPELINE + ".D",
				TEST_CONTENT_PIPELINE + ".E",
				TEST_CONTENT_PIPELINE + ".F",
				TEST_CONTENT_PIPELINE + ".G",
				TEST_CONTENT_PIPELINE + ".label"
		};
		_contentService.bindExtensions(EXTENSIONS, false);
		_contentService.getActivationService().activateExtensions(EXTENSIONS, true);
	}
	
	public void testPipelinedChildren() throws Exception
	{
		_initContent();
		_testPipelinedChildren();
	}
	
	public void testPipelinedChildrenWithLabel() throws Exception
	{
		_initContentWithLabel();
		_testPipelinedChildren();
	}
	
	private void _testPipelinedChildren() throws CoreException {
		final String NEW_FOLDER = "newFolder_" + System.currentTimeMillis();

		IFolder newFolder = _p1.getFolder(NEW_FOLDER);
		
		TestPipelineProvider.reset();
		newFolder.create(true, true, new NullProgressMonitor());
		TreeItem[] rootItems = _viewer.getTree().getItems();
		_expand(rootItems);
		_viewer.refresh(rootItems[_p1Ind]);
		
		assertEquals("Wrong query sequence for getPipelineChildren", "CGFBDE", 
				TestPipelineProvider.CHILDREN.get(_p1));
	}
	
	/** Verifies that interceptAdd is called in the right sequence */
	public void testInterceptAdd() throws CoreException
	{
		_initContent();
		_testInterceptAdd();
	}
	
	/** Verifies that interceptAdd is called in the right sequence */
	public void testInterceptAddWithLabel() throws CoreException
	{
		_initContentWithLabel();
		_testInterceptAdd();
	}
	
	private void _testInterceptAdd() throws CoreException {
		final String NEW_FOLDER_1 = "newFolder1";
		

		TreeItem[] rootItems = _viewer.getTree().getItems();
		// Make sure p1 children are visible
		_expand(rootItems);
				
		IFolder newFolder1 = _p1.getFolder(NEW_FOLDER_1);
		TestPipelineProvider.reset();
		newFolder1.create(true, true, new NullProgressMonitor());
		
		assertEquals("Wrong query sequence for interceptAdd", "ACGFBDE", 
				TestPipelineProvider.ADDS.get(newFolder1));
	}
	
	/** Verifies that interceptRemove is called in the right sequence */
	public void testInterceptRemove() throws CoreException
	{
		_initContent();
		_testInterceptRemove();
	}
	
	/** Verifies that interceptRemove is called in the right sequence */
	public void testInterceptRemoveWithLabel() throws CoreException
	{
		_initContentWithLabel();
		_testInterceptRemove();
	}
	
	private void _testInterceptRemove() throws CoreException {
		final String NEW_FOLDER_1 = "newFolder1";
		

		TreeItem[] rootItems = _viewer.getTree().getItems();
		// Make sure p1 children are visible
		_expand(rootItems);
				
		IFolder newFolder1 = _p1.getFolder(NEW_FOLDER_1);
		if (! newFolder1.exists()) {
			newFolder1.create(true, true, new NullProgressMonitor());
		}
		
		TestPipelineProvider.reset();
		newFolder1.delete(true, new NullProgressMonitor());
		
		assertEquals("Wrong query sequence for interceptRemove", "ACGFBDE", 
				TestPipelineProvider.REMOVES.get(newFolder1));
	}
	
	/** Verifies that interceptRefresh or interceptUpdate is called in the right sequence */
	public void testInterceptRefreshOnChildTypeChange() throws CoreException
	{
		_initContent();
		_testInterceptRefreshOnChildTypeChange();
	}
	
	/** Verifies that interceptRefresh or interceptUpdate is called in the right sequence */
	public void testInterceptRefreshOnChildTypeChangeWithLabel() throws CoreException
	{
		_initContentWithLabel();
		_testInterceptRefreshOnChildTypeChange();
	}
	
	private void _testInterceptRefreshOnChildTypeChange() throws CoreException {
		
		final IFile file2 = _p2.getFile("file2.txt");
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			
			public void run(IProgressMonitor monitor) throws CoreException {
				file2.delete(true, new NullProgressMonitor());
				file2.create(null, true, null);
			}
		};
		
		TestPipelineProvider.reset();
		ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		
		assertEquals("Wrong query sequence for interceptRefresh/update", "ACGFBDE", 
				TestPipelineProvider.UPDATES.get(file2));
	}
	
	private void _expand(TreeItem[] items)
	{
		for (int i = 0; i < items.length; i++) {
			_viewer.setExpandedState(items[i].getData(), true);
		}
	}


}