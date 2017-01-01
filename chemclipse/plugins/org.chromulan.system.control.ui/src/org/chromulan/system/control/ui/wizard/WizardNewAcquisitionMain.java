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

import org.chromulan.system.control.ui.acquisition.support.MillisecondsToMinutes;
import org.chromulan.system.control.ui.acquisition.support.MinutesToMilliseconds;
import org.chromulan.system.control.ui.acquisition.support.ValidatorDuration;
import org.chromulan.system.control.ui.acquisition.support.ValidatorName;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAcquisitionMain extends WizardPage {

	public WizardNewAcquisitionMain() {
		super("New Acquisition");
		setTitle("Paramenter of Acquisition");
	}

	public WizardNewAcquisitionMain(String pageName) {
		super(pageName);
	}

	public WizardNewAcquisitionMain(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
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
		Label label = new Label(composite, SWT.None);
		label.setText("Name");
		Text text = new Text(composite, SWT.BORDER);
		WizardModelAcquisition model = ((WizardNewAcquisition)getWizard()).getModel();
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.name, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorName()), null);
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		Button button = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(button), model.autoStop);
		label = new Label(composite, SWT.None);
		label.setText("Duration (min)");
		text = new Text(composite, SWT.BORDER);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.duration, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDuration()).setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		label = new Label(composite, SWT.None);
		label.setText("Description");
		text = new Text(composite, SWT.MULTI | SWT.V_SCROLL);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.description);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 3 * text.getLineHeight();
		text.setLayoutData(gridData);
		label = new Label(composite, SWT.None);
		label.setText("Analysis");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.analysis);
		label = new Label(composite, SWT.None);
		label.setText("Amount");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.amount);
		label = new Label(composite, SWT.None);
		label.setText("ISTD Amount");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.ISTDAmount);
		label = new Label(composite, SWT.None);
		label.setText("Inj.Volume");
		text = new Text(composite, SWT.None);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), model.injectionVolume);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		scrollComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(rootComposite);
	}
}
