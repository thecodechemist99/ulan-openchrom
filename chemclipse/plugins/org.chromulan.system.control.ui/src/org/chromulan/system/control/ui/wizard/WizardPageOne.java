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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;


public class WizardPageOne extends WizardPage {
	
	protected Composite container;
	protected Text name;
	protected String nameS;
	
	
	public WizardPageOne() {
		super("New Anlysis");
		setTitle("New Anlysis");
		setPageComplete(false);
		
		
	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    
	    
	    
	    
	    
	    
	    GridData gd = new GridData(GridData.FILL,GridData.FILL,true,false);
	    Label label = new Label(container, SWT.LEFT);
	    label.setText("Set name of analysis: ");
	    label.setLayoutData(gd);
	    
	   
	    name = new Text(container, SWT.BORDER | SWT.SINGLE);
	    name.setText("");
	    name.setLayoutData(gd);
	    
	    name.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
			
				setPageComplete(isPageComplete());
				nameS = name.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
	    gd = new GridData(GridData.FILL,GridData.FILL,true,false);
	    name.setLayoutData(gd);

	    
	    
	    setControl(container);
	    
	}
	
	public String getAnalysisName()
	{	
		return nameS;
	}
	
	@Override
	public boolean isPageComplete() {
		return !name.getText().isEmpty();
	}
	
	
	 
	


}
