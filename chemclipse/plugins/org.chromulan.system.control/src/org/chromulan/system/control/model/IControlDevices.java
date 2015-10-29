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

import java.util.List;

public interface IControlDevices {

	List<IControlDevice> getControlDevices();
	
	void add(IControlDevice controlDevice);
	
	void remove(IControlDevice controlDevice);
	
	boolean contains(IControlDevice controlDevice);
	
	void removeAllControlDevices();
	
	void add(IControlDevices devices);
	
	
}
