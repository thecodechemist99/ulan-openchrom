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
package org.chromulan.system.control.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

public class ControlDevices implements IControlDevices {

	private List<IControlDevice> controlDevices;

	public ControlDevices() {
		controlDevices = new LinkedList<IControlDevice>();
	}

	@Override
	public boolean add(IControlDevice device) {

		if(null == containsDevice(device.getID())) {
			controlDevices.add(device);
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(String id) {

		IControlDevice device = containsDevice(id);
		if(device == null) {
			return false;
		} else {
			return true;
		}
	}

	private IControlDevice containsDevice(String id) {

		for(IControlDevice iControlDevice : controlDevices) {
			if(iControlDevice.getID().equals(id)) {
				return iControlDevice;
			}
		}
		return null;
	}

	@Override
	public IControlDevice getControlDevice(String id) {

		for(IControlDevice device : controlDevices) {
			if(device.getID().equals(id)) {
				return device;
			}
		}
		return null;
	}

	@Override
	public List<IControlDevice> getControlDevices() {

		return controlDevices;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		controlDevices = (List<IControlDevice>)in.readObject();
	}

	@Override
	public boolean remove(String id) {

		IControlDevice device = containsDevice(id);
		if(device != null) {
			controlDevices.remove(device);
			return true;
		}
		return false;
	}

	@Override
	public void removeAllControlDevices() {

		controlDevices.clear();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(controlDevices);
	}
}
