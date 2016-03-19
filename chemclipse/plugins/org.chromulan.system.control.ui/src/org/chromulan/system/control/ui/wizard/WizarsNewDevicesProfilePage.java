/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.wizard;

import org.chromulan.system.control.model.DevicesProfile;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.IDevicesProfile;
import org.chromulan.system.control.model.IDevicesProfiles;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class WizarsNewDevicesProfilePage extends WizardPage {

	private IControlDevices controlDevices;
	private IDevicesProfile profile;

	public WizarsNewDevicesProfilePage(IControlDevices controlDevices, IDevicesProfiles profiles) {
		super("New Devices Profile");
		profile = new DevicesProfile();
		this.controlDevices = controlDevices;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		final Label label = new Label(composite, SWT.None);
		label.setText("Name");
		label.setLayoutData(gridData);
		final Text nameText = new Text(composite, SWT.None);
		nameText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String ss = nameText.getText();
				if(ss.isEmpty()) {
					setErrorMessage("Name is empty.");
					setPageComplete(false);
				} else if(!ss.trim().equals(ss)) {
					setErrorMessage("Name can not start or finish whitespace");
					setPageComplete(false);
				} else {
					setErrorMessage(null);
					profile.setName(ss);
					setPageComplete(true);
				}
			}
		});
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		nameText.setLayoutData(gridData);
		Table table = new Table(composite, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		final CheckboxTableViewer checkTable = new CheckboxTableViewer(table);
		createTable(checkTable);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);
		Button buttonSelecetAll = new Button(composite, SWT.PUSH);
		buttonSelecetAll.setText("Select All");
		buttonSelecetAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(IControlDevice controlDevice : controlDevices.getControlDevices()) {
					checkTable.setChecked(controlDevice, true);
					if(!profile.contains(controlDevice.getID())) {
						profile.add(controlDevice);
					}
				}
			}
		});
		Button buttonDeselectAll = new Button(composite, SWT.PUSH);
		buttonDeselectAll.setText("Deselect All");
		buttonDeselectAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(IControlDevice controlDevice : controlDevices.getControlDevices()) {
					checkTable.setChecked(controlDevice, false);
					if(profile.contains(controlDevice.getID())) {
						profile.remove(controlDevice.getID());
					}
				}
			}
		});
		WizardNewDevicesProfile wizardNewDevicesProfile = (WizardNewDevicesProfile)getWizard();
		wizardNewDevicesProfile.setDevicesProfile(profile);
		setErrorMessage("Name is empty.");
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}

	private void createTable(CheckboxTableViewer checkTable) {

		TableViewerColumn column = createTableViewerColumn("Port", 80, 1, checkTable);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IControlDevice) {
					IControlDevice device = (IControlDevice)element;
					return Long.toString(device.getDeviceDescription().getAdr());
				}
				return "";
			}
		});
		column = createTableViewerColumn("Description", 300, 2, checkTable);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IControlDevice) {
					IControlDevice device = (IControlDevice)element;
					return device.getDeviceDescription().getDescription();
				}
				return "";
			}
		});
		checkTable.setContentProvider(new ArrayContentProvider());
		checkTable.setInput(controlDevices.getControlDevices());
		checkTable.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {

				IControlDevice device = ((IControlDevice)event.getElement());
				if(profile.contains(device.getID())) {
					profile.remove(device.getID());
				} else {
					profile.add(device);
				}
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber, TableViewer viewer) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
}
