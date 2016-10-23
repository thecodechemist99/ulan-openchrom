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

import java.util.List;

import org.chromulan.system.control.device.IControlDevice;

public interface IUlanControlDevices {

	static boolean add(List<IControlDevice> controlDevices, IUlanControlDevice device) {

		if(null == containsDevice(controlDevices, device.getDeviceID())) {
			controlDevices.add(device);
			return true;
		}
		return false;
	}

	static boolean contains(List<IControlDevice> controlDevices, String id) {

		IControlDevice device = containsDevice(controlDevices, id);
		if(device == null) {
			return false;
		} else {
			return true;
		}
	}

	static IUlanControlDevice containsDevice(List<IControlDevice> controlDevices, String id) {

		for(IControlDevice device : controlDevices) {
			if(device instanceof IUlanControlDevice) {
				return (IUlanControlDevice)device;
			}
		}
		return null;
	}

	static IUlanControlDevice getControlDevice(List<IControlDevice> controlDevices, String id) {

		for(IControlDevice device : controlDevices) {
			if(device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = (IUlanControlDevice)device;
				if(ulanDevice.getDeviceID().equals(id)) {
					return ulanDevice;
				}
			}
		}
		return null;
	}

	static boolean remove(List<IControlDevice> controlDevices, String id) {

		IControlDevice device = containsDevice(controlDevices, id);
		if(device != null) {
			controlDevices.remove(device);
			return true;
		}
		return false;
	}
}
