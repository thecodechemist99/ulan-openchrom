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
package org.chromulan.system.control.devices.supports;

import java.util.List;

import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DevicesTable {

	private class NameEditor extends EditingSupport {

		private CellEditor editor;

		public NameEditor(TableViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {

			return editableName;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {

			return editor;
		}

		@Override
		protected Object getValue(Object element) {

			return ((IUlanControlDevice)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {

			((IUlanControlDevice)element).setName(String.valueOf(value));
		}
	}

	private List<IUlanControlDevice> devices;
	private boolean editableName;
	private ObservableListContentProvider viewContentProvider;
	private TableViewer viewer;

	public DevicesTable(Composite parent, int style, boolean setFilter) {
		this.viewer = new TableViewer(parent, style);
		viewContentProvider = new ObservableListContentProvider();
		viewer.setContentProvider(viewContentProvider);
		if(setFilter) {
			viewer.addFilter(new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {

					if(!(element instanceof IUlanControlDevice)) {
						return false;
					}
					IUlanControlDevice device = (IUlanControlDevice)element;
					return device.isConnected();
				}
			});
		}
		createColumns(parent, viewer);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		editableName = false;
	}

	private void createColumns(Composite parent, TableViewer viewer) {

		String[] titles = {"", "Adr", "Name", "Description", "Type"};
		int[] bounds = {20, 50, 150, 300, 100};
		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0]);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IUlanControlDevice controlDevice = (IUlanControlDevice)element;
				if(controlDevice.isConnected()) {
					return "Y";
				} else {
					return "N";
				}
			}
		});
		column = createTableViewerColumn(titles[1], bounds[1]);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IUlanControlDevice controlDevice = (IUlanControlDevice)element;
				return Long.toString(controlDevice.getDeviceDescription().getAdr());
			}
		});
		column = createTableViewerColumn(titles[2], bounds[2]);
		IObservableMap attributeMapName = BeanProperties.value(IUlanControlDevice.class, IUlanControlDevice.PROPERTY_NAME).observeDetail(viewContentProvider.getKnownElements());
		column.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapName));
		column.setEditingSupport(new NameEditor(viewer));
		column = createTableViewerColumn(titles[3], bounds[3]);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IUlanControlDevice controlDevice = (IUlanControlDevice)element;
				return controlDevice.getDeviceDescription().getDescription();
			}
		});
		column = createTableViewerColumn(titles[4], bounds[4]);
		IObservableMap attributeMapType = BeanProperties.value(IUlanControlDevice.class, IUlanControlDevice.PROPERTY_DEVICE_TYPE).observeDetail(viewContentProvider.getKnownElements());
		column.setLabelProvider(new ObservableMapCellLabelProvider(attributeMapType));
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

	public List<IUlanControlDevice> getDevices() {

		return devices;
	}

	public TableViewer getViewer() {

		return viewer;
	}

	public void setDevices(List<IUlanControlDevice> devices) {

		viewer.setInput(new WritableList<>(devices, IUlanControlDevice.class));
		this.devices = devices;
	}

	public void setEditableName(boolean b) {

		editableName = b;
	}
}
