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
package org.chromulan.system.control.devices.addons;

import javax.inject.Inject;

import org.chromulan.system.control.data.DataSupplier;
import org.chromulan.system.control.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.model.ControlDevices;
import org.chromulan.system.control.model.IControlDevice;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ULanCommunicationInterface;

public class DevicesControl {

	@Inject
	private DataSupplier dataSupplier;
	@Inject
	private IEventBroker eventBroker;

	public DevicesControl() {
	}

	@Inject
	@Optional
	public void conectReqiredDevice(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_CONTROL) ControlDevices devices) {

		if(devices == null) {
			return;
		}
		for(IControlDevice device : devices.getControlDevices()) {
			IControlDevice controlDevice = this.dataSupplier.getControlDevices().getControlDevice(device.getID());
			if(controlDevice == null) {
				try {
					DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
					if(description != null) {
						this.dataSupplier.getControlDevices().add(device);
						eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, this.dataSupplier.getControlDevices().getControlDevice(device.getID()));
					}
				} catch(Exception e) {
				}
			} else {
				try {
					DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
					if(description != null) {
						controlDevice.setConnected(true);
						eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
					} else {
						controlDevice.setConnected(false);
						eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, controlDevice);
					}
				} catch(Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		dataSupplier.updateControlDevices();
	}
}
