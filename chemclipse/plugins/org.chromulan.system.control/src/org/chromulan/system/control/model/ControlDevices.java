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
package org.chromulan.system.control.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class ControlDevices implements IControlDevices {
	
	List<IControlDevice> controlDevices;
	
	
	public ControlDevices() {
		controlDevices = new LinkedList<IControlDevice>();
	}


	@Override
	public List<IControlDevice> getControlDevices() {

		return null;
	}


	@Override
	public void add(IControlDevice controlDevice) {

	}


	@Override
	public void remove(IControlDevice controlDevice) {

	}


	@Override
	public boolean contains(IControlDevice controlDevice) {

		return false;
	}


	@Override
	public void removeAllControlDevices() {

	}


	@Override
	public void add(IControlDevices devices) {

	}
	

	
}
