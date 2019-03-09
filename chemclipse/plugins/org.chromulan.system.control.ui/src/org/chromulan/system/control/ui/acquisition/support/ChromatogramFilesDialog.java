/*******************************************************************************
 * Copyright (c) 2015, 2018 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.acquisition.support;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.IAcquisitionSaver;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ChromatogramFilesDialog extends Dialog {

	private IAcquisitionSaver acquisitionSaver;
	private List<IProcessingInfo> chromatogramExportConverterProcessingInfos;

	public ChromatogramFilesDialog(Shell parentShell, IAcquisitionSaver acquisitionSaver) {
		super(parentShell);
		this.acquisitionSaver = acquisitionSaver;
		chromatogramExportConverterProcessingInfos = new LinkedList<>();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite)super.createDialogArea(parent);
		Composite composite = new Composite(area, SWT.NONE);
		area.setLayout(new FillLayout());
		Table table = new Table(composite, SWT.CHECK | SWT.NO_FOCUS | SWT.HIDE_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		table.setLayoutData(gridData);
		final CheckboxTableViewer viewer = new CheckboxTableViewer(table);
		viewer.setContentProvider(new ArrayContentProvider());
		createTable(viewer);
		if(acquisitionSaver.getChromatogramExportConverterProcessInfo() != null) {
			viewer.setInput(acquisitionSaver.getChromatogramExportConverterProcessInfo());
		}
		Button selectAll = new Button(composite, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramExportConverterProcessingInfos.clear();
				chromatogramExportConverterProcessingInfos.addAll(acquisitionSaver.getChromatogramExportConverterProcessInfo());
			}
		});
		Button deselectAll = new Button(composite, SWT.PUSH);
		deselectAll.setText("Deselect All");
		deselectAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramExportConverterProcessingInfos.clear();
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {

				if(event.getChecked()) {
					chromatogramExportConverterProcessingInfos.add((IProcessingInfo)event.getElement());
				} else {
					chromatogramExportConverterProcessingInfos.remove(event.getElement());
				}
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		return area;
	}

	private void createTable(CheckboxTableViewer checkTable) {

		TableViewerColumn column = createTableViewerColumn("chromatogram file", 300, checkTable);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessingInfo) {
					IProcessingInfo<File> info = (IProcessingInfo)element;
					File file;
					file = info.getProcessingResult();
					return file.getName();
				}
				return null;
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, TableViewer viewer) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public List<IProcessingInfo> getChromatogramExportConverterProcessingInfos() {

		return chromatogramExportConverterProcessingInfos;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(300, 300);
	}

	@Override
	protected boolean isResizable() {

		return true;
	}
}
