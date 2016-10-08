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

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IControlDevices;

public interface IUlanControlDevices {

	static boolean add(IControlDevices controlDevices, IUlanControlDevice device) {

		if (null == containsDevice(controlDevices, device.getDeviceID())) {
			controlDevices.getControlDevices().add(device);
			return true;
		}
		return false;
	}

	static boolean contains(IControlDevices controlDevices, String id) {

		IControlDevice device = containsDevice(controlDevices, id);
		if (device == null) {
			return false;
		} else {
			return true;
		}
	}

	static IUlanControlDevice containsDevice(IControlDevices controlDevices, String id) {

		for (IControlDevice device : controlDevices.getControlDevices()) {
			if (device instanceof IUlanControlDevice) {
				return (IUlanControlDevice) device;
			}
		}
		return null;
	}

	static IUlanControlDevice getControlDevice(IControlDevices controlDevices, String id) {

		for (IControlDevice device : controlDevices.getControlDevices()) {
			if (device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = (IUlanControlDevice) device;
				if (ulanDevice.getDeviceID().equals(id)) {
					return ulanDevice;
				}
			}
		}
		return null;
	}

	static boolean remove(IControlDevices controlDevices, String id) {

		IControlDevice device = containsDevice(controlDevices, id);
		if (device != null) {
			controlDevices.getControlDevices().remove(device);
			return true;
		}
		return false;
	}
}
