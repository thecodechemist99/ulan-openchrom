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
package org.chromulan.system.control.events;

public interface IAnalysisEvents {

	String TOPIC_ANALYSIS_CHROMULAN_SET = "analysis/chromulan/set";
	String TOPIC_ANALYSIS_CHROMULAN_START = "analysis/chromulan/start";
	String TOPIC_ANALYSIS_CHROMULAN_START_RECORDING = "analysis/chromulan/startrecording";
	String TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING = "analysis/chromulan/stoprecording";
	String TOPIC_ANALYSIS_CHROMULAN_END = "analysis/chromulan/end";
}
