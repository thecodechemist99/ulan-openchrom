/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class WizardNewAcquisitionType extends WizardPage {

	protected WizardNewAcquisitionType() {
		super("Select acquisition type");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		Group group = new Group(composite, SWT.V_SCROLL);
		group.setText("Acquisition Type");
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		SelectObservableValue<String> selectedRadioButtonObservable = new SelectObservableValue<>();
		Button button = new Button(group, SWT.RADIO);
		button.setText("Acquisitin CSD");
		selectedRadioButtonObservable.addOption(IAcquisitionCSD.class.getName(), WidgetProperties.selection().observe(button));
		button = new Button(group, SWT.RADIO);
		button.setText("Acquisitin MSD");
		selectedRadioButtonObservable.addOption(IAcquisitionMSD.class.getName(), WidgetProperties.selection().observe(button));
		button = new Button(group, SWT.RADIO);
		button.setText("Acquisitin WSD");
		selectedRadioButtonObservable.addOption(IAcquisitionWSD.class.getName(), WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservable, model.acquisitionType, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDevicesProfile()), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}
}
