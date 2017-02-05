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
package org.chromulan.system.control.ui.wizard;

import java.util.List;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
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
		String classAcquistion = model.acquisitionType.getValue();
		if((IAcquisitionCSD.class.getName().equals(classAcquistion))) {
			for(IControlDevice device : profile.getControlDevices()) {
				if((device.getFlg() & IControlDevice.FLG_SUPPORT_CSD_CHROMATOGRAM) == 0) {
					return false;
				}
			}
			return true;
		} else if((IAcquisitionMSD.class.getName().equals(classAcquistion))) {
			for(IControlDevice device : profile.getControlDevices()) {
				if((device.getFlg() & IControlDevice.FLG_SUPPORT_MSD_CHROMATOGRAM) == 0) {
					return false;
				}
			}
			return true;
		} else if((IAcquisitionWSD.class.getName().equals(classAcquistion))) {
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

		Composite rootComposite = new Composite(parent, SWT.None);
		rootComposite.setLayout(new FillLayout());
		ScrolledComposite scrollComposite = new ScrolledComposite(rootComposite, SWT.BORDER | SWT.V_SCROLL);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		Composite composite = new Composite(scrollComposite, SWT.None);
		scrollComposite.setContent(composite);
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		Group group = new Group(composite, SWT.V_SCROLL);
		group.setText("Devices Profile");
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		SelectObservableValue<IDevicesProfile> selectedRadioButtonObservable = new SelectObservableValue<>();
		for(IDevicesProfile profile : devicesProfil) {
			Button button = new Button(group, SWT.RADIO);
			button.setText(profile.getName());
			selectedRadioButtonObservable.addOption(profile, WidgetProperties.selection().observe(button));
		}
		dbc.bindValue(selectedRadioButtonObservable, model.devicesProfile, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDevicesProfile()), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		scrollComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(rootComposite);
	}

	@Override
	public boolean isPageComplete() {

		setErrorMessage(null);
		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		IDevicesProfile profile = model.devicesProfile.getValue();
		if(profile != null) {
			if(!controlProfile(profile)) {
				setErrorMessage("Device in profile does not support this type acquisition");
				return false;
			}
		}
		return super.isPageComplete();
	}
}
