/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
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

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitionCSD;
import org.chromulan.system.control.model.IAcquisitionMSD;
import org.chromulan.system.control.model.IAcquisitionWSD;
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

	private boolean controlProfile(IDevicesProfile profile) {

		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		Class<? extends IAcquisition> classAcquistion = (Class<? extends IAcquisition>)model.acquisitionType.getValue();
		if((IAcquisitionCSD.class.isAssignableFrom(classAcquistion))) {
			for(IControlDevice device : profile.getControlDevices()) {
				if((device.getFlg() & IControlDevice.FLG_SUPPORT_CSD_CHROMATOGRAM) == 0) {
					return false;
				}
			}
			return true;
		} else if((IAcquisitionMSD.class.isAssignableFrom(classAcquistion))) {
			for(IControlDevice device : profile.getControlDevices()) {
				if((device.getFlg() & IControlDevice.FLG_SUPPORT_MSD_CHROMATOGRAM) == 0) {
					return false;
				}
			}
			return true;
		} else if((IAcquisitionWSD.class.isAssignableFrom(classAcquistion))) {
			for(IControlDevice device : profile.getControlDevices()) {
				if((device.getFlg() & IControlDevice.FLG_SUPPORT_WSD_CHROMATOGRAM) == 0) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
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

	@Override
	public boolean isPageComplete() {

		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		IDevicesProfile profile = (IDevicesProfile)model.devicesProfile.getValue();
		if(profile != null) {
			if(!controlProfile(profile)) {
				setErrorMessage("Device in profile does not support this type acquisition");
				return false;
			}
		}
		return super.isPageComplete();
	}
}
