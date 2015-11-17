/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.analysis.support;

import java.lang.reflect.InvocationTargetException;

import org.chromulan.system.control.model.ControlDevice;
import org.chromulan.system.control.model.ControlDevices;
import org.chromulan.system.control.model.IControlDevices;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ULanCommunicationInterface;

public class UlanScanNetRunnable implements IRunnableWithProgress {

	private IControlDevices controlDevices;

	public UlanScanNetRunnable() {

		controlDevices = new ControlDevices();
	}

	public IControlDevices getDevices() {

		return controlDevices;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		monitor.beginTask("Scan net", 100);
		controlDevices.removeAllControlDevices();
		try {
			try {
				for(long i = 1; i <= 100 && !monitor.isCanceled(); i++) {
					monitor.worked(1);
					monitor.subTask("Scan address " + i);
					DeviceDescription device = ULanCommunicationInterface.getDevice(i);
					if(device != null && device.containsTag("oi")) {
						controlDevices.add(new ControlDevice(device));
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
