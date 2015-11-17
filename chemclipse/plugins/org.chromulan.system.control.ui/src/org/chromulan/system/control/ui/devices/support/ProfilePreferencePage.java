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
package org.chromulan.system.control.ui.devices.support;

import org.chromulan.system.control.model.IDevicesProfile;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
		final Table table = new Table(composite, SWT.HIDE_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		final TableColumn column1 = new TableColumn(table, SWT.None);
		column1.setText("Port");
		column1.setWidth(80);
		final TableColumn column2 = new TableColumn(table, SWT.None);
		column2.setText("Description");
		column2.setWidth(300);
		for(int i = 0; i < profile.getControlDevices().getControlDevices().size(); i++) {
			TableItem item = new TableItem(table, SWT.None);
			item.setText(1, profile.getControlDevices().getControlDevices().get(i).getDeviceDescription().getDescription());
			item.setText(0, Long.toString(profile.getControlDevices().getControlDevices().get(i).getDeviceDescription().getAdr()));
			item.setData(profile.getControlDevices().getControlDevices().get(i));
		}
		return composite;
	}
}
