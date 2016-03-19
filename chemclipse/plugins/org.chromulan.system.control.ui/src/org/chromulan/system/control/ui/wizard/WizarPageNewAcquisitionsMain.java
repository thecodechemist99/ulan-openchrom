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
package org.chromulan.system.control.ui.wizard;

import java.io.File;
import java.util.List;

import org.chromulan.system.control.ui.acquisition.support.FileToString;
import org.chromulan.system.control.ui.acquisition.support.StringToFile;
import org.chromulan.system.control.ui.acquisition.support.ValidatorDirectory;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizarPageNewAcquisitionsMain extends WizardPage {

	private final String DEFAULT_SUPPLIER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	private String defFile;
	private String defSupplier;
	private IObservableValue file;
	private IObservableValue supplier;

	public WizarPageNewAcquisitionsMain(String pageName, String title, ImageDescriptor titleImage, String defFile, String defSupplier) {
		super(pageName, title, titleImage);
		this.defFile = defFile;
		this.defSupplier = defSupplier;
	}

	public WizarPageNewAcquisitionsMain(String pageName, String defFile, String defSupplier) {
		this(pageName, null, null, defFile, defSupplier);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);
		setPageComplete(false);
		Label label = new Label(composite, SWT.None);
		label.setText("Type File");
		ComboViewer combo = new ComboViewer(composite, SWT.READ_ONLY);
		ChromatogramConverterSupport support = ChromatogramConverterCSD.getChromatogramConverterSupport();
		List<ISupplier> suppliers = support.getExportSupplier();
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISupplier) {
					ISupplier supplier = (ISupplier)element;
					return supplier.getFilterName();
				}
				return "";
			}
		});
		combo.setInput(suppliers);
		ISupplier defaultSupplier = getDefaultSupplier(suppliers);
		supplier = new WritableValue(defaultSupplier, ISupplier.class);
		IViewerObservableValue observeCombo = ViewerProperties.singleSelection().observe(combo);
		dbc.bindValue(observeCombo, supplier);
		if(defFile != null) {
			file = new WritableValue(new File(defFile), File.class);
		} else {
			file = new WritableValue(null, File.class);
		}
		final DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
		label = new Label(composite, SWT.None);
		label.setText("Directory: ");
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse..");
		GridData gridData = new GridData(GridData.END, GridData.END, false, false);
		button.setLayoutData(gridData);
		final Text directory = new Text(composite, SWT.BEGINNING | SWT.BORDER);
		IObservableValue observeDirectory = WidgetProperties.text(SWT.Modify).observe(directory);
		dbc.bindValue(observeDirectory, file, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorDirectory()).setConverter(new StringToFile()), new UpdateValueStrategy().setConverter(new FileToString()));
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		directory.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String ss = dialog.open();
				if(ss != null) {
					directory.setText(ss);
				}
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}

	private ISupplier getDefaultSupplier(List<ISupplier> suppliers) {

		if(suppliers == null || suppliers.isEmpty()) {
			return null;
		}
		if(defSupplier != null) {
			for(ISupplier supplier : suppliers) {
				if(supplier.getId().equals(defSupplier)) {
					return supplier;
				}
			}
		}
		for(ISupplier supplier : suppliers) {
			if(supplier.getId().equals(DEFAULT_SUPPLIER_ID)) {
				return supplier;
			}
		}
		return suppliers.get(0);
	}

	public File getFile() {

		return (File)file.getValue();
	}

	public ISupplier getSupplier() {

		return (ISupplier)supplier.getValue();
	}
}
