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
package org.chromulan.system.control.ui.devices.support;

import org.chromulan.system.control.device.IDevicesProfile;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProfilePreferencePage extends PreferencePage {

	private IDevicesProfile profile;

	public ProfilePreferencePage(IDevicesProfile profile) {
		super("devices profile");
		this.profile = profile;
		noDefaultAndApplyButton();
	}

	@Override
	protected void contributeButtons(Composite parent) {

	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		GridLayoutFactory.swtDefaults().numColumns(1).generateLayout(composite);
		final Label lable = new Label(composite, SWT.None);
		lable.setText("Name");
		final Text textName = new Text(composite, SWT.None);
		textName.setText(profile.getName());
		textName.setEnabled(false);
		Composite compositeTable = new Composite(composite, SWT.None);
		compositeTable.setLayout(new FillLayout());
		DevicesTable table = new DevicesTable(compositeTable, SWT.HIDE_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setDevices(profile);
		return composite;
	}
}
