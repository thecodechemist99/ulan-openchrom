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
package org.chromulan.system.control.ui.acquisition.support;

import java.util.ArrayList;
import java.util.List;

import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AcquisitionSettingsPreferencePage extends PreferencePage {

	private IAcquisition acquisition;
	private List<Control> controls;
	private DataBindingContext dbc;

	public AcquisitionSettingsPreferencePage(IAcquisition acquisition) {
		super("Main");
		this.acquisition = acquisition;
		this.dbc = new DataBindingContext();
		this.controls = new ArrayList<>(20);
	}

	@Override
	protected Control createContents(Composite parent) {

		PreferencePageSupport.create(this, dbc);
		Composite rootComposite = new Composite(parent, SWT.None);
		rootComposite.setLayout(new FillLayout());
		ScrolledComposite scrollComposite = new ScrolledComposite(rootComposite, SWT.BORDER | SWT.V_SCROLL);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		Composite composite = new Composite(scrollComposite, SWT.None);
		scrollComposite.setContent(composite);
		Label label = new Label(composite, SWT.None);
		label.setText("Name");
		Text text = new Text(composite, SWT.BORDER);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_NAME).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorName()), null);
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		Button button = new Button(composite, SWT.CHECK);
		controls.add(button);
		dbc.bindValue(WidgetProperties.selection().observe(button), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_AUTO_STOP).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Duration (min)");
		text = new Text(composite, SWT.BORDER);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DURATION).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorDuration()).setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		label = new Label(composite, SWT.None);
		label.setText("Description");
		text = new Text(composite, SWT.MULTI | SWT.V_SCROLL);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DESCRIPTION).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 3 * text.getLineHeight();
		text.setLayoutData(gridData);
		label = new Label(composite, SWT.None);
		label.setText("Analysis");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_ANALYSIS).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Amount");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_AMOUNT).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("ISTD Amount");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_ISTD_AMOUNT).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Inj.Volume");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_INJECTION_VOLUME).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("column");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_COLUMN).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("mobil phase");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_MOBIL_PHASE).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Flow rate");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_FLOW_RATE).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Flow rate unit");
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(IAcquisition.FLOW_RATE_UNIT_ML_MIN);
		combo.add(IAcquisition.FLOW_RATE_UNIT_UL_MIN);
		controls.add(combo);
		dbc.bindValue(WidgetProperties.selection().observe(combo), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_FLOW_RATE_UNIT).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Detection");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_DETECTION).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Temperature");
		text = new Text(composite, SWT.None);
		controls.add(text);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_TEMPERATURE).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		label.setText("Temperature unit");
		combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(IAcquisition.TEMPERATURE_UNIT_C);
		combo.add(IAcquisition.TEMPERATURE_UNIT_F);
		combo.add(IAcquisition.TEMPERATURE_UNIT_K);
		controls.add(combo);
		dbc.bindValue(WidgetProperties.selection().observe(combo), BeanProperties.value(IAcquisition.class, IAcquisition.PROPERTY_TEMPERATURE_UNIT).observe(acquisition), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		scrollComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl();
		return rootComposite;
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
	}

	@Override
	public boolean performOk() {

		return setControl();
	}

	public boolean setControl() {

		synchronized(acquisition) {
			if(acquisition.isCompleted() || acquisition.isRunning()) {
				dbc.updateTargets();
				setErrorMessage("Can not change acquisition because Anaysis has been recorded or is recording");
				for(Control control : controls) {
					control.setEnabled(false);
				}
				return false;
			} else {
				dbc.updateModels();
				return true;
			}
		}
	}
}
