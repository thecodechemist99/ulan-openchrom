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
package org.chromulan.system.control.ui.wizard;

import org.chromulan.system.control.model.DevicesProfile;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.IDevicesProfile;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class WizarsNewDevicesProfilePageOne extends WizardPage {

	private IControlDevices controlDevices;
	private IDevicesProfile profile;

	public WizarsNewDevicesProfilePageOne(IControlDevices controlDevices) {

		super("New Devices Profile");
		profile = new DevicesProfile();
		this.controlDevices = controlDevices;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		final Label label = new Label(composite, SWT.BEGINNING);
		label.setText("Name");
		final Text nameText = new Text(composite, SWT.None);
		nameText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String ss = nameText.getText();
				if(ss.isEmpty()) {
					setErrorMessage("Name is empty.");
					setPageComplete(false);
				} else if(!ss.trim().equals(ss)) {
					setErrorMessage("Name can not start or finish whitespace");
					setPageComplete(false);
				} else {
					setErrorMessage(null);
					profile.setName(ss);
					setPageComplete(true);
				}
			}
		});
		final Table table = new Table(composite, SWT.CHECK | SWT.NO_FOCUS | SWT.HIDE_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);// );
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail == SWT.CHECK) {
					IControlDevice device = ((IControlDevice)event.item.getData());
					if(profile.getControlDevices().contains(device.getID())) {
						profile.getControlDevices().remove(device.getID());
					} else {
						profile.getControlDevices().add(device);
					}
				}
			}
		});
		table.setHeaderVisible(true);
		final TableColumn column1 = new TableColumn(table, SWT.None);
		column1.setText("Port");
		column1.setWidth(80);
		final TableColumn column2 = new TableColumn(table, SWT.None);
		column2.setText("Description");
		column2.setWidth(300);
		for(int i = 0; i < controlDevices.getControlDevices().size(); i++) {
			TableItem item = new TableItem(table, SWT.None);
			item.setText(1, controlDevices.getControlDevices().get(i).getDeviceDescription().getDescription());
			item.setText(0, Long.toString(controlDevices.getControlDevices().get(i).getDeviceDescription().getAdr()));
			item.setData(controlDevices.getControlDevices().get(i));
		}
		WizardNewDevicesProfile wizardNewDevicesProfile = (WizardNewDevicesProfile)getWizard();
		wizardNewDevicesProfile.setDevicesProfile(profile);
		setErrorMessage("Name is empty.");
		GridLayoutFactory.swtDefaults().numColumns(1).generateLayout(composite);
		setControl(composite);
	}
}
