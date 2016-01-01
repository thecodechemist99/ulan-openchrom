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
package org.chromulan.system.control.ui.acquisition.support;

import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AcquisitionSettingsPreferencePage extends PreferencePage {

	private IAcquisition acquisition;
	private Button buttonAutoContinue;
	private Button buttonAutoStop;
	private DataBindingContext dbc;
	private Text textDescription;
	private Text textDuration;
	private Text textName;

	public AcquisitionSettingsPreferencePage(IAcquisition acquisition) {
		super("Main");
		this.acquisition = acquisition;
		this.dbc = new DataBindingContext();
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		Label label = new Label(composite, SWT.None);
		label.setText("Name of acquisition");
		this.textName = new Text(composite, SWT.BORDER);
		label = new Label(composite, SWT.None);
		label.setText("Auto Continue");
		buttonAutoContinue = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		buttonAutoStop = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.None);
		label.setText("Duration (min)");
		textDuration = new Text(composite, SWT.BORDER);
		label = new Label(composite, SWT.None);
		label.setText("Description");
		textDescription = new Text(composite, SWT.MULTI | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 3 * textDescription.getLineHeight();
		textDescription.setLayoutData(gridData);
		PreferencePageSupport.create(this, dbc);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textName), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_NAME).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterGetValidator(new ValidatorName()), null);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_AUTO_CONTINUE).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoStop), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_AUTO_STOP).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textDuration), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DURATION).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorDuration()).setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		dbc.bindValue(WidgetProperties.enabled().observe(textDuration), WidgetProperties.selection().observe(buttonAutoStop));
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textDescription), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DESCRIPTION).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		return composite;
	}

	private void disableEdition() {

		textName.setEnabled(false);
		textDuration.setEnabled(false);
		buttonAutoContinue.setEnabled(false);
		buttonAutoStop.setEnabled(false);
		textDescription.setEnabled(false);
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
		setErrors();
	}

	@Override
	public boolean performOk() {

		if(acquisition.isCompleted()) {
			performDefaults();
			setErrors();
			return false;
		} else {
			dbc.updateModels();
			return true;
		}
	}

	private void setErrors() {

		if(acquisition.isCompleted()) {
			setErrorMessage("Can not change acquisition because Anaysis has been recorded");
			disableEdition();
		}
	}
}
