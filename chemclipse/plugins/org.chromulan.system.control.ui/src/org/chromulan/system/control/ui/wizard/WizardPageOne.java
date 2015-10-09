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
import org.chromulan.system.control.ui.analysis.support.ValidatorInterval;
import org.chromulan.system.control.ui.analysis.support.ValidatorName;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPageOne extends WizardPage {




	public WizardPageOne() {

		super("New Anlysis");
		setTitle("New Anlysis");

	}

	@Override
	public void createControl(Composite parent) {
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		Label label = new Label(composite, SWT.None);
		label.setText("Name of analysis");
		
		final Text textName = new Text(composite, SWT.BORDER);
		
		WizardModelAnalysis model = ((WizardNewAnalysis) getWizard()).getModel();
		
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textName), model.name,new UpdateValueStrategy().setAfterConvertValidator(new ValidatorName()),null);
		
		label = new Label(composite, SWT.None);
		label.setText("Auto Continue");
		
		final Button buttonAutoContinue = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), model.autoContinue);
		
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		final Button buttonAutoStop = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoStop), model.autoStop);
		
		label = new Label(composite,SWT.None);
		label.setText("Interval");
		final Text textInterval = new Text(composite, SWT.BORDER);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textInterval), model.interval,new UpdateValueStrategy().setAfterConvertValidator(new ValidatorInterval()).setConverter(new MinutesToMilliseconds()),
				new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		
		
		
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(
				composite);
		setControl(composite);
	}



}
