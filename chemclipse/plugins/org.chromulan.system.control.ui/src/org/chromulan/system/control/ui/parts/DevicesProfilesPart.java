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
package org.chromulan.system.control.ui.parts;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.manager.events.IDataSupplierEvents;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.ui.devices.support.ProfileDialog;
import org.chromulan.system.control.ui.wizard.WizardNewDevicesProfile;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class DevicesProfilesPart {

	final public static String ID = "org.chromulan.system.control.ui.part.devicesProfiles";
	private List<IAcquisition> actualAcquisition;
	@Inject
	private DataSupplier dataSupplier;
	@Inject
	private Display display;
	private List<IDevicesProfile> profiles;
	private Table tableProfiles;

	public DevicesProfilesPart() {
		actualAcquisition = new LinkedList<>();
	}

	@Inject
	@Optional
	public void addActualAcqusition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET) IAcquisition acquisition) {

		if(acquisition != null && !actualAcquisition.contains(acquisition)) {
			actualAcquisition.add(acquisition);
		}
	}

	private void addProfile() {

		List<IControlDevice> devices = getControlDevice();
		if(devices != null) {
			WizardNewDevicesProfile wizard = new WizardNewDevicesProfile(devices);
			WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), wizard);
			if(wizardDialog.open() == Window.OK) {
				profiles.add(wizard.getDevicesProfile());
				redrawTable();
			}
		} else {
			// TODO:alert
		}
	}

	@PostConstruct
	public void createParte(Composite parent) {

		this.profiles = dataSupplier.getDevicesProfiles();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parent.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		tableProfiles = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableProfiles.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				int i = tableProfiles.getSelectionIndex();
				if(i != -1) {
					editProfile((IDevicesProfile)tableProfiles.getItem(i).getData());
				}
			}
		});
		tableProfiles.setLayoutData(gridData);
		final Button buttonaddProfile = new Button(parent, SWT.PUSH);
		buttonaddProfile.setText("add");
		buttonaddProfile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addProfile();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonaddProfile.setLayoutData(gridData);
		final Button buttonremoveProfile = new Button(parent, SWT.PUSH);
		buttonremoveProfile.setText("remove");
		buttonremoveProfile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(tableProfiles.getSelectionIndex() != -1) {
					removeProfile(tableProfiles.getSelectionIndex());
				}
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonremoveProfile.setLayoutData(gridData);
		redrawTable();
	}

	private void editProfile(IDevicesProfile devicesProfile) {

		ProfileDialog dialog = new ProfileDialog(display.getActiveShell(), devicesProfile);
		dialog.open();
	}

	private List<IControlDevice> getControlDevice() {

		return dataSupplier.getControlDevices();
	}

	private void redrawTable() {

		tableProfiles.removeAll();
		for(IDevicesProfile iDevicesProfile : profiles) {
			TableItem item = new TableItem(tableProfiles, SWT.None);
			item.setText(iDevicesProfile.getName());
			item.setData(iDevicesProfile);
		}
	}

	@Inject
	@Optional
	public void removeActualAcqusition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END) IAcquisition acquisition) {

		if(acquisition != null) {
			actualAcquisition.remove(acquisition);
		}
	}

	private void removeProfile(int number) {

		profiles.remove(number);
		redrawTable();
	}

	@Inject
	@Optional
	public void updateProfiles(@UIEventTopic(value = IDataSupplierEvents.TOPIC_DATA_UPDATE_PROFILES) DataSupplier supplier) {

		redrawTable();
	}
}
