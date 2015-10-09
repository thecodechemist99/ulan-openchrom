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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ULanCommunicationInterface;


public class UlanScanNetRunnable implements IRunnableWithProgress{

	private  List<DeviceDescription> devices;
	
	public UlanScanNetRunnable() {
		this.devices = new LinkedList<DeviceDescription>();
	}
	
	public List<DeviceDescription> getDevices() {
		return devices;		
	}
	
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Scan net", 100);
		devices.clear();
		try {		
		if(ULanCommunicationInterface.isOpen())
			{
			
			
			try {	
				for(long i = 1; i < 100 && !monitor.isCanceled(); i++) {
					monitor.worked(1);
					monitor.subTask("Scan address " + i);
					
						DeviceDescription device = ULanCommunicationInterface.getDevice(i);
						if(device != null && device.containsTag("oi"))
						{
							devices.add(device);
						}					
					
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			}
		} finally{
			monitor.done();
		}
	}
}
