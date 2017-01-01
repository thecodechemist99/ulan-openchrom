/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy, Dr. Philip Wenig.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.devices.parts;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.devices.base.UlanDevicesManager;
import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.chromulan.system.control.devices.handlers.ScanNet;
import org.chromulan.system.control.devices.supports.DevicesTable;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.manager.events.IDataSupplierEvents;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class AvailableDevicesPart {

	public final static String ID = "org.chromulan.system.control.ui.part.availableDevices";
	private Button buttonRefreshDevices;
	@Inject
	private ECommandService commandService;
	@Inject
	private ULanConnection connection;
	private DevicesTable deviceTable;
	@Inject
	private Display display;
	@Inject
	private EHandlerService handlerService;
	private Label labelConnection;
	@Inject
	private UlanDevicesManager manager;

	public AvailableDevicesPart() {
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(1, false);
		composite.setLayout(gridLayout);
		labelConnection = new Label(composite, SWT.LEFT | SWT.BORDER);
		GridData gridData = new GridData(GridData.END, GridData.FILL, true, false);
		labelConnection.setLayoutData(gridData);
		setConnectionLabel();
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		Composite tableComposit = new Composite(composite, SWT.None);
		tableComposit.setLayoutData(gridData);
		tableComposit.setLayout(new FillLayout());
		deviceTable = new DevicesTable(tableComposit, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, false);
		deviceTable.setDevices(manager.getDevices());
		deviceTable.setEditableName(true);
		buttonRefreshDevices = new Button(composite, SWT.PUSH);
		buttonRefreshDevices.setText("Scan Net");
		buttonRefreshDevices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						scanNet();
					}
				});
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonRefreshDevices.setLayoutData(gridData);
		rewriteTable();
	}

	private void rewriteTable() {

		deviceTable.getViewer().refresh();
	}

	private void scanNet() {

		ParameterizedCommand com = commandService.createCommand(ScanNet.HANDLER_ID, new HashMap<String, Object>());
		if(handlerService.canExecute(com)) {
			handlerService.executeHandler(com);
		}
	}

	private void setConnectionLabel() {

		if(connection.isOpen()) {
			labelConnection.setText("Connection is opened");
			labelConnection.redraw();
			labelConnection.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
			labelConnection.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			labelConnection.getParent().layout();
		} else {
			labelConnection.setText("Connection is closed");
			labelConnection.setBackground(display.getSystemColor(SWT.COLOR_RED));
			labelConnection.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
			labelConnection.getParent().layout();
		}
	}

	@Inject
	@Optional
	public void updateDevices(@UIEventTopic(value = IDataSupplierEvents.TOPIC_DATA_UPDATE_DEVICES) DataSupplier dataSupplier) {

		rewriteTable();
	}

	@Inject
	@Optional
	public void updateScanState(@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN) ULanConnection connection) {

		setConnectionLabel();
		rewriteTable();
	}
}
