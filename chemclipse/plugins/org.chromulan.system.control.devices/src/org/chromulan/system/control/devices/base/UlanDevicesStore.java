/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.devices.base;

import java.util.ArrayList;
import java.util.List;

import org.chromulan.system.control.device.IControlDevice;

public class UlanDevicesStore {

	List<IUlanControlDevice> devices;

	public UlanDevicesStore() {
		devices = new ArrayList<>();
	}

	public boolean add(IUlanControlDevice device) {

		if(null == containsDevice(device.getDeviceID())) {
			devices.add(device);
			return true;
		}
		return false;
	}

	public boolean contains(String id) {

		IControlDevice device = containsDevice(id);
		if(device == null) {
			return false;
		} else {
			return true;
		}
	}

	public IUlanControlDevice containsDevice(String id) {

		for(IControlDevice device : devices) {
			if(device instanceof IUlanControlDevice) {
				return (IUlanControlDevice)device;
			}
		}
		return null;
	}

	public IControlDevice getControlDevice(String id) {

		for(IControlDevice device : devices) {
			if(device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = (IUlanControlDevice)device;
				if(ulanDevice.getDeviceID().equals(id)) {
					return ulanDevice;
				}
			}
		}
		return null;
	}

	public List<IUlanControlDevice> getControlDevices() {

		return devices;
	}

	public boolean remove(String id) {

		IControlDevice device = containsDevice(id);
		if(device != null) {
			devices.remove(device);
			return true;
		}
		return false;
	}

	public void removeAllDevices() {

		devices.clear();
	}
}
