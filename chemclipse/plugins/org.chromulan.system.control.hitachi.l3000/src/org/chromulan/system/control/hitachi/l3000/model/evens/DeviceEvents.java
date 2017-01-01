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
package org.chromulan.system.control.hitachi.l3000.model.evens;

public interface DeviceEvents {

	final String TOPIC_HITACHI_L3000_CONNECTION_CLOSE = "hitachiL3000/connection/close";
	final String TOPIC_HITACHI_L3000_CONNECTION_OPEN = "hitachiL3000/connection/open";
	final String TOPIC_HITACHI_L3000_CONNECTION_SET_PARAMETERS = "hitachiL3000/connection/setParametrs";
}
