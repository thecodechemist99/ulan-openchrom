/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
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

import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.ui.acquisitions.AcquisitionProcess;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.databinding.beans.BeanProperties;
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

public class AcquisitionsTable {

	private AcquisitionProcess acquisitionProcess;
	private boolean containsFilterName;
	private String filterName;
	private ObservableListContentProvider viewContentProvider;
	private TableViewer viewer;
	private ViewerFilter viewerfilterName;

	public AcquisitionsTable(Composite parent, int style) {
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

				IAcquisition acquisition = (IAcquisition)element;
				return acquisition.getName().contains(filterName);
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
		IObservableMap attributeMapName = BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_NAME).observeDetail(viewContentProvider.getKnownElements());
		col.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapName));
		// column for the state
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IAcquisition acquisition = (IAcquisition)cell.getElement();
				if(acquisitionProcess.getActualAcquisition() == acquisition) {
					cell.setText("Actual");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				} else if(acquisition.isCompleted()) {
					cell.setText("Completed");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				} else {
					cell.setText("Ready");
					cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				}
			}
		});
		// column for the auto stop
		col = createTableViewerColumn(titles[2], bounds[2]);
		IObservableMap attributeMapAutoStop = BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_AUTO_STOP).observeDetail(viewContentProvider.getKnownElements());
		col.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapAutoStop) {

			@Override
			public void update(ViewerCell cell) {

				Object element = cell.getElement();
				IAcquisition acquisition = (IAcquisition)element;
				cell.setText(acquisition.getAutoStop() == true ? "Yes" : "No"); //$NON-NLS-1$
			}
		});
		// column for the interval
		col = createTableViewerColumn(titles[3], bounds[3]);
		IObservableMap attributeMapInterval = BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DURATION).observeDetail(viewContentProvider.getKnownElements());
		col.setLabelProvider(new ObservableMapCellLabelProvider(new IObservableMap[]{attributeMapAutoStop, attributeMapInterval}) {

			@Override
			public void update(ViewerCell cell) {

				Object element = cell.getElement();
				IAcquisition acquisition = (IAcquisition)element;
				if(acquisition.getAutoStop()) {
					cell.setText(Long.toString(acquisition.getDuration() / (long)IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				} else {
					cell.setText("");
				}
			}
		});
		// column for the auto continue
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

	public void displayActualAcquisition() {

		IAcquisition acquisition = acquisitionProcess.getActualAcquisition();
		if(acquisition != null) {
			int index = acquisitionProcess.getAcquisitions().indexOf(acquisition);
			if(index != -1) {
				viewer.getTable().setTopIndex(index);
				viewer.getTable().setSelection(index);
				viewer.getTable().setFocus();
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

	public void selectActualAcquisition() {

		IAcquisition acquisition = acquisitionProcess.getActualAcquisition();
		if(acquisition != null) {
			int num = acquisitionProcess.getAcquisitions().indexOf(acquisition);
			if(num != -1) {
				viewer.getTable().deselectAll();
				viewer.getTable().setTopIndex(num);
			}
		}
	}

	public void setAcquisitions(AcquisitionProcess acquisitions) {

		this.acquisitionProcess = acquisitions;
		viewer.setInput(new WritableList<>(acquisitions.getAcquisitions(), IAcquisition.class));
	}
}
