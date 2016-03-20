package org.chromulan.system.control.devices.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.chromulan.system.control.data.DataSupplier;
import org.chromulan.system.control.devices.supports.UlanScanNetRunnable;
import org.chromulan.system.control.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.ULanConnection;
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
				IControlDevices devices = runnable.getDevices();
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

	private void setDevices(IControlDevices devices) {

		for(IControlDevice device : devices.getControlDevices()) {
			if(this.dataSupplier.getControlDevices().contains(device.getID())) {
				this.dataSupplier.getControlDevices().getControlDevice(device.getID()).setConnected(true);
			} else {
				this.dataSupplier.getControlDevices().add(device);
			}
			eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, this.dataSupplier.getControlDevices().getControlDevice(device.getID()));
		}
		for(IControlDevice device : this.dataSupplier.getControlDevices().getControlDevices()) {
			if(!devices.contains(device.getID())) {
				device.setConnected(false);
				eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, device);
			}
		}
		eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE, dataSupplier.getControlDevices());
	}
}
