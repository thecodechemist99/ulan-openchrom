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

import org.chromulan.system.control.ui.analysis.support.MillisecondsToMinutes;
import org.chromulan.system.control.ui.analysis.support.MinutesToMilliseconds;
import org.chromulan.system.control.ui.analysis.support.ValidatorDuration;
import org.chromulan.system.control.ui.analysis.support.ValidatorName;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAnalysisMain extends WizardPage {

	public WizardNewAnalysisMain() {

		super("New Analysis");
		setTitle("Paramenter of Analysis");
	}

	public WizardNewAnalysisMain(String pageName) {

		super(pageName);
	}

	public WizardNewAnalysisMain(String pageName, String title, ImageDescriptor titleImage) {

		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		Label label = new Label(composite, SWT.None);
		label.setText("Name of analysis");
		final Text textName = new Text(composite, SWT.BORDER);
		WizardModelAnalysis model = ((WizardNewAnalysis)getWizard()).getModel();
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textName), model.name, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorName()), null);
		label = new Label(composite, SWT.None);
		label.setText("Auto Continue");
		final Button buttonAutoContinue = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), model.autoContinue);
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		final Button buttonAutoStop = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoStop), model.autoStop);
		label = new Label(composite, SWT.None);
		label.setText("Duration (min)");
		final Text textInterval = new Text(composite, SWT.BORDER);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textInterval), model.duration, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDuration()).setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		label = new Label(composite, SWT.None);
		label.setText("Description");
		final Text textDescription = new Text(composite, SWT.MULTI | SWT.V_SCROLL);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textDescription), model.description);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 3 * textDescription.getLineHeight();
		textDescription.setLayoutData(gridData);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}
}
