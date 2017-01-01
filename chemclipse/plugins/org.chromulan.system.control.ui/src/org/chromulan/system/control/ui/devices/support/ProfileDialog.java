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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ProfileDialog extends Dialog {

	private IDevicesProfile profile;

	public ProfileDialog(Shell parentShell, IDevicesProfile profile) {
		super(parentShell);
		this.profile = profile;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		DevicesTable devicesTable = new DevicesTable(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		devicesTable.setDevices(profile);
		return super.createDialogArea(parent);
	}
}
