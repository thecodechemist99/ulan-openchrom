/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.devices.events;

public interface IULanConnectionEvents {

	String TOPIC_CONNECTION_ULAN = "connection/ulan/*";
	String TOPIC_CONNECTION_ULAN_CLOSE = "connection/ulan/close";
	String TOPIC_CONNECTION_ULAN_OPEN = "connection/ulan/open";
}
