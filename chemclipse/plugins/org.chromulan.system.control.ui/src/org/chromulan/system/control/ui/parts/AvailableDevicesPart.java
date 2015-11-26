/*******************************************************************************
 * Copyright (c) 2015 Jan Holy, Dr. Philip Wenig.
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
package org.chromulan.system.control.ui.parts;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.ControlDevice;
import org.chromulan.system.control.model.ControlDevices;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.ui.analysis.support.UlanScanNetRunnable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanDrv;
import net.sourceforge.ulan.base.ULanMsg;

public class AvailableDevicesPart {

	public final static String ID = "org.chromulan.system.control.ui.part.availableDevices";
	static {
		if(!ULanCommunicationInterface.isHandleInitialized()) {
			try {
				ULanCommunicationInterface.setHandle(new ULanDrv());
			} catch(Exception e) {
				// TODO: logger.warn(e);
			}
		}
	}
	@Inject
	private MApplication application;
	private Button buttonRefreshDevices;
	private ULanCommunicationInterface communication;
	private ULanConnection connection;
	private IControlDevices devices;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private IFilt filtCloseConnection;
	private Label labelConnection;
	@Inject
	private EModelService modelService;
	@Inject
	private MPart part;
	@Inject
	private EPartService partService;
	private Table table;

	public AvailableDevicesPart() {

		communication = new ULanCommunicationInterface();
		connection = new ULanConnection();
		devices = new ControlDevices();
	}

	private void closeConnection() {

		devices.removeAllControlDevices();
		rewriteTable();
		if(ULanDrv.isLibraryLoaded()) {
			try {
				if(ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.close();
				}
			} catch(IOException e) {
				// logger.warn(e);
			} finally {
				labelConnection.setText("Connection is closed");
				labelConnection.setBackground(display.getSystemColor(SWT.COLOR_RED));
				labelConnection.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				eventBroker.post(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_CLOSE, connection);
			}
		}
	}

	@Inject
	@Optional
	public void conectReqiredDevice(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_REQIRED) ControlDevices devices) {

		for(IControlDevice device : devices.getControlDevices()) {
			MPart part = partService.findPart(device.getID());
			if(part == null) {
				try {
					DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
					if(description != null) {
						IControlDevice controlDevice = new ControlDevice(description);
						if(!this.devices.contains(controlDevice.getID())) {
							this.devices.add(controlDevice);
							rewriteTable();
						}
						eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
					}
				} catch(Exception e) {
					// TODO: logger.warn(e);
				}
			}
		}
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(1, false);
		composite.setLayout(gridLayout);
		labelConnection = new Label(composite, SWT.LEFT | SWT.BORDER);
		labelConnection.setText("Connection is closed");
		labelConnection.setBackground(display.getSystemColor(SWT.COLOR_RED));
		labelConnection.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(GridData.END, GridData.FILL, true, false);
		labelConnection.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		final TableColumn columnAdr = new TableColumn(table, SWT.NULL);
		columnAdr.setText("Adr");
		columnAdr.setWidth(40);
		final TableColumn columnName = new TableColumn(table, SWT.NULL);
		columnName.setText("Name of devices");
		columnName.setWidth(150);
		final TableColumn columnDescription = new TableColumn(table, SWT.None);
		columnDescription.setText("Description");
		columnDescription.setWidth(400);
		buttonRefreshDevices = new Button(composite, SWT.PUSH);
		buttonRefreshDevices.setText("Scan Net");
		buttonRefreshDevices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						openConection();
						loadDevice();
					}
				});
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonRefreshDevices.setLayoutData(gridData);
		filtCloseConnection = communication.addFilt(0, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

			}

			@Override
			public void failed(Exception arg0, Void arg1) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						closeConnection();
					}
				});
			}
		});
	}

	private void loadDevice() {

		if(ULanCommunicationInterface.isOpen()) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			UlanScanNetRunnable runnable = new UlanScanNetRunnable();
			try {
				dialog.run(true, false, runnable);
				devices = runnable.getDevices();
				rewriteTable();
				part.getContext().set(IControlDevices.class, devices);
				for(IControlDevice controlDevice : devices.getControlDevices()) {
					if(partService.findPart(controlDevice.getID()) == null) {
						eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
					}
				}
				eventBroker.send(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE, devices);
				MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", application);
				if(!stack.getChildren().isEmpty()) {
					stack.setVisible(true);
				}
			} catch(InvocationTargetException e) {
				// /logger.warn(e);
			} catch(InterruptedException e) {
				// logger.warn(e);
			}
		}
	}

	private void openConection() {

		if(ULanDrv.isLibraryLoaded() && ULanCommunicationInterface.isHandleInitialized()) {
			try {
				if(!ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.open();
					if(!filtCloseConnection.isFiltActive()) {
						filtCloseConnection.activateFilt();
					}
					labelConnection.setText("Connection is opened");
					labelConnection.redraw();
					labelConnection.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
					labelConnection.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					eventBroker.send(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN, connection);
					labelConnection.getParent().layout();
				}
			} catch(IOException e) {
				// TODO:Exception
			}
		}
	}

	private void rewriteTable() {

		table.removeAll();
		for(IControlDevice device : devices.getControlDevices()) {
			TableItem item = new TableItem(table, SWT.None);
			item.setText(0, Long.toString(device.getDeviceDescription().getAdr()));
			item.setText(1, device.getDeviceDescription().getModulType());
			item.setText(2, device.getDeviceDescription().getDescription());
		}
	}
}
