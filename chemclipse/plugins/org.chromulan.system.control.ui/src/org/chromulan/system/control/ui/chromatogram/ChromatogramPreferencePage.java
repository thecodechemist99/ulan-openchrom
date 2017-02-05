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
package org.chromulan.system.control.ui.chromatogram;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ChromatogramPreferencePage extends PreferencePage {

	private DataBindingContext dbc;
	private ChromatogramOverviewUI chromatogramOverviewUI;
	private IObservableValue<Double> interval;
	private IObservableValue<Double> minYAdjustIntensity;

	public ChromatogramPreferencePage(ChromatogramOverviewUI chromatogramOverviewUI) {
		super("Main");
		this.chromatogramOverviewUI = chromatogramOverviewUI;
		minYAdjustIntensity = new WritableValue<>(chromatogramOverviewUI.getMinYAdjustIntensity(), Double.class);
		interval = new WritableValue<>(chromatogramOverviewUI.getInterval(), Double.class);
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		dbc = new DataBindingContext();
		PreferencePageSupport.create(this, dbc);
		Label label = new Label(composite, SWT.None);
		label.setText("Interval (min)");
		Text textInterval = new Text(composite, SWT.None);
		setDBCInterval(WidgetProperties.text(SWT.Modify).observe(textInterval), interval);
		label = new Label(composite, SWT.None);
		label.setText("Minimal Y adjust Intensity");
		Text textIntensity = new Text(composite, SWT.None);
		setDBCAutoMinYAdjustIntensity(WidgetProperties.text(SWT.Modify).observe(textIntensity), minYAdjustIntensity);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		return composite;
	}

	@Override
	protected void performDefaults() {

		minYAdjustIntensity.setValue(chromatogramOverviewUI.getMinYAdjustIntensity());
		interval.setValue(chromatogramOverviewUI.getInterval());
	}

	@Override
	public boolean performOk() {

		chromatogramOverviewUI.setMinYAdjustIntensity(minYAdjustIntensity.getValue());
		chromatogramOverviewUI.setInterval(interval.getValue());
		return true;
	}

	private void setDBCAutoMinYAdjustIntensity(ISWTObservableValue targetObservableValue, IObservableValue<Double> modelObservableValue) {

		IValidator validator = new IValidator() {

			@Override
			public IStatus validate(Object value) {

				if(value instanceof Double) {
					Double interval = (Double)value;
					if(interval > 0) {
						return ValidationStatus.ok();
					} else {
						return ValidationStatus.error("Number have to be positive.");
					}
				}
				return ValidationStatus.error("It is not number.");
			}
		};
		Converter stringToDouble = new Converter(String.class, Double.class) {

			@Override
			public Object convert(Object fromObject) {

				if(fromObject instanceof String) {
					String variable = (String)fromObject;
					try {
						return Double.parseDouble(variable);
					} catch(NumberFormatException e) {
						return null;
					}
				} else {
					return null;
				}
			}
		};
		Converter doubleToString = new Converter(Double.class, String.class) {

			@Override
			public Object convert(Object fromObject) {

				if(fromObject instanceof Double) {
					Double d = (Double)fromObject;
					return Double.toString(d);
				}
				return null;
			}
		};
		dbc.bindValue(targetObservableValue, modelObservableValue, new UpdateValueStrategy().setAfterConvertValidator(validator).setConverter(stringToDouble), new UpdateValueStrategy().setConverter(doubleToString));
	}

	private void setDBCInterval(ISWTObservableValue targetObservableValue, IObservableValue<Double> modelObservableValue) {

		Converter stringToDouble = new Converter(String.class, Double.class) {

			@Override
			public Object convert(Object fromObject) {

				if(fromObject instanceof String) {
					String variable = (String)fromObject;
					try {
						return Double.parseDouble(variable) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
					} catch(NumberFormatException e) {
						return null;
					}
				} else {
					return null;
				}
			}
		};
		Converter doubleToString = new Converter(Double.class, String.class) {

			@Override
			public Object convert(Object fromObject) {

				if(fromObject instanceof Double) {
					Double d = (Double)fromObject;
					return Double.toString(d / (IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				}
				return null;
			}
		};
		IValidator validator = new IValidator() {

			@Override
			public IStatus validate(Object value) {

				if(value instanceof Double) {
					Double interval = (Double)value;
					if(interval > 0) {
						return ValidationStatus.ok();
					} else {
						return ValidationStatus.error("Number have to be positive.");
					}
				}
				return ValidationStatus.error("It is not number.");
			}
		};
		dbc.bindValue(targetObservableValue, modelObservableValue, new UpdateValueStrategy().setConverter(stringToDouble).setAfterConvertValidator(validator), new UpdateValueStrategy().setConverter(doubleToString));
	}
}
