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
package org.chromulan.system.control.devices.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.chromulan.system.control.data.DataSupplier;
import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevices;
import org.chromulan.system.control.devices.base.UlanDevicesStore;
import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IControlDeviceEvents;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.chromulan.system.control.devices.supports.UlanScanNetRunnable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;

public class ScanNet {

	public static final String HANDLER_ID = "org.chromulan.system.control.devices.command.scannet";
	@Inject
	private ULanConnection connection;
	@Inject
	private DataSupplier dataSupplier;
	@Inject
	private IEventBroker eventBroker;

	@Execute
	private void execute() {

		try {
			connection.open();
			eventBroker.post(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN, connection);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			UlanScanNetRunnable runnable = new UlanScanNetRunnable();
			try {
				dialog.run(true, false, runnable);
				UlanDevicesStore devices = runnable.getDevices();
				setDevices(devices);
			} catch(InvocationTargetException e) {
				// /logger.warn(e);
			} catch(InterruptedException e) {
				// logger.warn(e);
			}
		} catch(IOException e) {
			// TODO:Exception
		}
	}

	private void setDevices(UlanDevicesStore devices) {

		for(IUlanControlDevice device : devices.getControlDevices()) {
			if(device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = device;
				if(IUlanControlDevices.contains(this.dataSupplier.getControlDevices(), ulanDevice.getID())) {
					IUlanControlDevices.getControlDevice(this.dataSupplier.getControlDevices(), ulanDevice.getID()).setConnected(true);
				} else {
					IUlanControlDevices.add(this.dataSupplier.getControlDevices(), ulanDevice);
				}
				eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, IUlanControlDevices.getControlDevice(this.dataSupplier.getControlDevices(), ulanDevice.getID()));
			}
		}
		for(IControlDevice device : this.dataSupplier.getControlDevices().getControlDevices()) {
			if(device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = (IUlanControlDevice)device;
				if(!devices.contains(ulanDevice.getID())) {
					ulanDevice.setConnected(false);
					eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, ulanDevice);
				}
			}
		}
		dataSupplier.updateControlDevices();
	}
}
