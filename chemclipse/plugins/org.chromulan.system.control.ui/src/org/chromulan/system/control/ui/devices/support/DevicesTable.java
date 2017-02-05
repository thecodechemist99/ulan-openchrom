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
package org.chromulan.system.control.ui.devices.support;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IControlDevices;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DevicesTable {

	private IControlDevices devices;
	private ObservableListContentProvider viewContentProvider;
	private TableViewer viewer;

	public DevicesTable(Composite parent, int style) {
		this.viewer = new TableViewer(parent, style);
		viewContentProvider = new ObservableListContentProvider();
		viewer.setContentProvider(viewContentProvider);
		createColumns(parent, viewer);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createColumns(Composite parent, TableViewer viewer) {

		String[] titles = {"Name", "Device Description"};
		int[] bounds = {70, 400};
		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0]);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IControlDevice controlDevice = (IControlDevice)element;
				return controlDevice.getName();
			}
		});
		column = createTableViewerColumn(titles[1], bounds[1]);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IControlDevice controlDevice = (IControlDevice)element;
				return controlDevice.getDescription();
			}
		});
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

	public IControlDevices getDevices() {

		return devices;
	}

	public TableViewer getViewer() {

		return viewer;
	}

	public void setDevices(IControlDevices devices) {

		viewer.setInput(new WritableList<>(devices.getControlDevices(), IControlDevice.class));
		this.devices = devices;
	}
}
