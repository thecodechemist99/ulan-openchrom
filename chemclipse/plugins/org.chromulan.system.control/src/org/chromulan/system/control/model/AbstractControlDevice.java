/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import net.sourceforge.ulan.base.DeviceDescription;

public abstract class AbstractControlDevice implements IControlDevice {

	DeviceDescription description;

	public AbstractControlDevice(DeviceDescription description) {

		super();
		this.description = description;
	}
	
	@Override
	public void setDeviceDescription(DeviceDescription description) {
		this.description = description;
	}
	
	@Override
	public DeviceDescription getDeviceDescription() {
	
		return description;
	}
	
	@Override
	public String getID() {
		
		return description.getModulType() + "." + Long.toString(description.getAdr());
	}
	
	@Override
	public int compareTo(IControlDevice o) {
	
		
		return 0;
	}
}
