/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy, Dr. Philip Wenig.
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
import org.chromulan.system.control.ui.acquisition.support.UlanScanNetRunnable;
import org.chromulan.system.control.ui.devices.support.DevicesTable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
	private DevicesTable deviceTable;
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

	public AvailableDevicesPart() {
		communication = new ULanCommunicationInterface();
		connection = new ULanConnection();
		devices = new ControlDevices();
	}

	private void closeConnection() {

		for(IControlDevice device : devices.getControlDevices()) {
			device.setConnected(false);
			eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, device);
		}
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
				eventBroker.post(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_CLOSE, connection);
			}
		}
	}

	@Inject
	@Optional
	public void conectReqiredDevice(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_CONTROL) ControlDevices devices) {

		for(IControlDevice device : devices.getControlDevices()) {
			if(!this.devices.contains(device.getID())) {
				try {
					DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
					if(description != null) {
						IControlDevice controlDevice = new ControlDevice(description, true);
						connectDevice(controlDevice);
					}
				} catch(Exception e) {
				}
			} else {
				try {
					DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
					IControlDevice controlDevice = this.devices.getControlDevice(device.getID());
					if(controlDevice != null) {
						if(description != null) {
							controlDevice.setConnected(true);
							eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
						} else {
							controlDevice.setConnected(false);
							eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, controlDevice);
						}
					}
				} catch(Exception e) {
				}
			}
		}
		rewriteTable();
		eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE, this.devices);
	}

	private void connectDevice(IControlDevice device) {

		if(this.devices.contains(device.getID())) {
			this.devices.getControlDevice(device.getID()).setConnected(true);
		} else {
			this.devices.add(device);
		}
		eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, device);
		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", application);
		if(stack != null && !stack.getChildren().isEmpty()) {
			stack.setVisible(true);
		}
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		part.getContext().set(IControlDevices.class, devices);
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
		Composite tableComposit = new Composite(composite, SWT.None);
		tableComposit.setLayoutData(gridData);
		tableComposit.setLayout(new FillLayout());
		deviceTable = new DevicesTable(tableComposit, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, true);
		deviceTable.setDevices(devices);
		deviceTable.setEditableName(true);
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
				IControlDevices devices = runnable.getDevices();
				setDevices(devices);
				rewriteTable();
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
					eventBroker.send(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN, connection);
					labelConnection.getParent().layout();
				}
			} catch(IOException e) {
				// TODO:Exception
			}
		}
	}

	private void rewriteTable() {

		deviceTable.getViewer().refresh();
	}

	private void setDevices(IControlDevices devices) {

		for(IControlDevice device : devices.getControlDevices()) {
			connectDevice(device);
		}
		for(IControlDevice device : this.devices.getControlDevices()) {
			if(!devices.contains(device.getID())) {
				device.setConnected(false);
				eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, device);
			}
		}
		eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE, this.devices);
	}
}
