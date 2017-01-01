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
package org.chromulan.system.control.events;

public interface IAcquisitionEvents {

	String TOPIC_ACQUISITION_CHROMULAN_END = "acquisition/chromulan/end";
	String TOPIC_ACQUISITION_CHROMULAN_SET = "acquisition/chromulan/set";
	String TOPIC_ACQUISITION_CHROMULAN_START_RECORDING = "acquisition/chromulan/startrecording";
	String TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING = "acquisition/chromulan/stoprecording";
}
