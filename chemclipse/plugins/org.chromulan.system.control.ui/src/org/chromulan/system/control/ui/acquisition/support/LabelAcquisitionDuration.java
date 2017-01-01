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
package org.chromulan.system.control.ui.acquisition.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelAcquisitionDuration {

	private Label label;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	private long time;

	public LabelAcquisitionDuration(Composite composite, int style) {
		label = new Label(composite, style);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date(0);
		String dateFormatted = simpleDateFormat.format(date);
		label.setText(dateFormatted);
	}

	public Label getLabel() {

		return label;
	}

	public long getTime() {

		return time;
	}

	public void setTime(long time) {

		Date date = new Date(time);
		this.time = time;
		String dateFormatted = simpleDateFormat.format(date);
		label.setText(dateFormatted);
	}
}
