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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelAnalysisInterval {

	private Label label;
	private long time;

	public LabelAnalysisInterval(Composite composite, int style) {

		label = new Label(composite, style);
		label.setText("00:00:00");
	}

	private StringBuilder addzerro(long i, StringBuilder ss) {

		if(i == 0) {
			ss.append("00");
		} else if(i < 10) {
			ss.append("0");
			ss.append(Long.toString(i));
		} else {
			ss.append(Long.toString(i));
		}
		return ss;
	}

	public Label getLabel() {

		return label;
	}

	public long getTime() {

		return time;
	}

	public void setTime(long time) {

		long secondes = (time / 1000) % 60;
		long minutes = (time / 60000) % 60;
		long hours = (time / 3600000) % 60;
		StringBuilder ss = new StringBuilder();
		ss = addzerro(hours, ss);
		ss.append(":");
		ss = addzerro(minutes, ss);
		ss.append(":");
		ss = addzerro(secondes, ss);
		label.setText(ss.toString());
	}
}
