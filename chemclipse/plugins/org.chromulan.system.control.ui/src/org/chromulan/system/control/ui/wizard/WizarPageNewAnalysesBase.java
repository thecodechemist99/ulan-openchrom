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

import java.io.File;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizarPageNewAnalysesBase extends WizardPage {

	private File file;

	public WizarPageNewAnalysesBase(String pageName) {

		super(pageName);
	}

	public WizarPageNewAnalysesBase(String pageName, String title, ImageDescriptor titleImage) {

		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		setPageComplete(false);
		final DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
		Label label = new Label(composite, SWT.None);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse..");
		GridData gridData = new GridData(GridData.END, GridData.END, false, false);
		button.setLayoutData(gridData);
		label.setText("Directory: ");
		final Text directory = new Text(composite, SWT.BEGINNING | SWT.BORDER);
		directory.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				String ss = ((Text)e.getSource()).getText();
				File f = new File(ss);
				if(f.isDirectory()) {
					file = f;
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});
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

	public File getFile() {

		return file;
	}
}
