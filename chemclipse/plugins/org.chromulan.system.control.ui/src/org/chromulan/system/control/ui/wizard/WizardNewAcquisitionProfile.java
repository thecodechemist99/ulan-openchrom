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

import java.util.List;

import org.chromulan.system.control.model.IDevicesProfile;
import org.chromulan.system.control.ui.devices.support.ValidatorDevicesProfile;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class WizardNewAcquisitionProfile extends WizardPage {

	private List<IDevicesProfile> devicesProfil;

	public WizardNewAcquisitionProfile(List<IDevicesProfile> devicesProfil) {
		super("New Acquisition ");
		setTitle("Selecet Devices profile");
		this.devicesProfil = devicesProfil;
	}

	public WizardNewAcquisitionProfile(String pageName, List<IDevicesProfile> devicesProfil) {
		super(pageName);
		this.devicesProfil = devicesProfil;
	}

	public WizardNewAcquisitionProfile(String pageName, String title, ImageDescriptor titleImage, List<IDevicesProfile> devicesProfil) {
		super(pageName, title, titleImage);
		this.devicesProfil = devicesProfil;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		if(devicesProfil == null || devicesProfil.isEmpty()) {
			setErrorMessage("Can not find any devices profiles, please create devices profiles");
			setPageComplete(false);
			setControl(composite);
			return;
		}
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		Group group = new Group(composite, SWT.V_SCROLL);
		group.setText("Devices Profile");
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		SelectObservableValue selectedRadioButtonObservable = new SelectObservableValue();
		for(IDevicesProfile profile : devicesProfil) {
			Button button = new Button(group, SWT.RADIO);
			button.setText(profile.getName());
			selectedRadioButtonObservable.addOption(profile, WidgetProperties.selection().observe(button));
		}
		dbc.bindValue(selectedRadioButtonObservable, model.devicesProfile, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDevicesProfile()), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}
}
