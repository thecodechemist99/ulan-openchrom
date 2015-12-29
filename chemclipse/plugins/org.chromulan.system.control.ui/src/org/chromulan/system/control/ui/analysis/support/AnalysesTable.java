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
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class AnalysesTable {

	private IAnalyses analyses;
	private boolean containsFilterName;
	private String filterName;
	private ObservableListContentProvider viewContentProvider;
	private TableViewer viewer;
	private ViewerFilter viewerfilterName;

	public AnalysesTable(Composite parent, int style) {
		containsFilterName = false;
		this.viewer = new TableViewer(parent, style);
		viewContentProvider = new ObservableListContentProvider();
		viewer.setContentProvider(viewContentProvider);
		createColumns(parent, viewer);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewerfilterName = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {

				IAnalysis analysis = (IAnalysis)element;
				return analysis.getName().contains(filterName);
			}
		};
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

	private void createColumns(Composite parent, final TableViewer viewer) {

		String[] titles = {"Name", "State", "Auto stop", "Duration (min)", "Auto Continue", "Directory"};
		int[] bounds = {150, 100, 100, 100, 100, 100};
		// column for the name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		IObservableMap attributeMapName = BeansObservables.observeMap(viewContentProvider.getKnownElements(), IAnalysis.class, IAnalysis.PROPERTY_NAME);
		col.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapName));
		// column for the state
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IAnalysis analysis = (IAnalysis)cell.getElement();
				if(analyses.isActualAnalysis(analysis)) {
					cell.setText("Actual");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				} else if(analysis.hasBeenRecorded()) {
					cell.setText("Record");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				} else {
					cell.setText("Ready");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				}
			}
		});
		// column for the auto stop
		col = createTableViewerColumn(titles[2], bounds[2]);
		IObservableMap attributeMapAutoStop = BeansObservables.observeMap(viewContentProvider.getKnownElements(), IAnalysis.class, IAnalysis.PROPERTY_AUTO_STOP);
		col.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapAutoStop) {

			@Override
			public void update(ViewerCell cell) {

				Object element = cell.getElement();
				boolean value = (boolean)attributeMaps[0].get(element);
				cell.setText(value == true ? "Yes" : "No"); //$NON-NLS-1$
			}
		});
		// column for the interval
		col = createTableViewerColumn(titles[3], bounds[3]);
		IObservableMap attributeMapInterval = BeansObservables.observeMap(viewContentProvider.getKnownElements(), IAnalysis.class, IAnalysis.PROPERTY_DURATION);
		col.setLabelProvider(new ObservableMapCellLabelProvider(new IObservableMap[]{attributeMapAutoStop, attributeMapInterval}) {

			@Override
			public void update(ViewerCell cell) {

				Object element = cell.getElement();
				IAnalysis analysis = (IAnalysis)element;
				if(analysis.getAutoStop()) {
					cell.setText(Long.toString(analysis.getDuration() / (long)IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				} else {
					cell.setText("");
				}
			}
		});
		// column for the auto continue
		col = createTableViewerColumn(titles[4], bounds[4]);
		IObservableMap attributeMapAutoContinue = BeansObservables.observeMap(viewContentProvider.getKnownElements(), IAnalysis.class, IAnalysis.PROPERTY_AUTO_CONTINUE);
		col.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapAutoContinue) {

			@Override
			public void update(ViewerCell cell) {

				Object element = cell.getElement();
				boolean value = (boolean)attributeMaps[0].get(element);
				cell.setText(value == true ? "Yes" : "No");
			}
		});/**/
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {

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
				viewer.getTable().setTopIndex(index);
				viewer.getTable().setSelection(index);
				viewer.getTable().setFocus();
				viewer.refresh();
				return;
			}
		}
	}

	public IAnalyses getAnalyse() {

		return analyses;
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
			}
		}
	}

	public void setAnalyses(IAnalyses analyses) {

		this.analyses = analyses;
		viewer.setInput(new WritableList(analyses.getAnalyses(), IAnalysis.class));
	}
}
