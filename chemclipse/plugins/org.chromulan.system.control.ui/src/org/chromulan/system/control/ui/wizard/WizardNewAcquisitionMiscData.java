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

import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAcquisitionMiscData extends WizardPage {

	public WizardNewAcquisitionMiscData() {
		super("Misc data");
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
		Label label = new Label(composite, SWT.None);
		label.setText("column");
		Text text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.column);
		label = new Label(composite, SWT.None);
		label.setText("mobil phase");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.mobilPhase);
		label = new Label(composite, SWT.None);
		label.setText("Flow rate");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.flowRate);
		label = new Label(composite, SWT.None);
		label.setText("Flow rate unit");
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(IAcquisition.FLOW_RATE_UNIT_ML_MIN);
		combo.add(IAcquisition.FLOW_RATE_UNIT_UL_MIN);
		dbc.bindValue(WidgetProperties.selection().observe(combo), model.flowRateUnit);
		label = new Label(composite, SWT.None);
		label.setText("Detection");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.detection);
		label = new Label(composite, SWT.None);
		label.setText("Temperature");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.temperature);
		label = new Label(composite, SWT.None);
		label.setText("Temperature unit");
		combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(IAcquisition.TEMPERATURE_UNIT_C);
		combo.add(IAcquisition.TEMPERATURE_UNIT_F);
		combo.add(IAcquisition.TEMPERATURE_UNIT_K);
		dbc.bindValue(WidgetProperties.selection().observe(combo), model.temperatureUnit);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		scrollComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(rootComposite);
	}
}
