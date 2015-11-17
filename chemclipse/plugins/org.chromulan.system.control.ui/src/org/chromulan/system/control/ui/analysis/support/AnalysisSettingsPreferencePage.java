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
package org.chromulan.system.control.ui.analysis.support;

import org.chromulan.system.control.model.IAnalysis;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AnalysisSettingsPreferencePage extends PreferencePage {

	private IAnalysis analysis;
	private Button buttonAutoContinue;
	private Button buttonAutoStop;
	private Button buttonDefault;
	private DataBindingContext dbc;
	private Text textDescription;
	private Text textInterval;
	private Text textName;
	private boolean updatable;

	public AnalysisSettingsPreferencePage(IAnalysis analysis, boolean updatable) {

		super("Main");
		this.analysis = analysis;
		this.dbc = new DataBindingContext();
		this.updatable = updatable;
		noDefaultAndApplyButton();
	}

	@Override
	protected void contributeButtons(Composite parent) {

		buttonDefault = new Button(parent, SWT.PUSH);
		buttonDefault.setText("Default");
		buttonDefault.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				performDefaults();
			}
		});
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		Point minButtonSize = buttonDefault.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, minButtonSize.x);
		buttonDefault.setLayoutData(data);
		GridLayout gd = (GridLayout)parent.getLayout();
		gd.numColumns++;
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		Label label = new Label(composite, SWT.None);
		label.setText("Name of analysis");
		this.textName = new Text(composite, SWT.BORDER);
		label = new Label(composite, SWT.None);
		label.setText("Auto Continue");
		buttonAutoContinue = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.None);
		label.setText("Auto Stop");
		buttonAutoStop = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.None);
		label.setText("Interval");
		textInterval = new Text(composite, SWT.BORDER);
		label = new Label(composite, SWT.None);
		label.setText("Description");
		textDescription = new Text(composite, SWT.MULTI | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 3 * textDescription.getLineHeight();
		textDescription.setLayoutData(gridData);
		PreferencePageSupport.create(this, dbc);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textName), BeanProperties.value(IAnalysis.class, IAnalysis.PROPERTY_NAME).observe(analysis), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterGetValidator(new ValidatorName()), null);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), BeanProperties.value(IAnalysis.class, IAnalysis.PROPERTY_AUTO_CONTINUE).observe(analysis), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoStop), BeanProperties.value(IAnalysis.class, IAnalysis.PROPERTY_AUTO_STOP).observe(analysis), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textInterval), BeanProperties.value(IAnalysis.class, IAnalysis.PROPERTY_INTERVAL).observe(analysis), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorInterval()).setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		dbc.bindValue(WidgetProperties.enabled().observe(textInterval), WidgetProperties.selection().observe(buttonAutoStop));
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textDescription), BeanProperties.value(IAnalysis.class, IAnalysis.PROPERTY_DESCRIPTION).observe(analysis), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		if(!updatable || analysis.hasBeenRecorded()) {
			disableEdition();
			if(updatable) {
				setErrors();
			}
		} else {
		}
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		return composite;
	}

	private void disableEdition() {

		textName.setEnabled(false);
		textInterval.setEnabled(false);
		buttonAutoContinue.setEnabled(false);
		buttonAutoStop.setEnabled(false);
		textDescription.setEnabled(false);
	}

	@Override
	protected Button getDefaultsButton() {

		return buttonDefault;
	}

	@Override
	public boolean isValid() {

		return super.isValid();
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
		setErrors();
	}

	@Override
	public boolean performOk() {

		if(analysis.hasBeenRecorded()) {
			performDefaults();
			disableEdition();
			return false;
		} else {
			dbc.updateModels();
			return true;
		}
	}

	private void setErrors() {

		if(analysis.hasBeenRecorded()) {
			setErrorMessage("Can not change analysis because Anaysis has been recorded");
		}
	}
}
