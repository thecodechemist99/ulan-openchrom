/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.analysis.support;

import org.chromulan.system.control.model.IAnalyses;
import org.chromulan.system.control.model.IAnalysis;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class AnalysesTable {

	private IAnalyses analyses;
	private boolean containsFilterName;
	private String filterName;
	private Table table;
	private TableViewer viewer;
	private ViewerFilter viewerfilterName;

	public AnalysesTable(Composite parent, int style, IAnalyses analyses) {

		this.analyses = analyses;
		containsFilterName = false;
		createPartControl(parent, style);
	}

	public void addFilterName(String text) {

		if(text != null && !containsFilterName) {
			filterName = text;
			viewer.addFilter(viewerfilterName);
			viewer.refresh();
			containsFilterName = true;
		}
	}

	public boolean containsFilterName() {

		return containsFilterName;
	}

	private void createColumns(Composite parent, TableViewer viewer) {

		String[] titles = {IAnalysis.PROPERTY_NAME, "State", IAnalysis.PROPERTY_AUTO_STOP, IAnalysis.PROPERTY_INTERVAL, IAnalysis.PROPERTY_AUTO_CONTINUE, IAnalysis.PROPERTY_DIRECTORY};
		int[] bounds = {100, 100, 100, 100, 100, 100};
		// column for the name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IAnalysis analysis = (IAnalysis)element;
				return analysis.getName();
			}
		});
		// column for the state
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IAnalysis analysis = (IAnalysis)element;
				if(analyses.isActualAnalysis(analysis)) {
					return "Actual";
				} else if(analysis.hasBeenRecorded()) {
					return "Recored";
				} else {
					return "Ready";
				}
			}
		});
		// column for the auto stop
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IAnalysis analysis = (IAnalysis)element;
				return Boolean.toString(analysis.getAutoStop());
			}
		});
		// column for the interval
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IAnalysis analysis = (IAnalysis)element;
				if(analysis.getAutoStop()) {
					return Long.toString(analysis.getInterval() / (long)IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				} else {
					return "";
				}
			}
		});
		// column for the auto continue
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IAnalysis analysis = (IAnalysis)element;
				return Boolean.toString(analysis.getAutoContinue());
			}
		});
	}

	private void createPartControl(Composite parent, int style) {

		this.viewer = new TableViewer(parent, style);
		createColumns(parent, viewer);
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(analyses.getAnalyses());
		viewerfilterName = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {

				IAnalysis analysis = (IAnalysis)element;
				return analysis.getName().contains(filterName);
			}
		};
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public void displayActualAnalysis() {

		IAnalysis analysis = analyses.getActualAnalysis();
		if(analyses != null) {
			int index = analyses.gettIndex(analysis);
			if(index != -1) {
				table.setTopIndex(index);
				table.setSelection(index);
				table.setFocus();
				viewer.refresh();
				return;
			}
		}
	}

	public TableViewer getViewer() {

		return viewer;
	}

	public void removeFilterName() {

		viewer.removeFilter(viewerfilterName);
		viewer.refresh();
		containsFilterName = false;
	}

	public void selectActualAnalysis() {

		IAnalysis analysis = analyses.getActualAnalysis();
		if(analysis != null) {
			int num = analyses.gettIndex(analysis);
			if(num != -1) {
				viewer.getTable().deselectAll();
				viewer.getTable().setTopIndex(num);
				viewer.getTable().select(num);
			}
		}
	}
}
