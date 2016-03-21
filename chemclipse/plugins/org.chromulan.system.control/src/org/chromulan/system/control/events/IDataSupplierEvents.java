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
package org.chromulan.system.control.events;

public interface IDataSupplierEvents {

	String TOPIC_DATA_UPDATE = "data/update/*";
	String TOPIC_DATA_UPDATE_DEVICES = "data/update/devices";
	String TOPIC_DATA_UPDATE_PROFILES = "data/update/profiles";
}
