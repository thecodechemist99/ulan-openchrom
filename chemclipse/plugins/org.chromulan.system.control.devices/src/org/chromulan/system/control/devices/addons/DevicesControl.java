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

import org.chromulan.system.control.device.ControlDevices;
import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevices;
import org.chromulan.system.control.devices.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.manager.devices.DataSupplier;
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
	public void conectReqiredDevice(
			@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL) ControlDevices devices) {

		if (devices == null) {
			return;
		}
		for (IControlDevice deviceCon : devices.getControlDevices()) {
			if (deviceCon instanceof IUlanControlDevice) {
				IUlanControlDevice device = (IUlanControlDevice) deviceCon;
				IUlanControlDevice controlDevice = IUlanControlDevices
						.getControlDevice(this.dataSupplier.getControlDevices(), device.getDeviceID());
				if (controlDevice == null) {
					try {
						DeviceDescription description = ULanCommunicationInterface
								.getDevice(device.getDeviceDescription().getAdr());
						if (description != null) {
							IUlanControlDevices.add(this.dataSupplier.getControlDevices(), device);
							eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, IUlanControlDevices
									.getControlDevice(this.dataSupplier.getControlDevices(), device.getDeviceID()));
						}
					} catch (Exception e) {
					}
				} else {
					try {
						DeviceDescription description = ULanCommunicationInterface
								.getDevice(device.getDeviceDescription().getAdr());
						if (description != null) {
							controlDevice.setConnected(true);
							eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
						} else {
							controlDevice.setConnected(false);
							eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, controlDevice);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		}
		dataSupplier.updateControlDevices();
	}
}
