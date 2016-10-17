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
package org.chromulan.system.control.devices.supports;

import java.lang.reflect.InvocationTargetException;

import org.chromulan.system.control.devices.base.UlanControlDevice;
import org.chromulan.system.control.devices.base.UlanDevicesStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ULanCommunicationInterface;

public class UlanScanNetRunnable implements IRunnableWithProgress {

	private UlanDevicesStore devices;

	public UlanScanNetRunnable() {
		devices = new UlanDevicesStore();
	}

	public UlanDevicesStore getDevices() {

		return devices;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		monitor.beginTask("Scan net", 100);
		devices.removeAllDevices();
		try {
			try {
				for(long i = 1; i <= 100 && !monitor.isCanceled(); i++) {
					monitor.worked(1);
					monitor.subTask("Scan address " + i);
					DeviceDescription device = ULanCommunicationInterface.getDevice(i);
					if(device != null && (device.containsTag("oi") || device.getTag("uP").equals("51x"))) {
						devices.add(new UlanControlDevice(device, true));
					}
				}
			} catch(Exception e) {
				InvocationTargetException e1 = new InvocationTargetException(e);
				throw e1;
			}
		} finally {
			monitor.done();
		}
	}
}
