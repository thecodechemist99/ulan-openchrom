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

import org.chromulan.system.control.ui.analysis.support.ValidatorNumberOfAnalyses;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAnalysisMultiple extends WizardPage {

	public WizardNewAnalysisMultiple() {

		super("New Analysis");
		setTitle("Set multiple analysis");
	}

	public WizardNewAnalysisMultiple(String pageName) {

		super(pageName);
	}

	public WizardNewAnalysisMultiple(String pageName, String title, ImageDescriptor titleImage) {

		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		WizardModelAnalysis model = ((WizardNewAnalysis)getWizard()).getModel();
		Label label = new Label(composite, SWT.None);
		label.setText("Number of Analyses");
		final Text textNumber = new Text(composite, SWT.BORDER);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textNumber), model.numberAnalyses, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorNumberOfAnalyses()), null);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}
}
