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

import java.io.File;
import java.util.List;

import org.chromulan.system.control.model.IAnalysisCSD;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AnalysisCDSSavePreferencePage extends PreferencePage {

	private IAnalysisCSD analysis;
	private Button buttonSelectDirectory;
	private DataBindingContext dbc;
	private Text directory;
	private IObservableValue file;
	private IObservableValue supplier;

	public AnalysisCDSSavePreferencePage(IAnalysisCSD analysis) {
		super("Save");
		setAnalysis(analysis);
	}

	public AnalysisCDSSavePreferencePage(String title, IAnalysisCSD analysis) {
		super(title);
		setAnalysis(analysis);
	}

	public AnalysisCDSSavePreferencePage(String title, ImageDescriptor image, IAnalysisCSD analysis) {
		super(title, image);
		setAnalysis(analysis);
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		PreferencePageSupport.create(this, dbc);
		Label label = new Label(composite, SWT.None);
		label.setText("Type File");
		ComboViewer combo = new ComboViewer(composite, SWT.READ_ONLY);
		ChromatogramConverterSupport support = ChromatogramConverterCSD.getChromatogramConverterSupport();
		List<ISupplier> suppliers = support.getExportSupplier();
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				ISupplier supplier = (ISupplier)element;
				return supplier.getFilterName();
			}
		});
		combo.setInput(suppliers);
		IViewerObservableValue observeCombo = ViewerProperties.singleSelection().observe(combo);
		dbc.bindValue(observeCombo, this.supplier, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		label = new Label(composite, SWT.None);
		final DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
		label = new Label(composite, SWT.None);
		label.setText("Directory: ");
		buttonSelectDirectory = new Button(composite, SWT.PUSH);
		buttonSelectDirectory.setText("Browse..");
		directory = new Text(composite, SWT.BEGINNING | SWT.BORDER);
		IObservableValue observeDirectory = WidgetProperties.text(SWT.Modify).observe(directory);
		dbc.bindValue(observeDirectory, file, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorDirectory()).setConverter(new StringToFile()), new UpdateValueStrategy().setConverter(new FileToString()));
		buttonSelectDirectory.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String ss = dialog.open();
				if(ss != null) {
					directory.setText(ss);
				}
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(1).generateLayout(composite);
		return composite;
	}

	private void disableEdition() {

		directory.setEnabled(false);
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
		setErrors();
	}

	@Override
	public boolean performOk() {

		if(analysis.isCompleted()) {
			performDefaults();
			disableEdition();
			return false;
		} else {
			dbc.updateModels();
			analysis.getAnalysisCSDSaver().setFile((File)file.getValue());
			analysis.getAnalysisCSDSaver().setSuplier((ISupplier)supplier.getValue());
			return true;
		}
	}

	private void setAnalysis(IAnalysisCSD analysis) {

		this.analysis = analysis;
		file = new WritableValue(analysis.getAnalysisCSDSaver().getFile(), File.class);
		supplier = new WritableValue(analysis.getAnalysisCSDSaver().getSupplier(), ISupplier.class);
		this.dbc = new DataBindingContext();
	}

	private void setErrors() {

		if(analysis.isCompleted()) {
			setErrorMessage("Can not change analysis because Anaysis has been recorded");
			disableEdition();
		}
	}
}
