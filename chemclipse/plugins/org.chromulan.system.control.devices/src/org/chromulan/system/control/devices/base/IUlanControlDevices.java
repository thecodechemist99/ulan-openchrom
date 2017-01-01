/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
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

public class IUlanControlDevices {

	public static boolean add(List<IControlDevice> controlDevices, IUlanControlDevice device) {

		if(null == getControlDevice(controlDevices, device.getDeviceID())) {
			controlDevices.add(device);
			return true;
		}
		return false;
	}

	public static boolean contains(List<? extends IControlDevice> controlDevices, String id) {

		IControlDevice device = getControlDevice(controlDevices, id);
		if(device == null) {
			return false;
		} else {
			return true;
		}
	}

	public static IUlanControlDevice getControlDevice(List<? extends IControlDevice> controlDevices, String id) {

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

	public static boolean remove(List<? extends IControlDevice> controlDevices, String id) {

		IControlDevice device = getControlDevice(controlDevices, id);
		if(device != null) {
			controlDevices.remove(device);
			return true;
		}
		return false;
	}
}
