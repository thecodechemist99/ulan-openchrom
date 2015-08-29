/*******************************************************************************
 * Copyright (c) 2015 Jan Holý.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holý - initial API and implementation
*******************************************************************************/
package org.chromulan.system.control.ui.chromatogram;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.model.IControlDevice;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram.EditorChromatogramUI;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class ChromatogramViewer {

	
	
	@Inject
	MPart part;

	Button buttonUpdate;
	Button buttonViewAll;
	Button buttonViewLast;
	
	IControlDevice device;
	
	ChromatogramSelectionWSD chromatogramSelection;

	ChromatogramOverviewUI chromatogramOverView;
	
	@PostConstruct
	public void createPartControl(Composite parent){
		

		device = (IControlDevice)part.getObject();
	
		
		
		
		/*try {
			chromatogramSelection = new ChromatogramSelectionWSD(device.getChromatogram());
			chromatogramSelection.getSelectedScan();
		*/	
			GridLayout gridLayout = new GridLayout();
			
			parent.setLayout(gridLayout);
			
			Composite part1 = new Composite(parent, SWT.NONE);
			RowLayout rowLayout = new RowLayout();
			rowLayout.type = SWT.HORIZONTAL;
			
			part1.setLayout(rowLayout);
			
			buttonUpdate = new Button(part1, SWT.CHECK);
			buttonUpdate.setText("Update");
			
			buttonViewAll = new Button(part1, SWT.RADIO);
			buttonViewAll.setText("All scans");
			buttonViewAll.setSelection(true);
			
			buttonViewLast = new Button(part1, SWT.RADIO);
			buttonViewLast.setText("Last scans");
			
			
			
			
			
			Composite part2 = new Composite(parent, SWT.NONE);
			part2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			part2.setLayout(new FillLayout());
			chromatogramOverView = new ChromatogramOverviewUI(part2, SWT.NONE);
			//chromatogramOverView.setViewSeries();
			
			
		/*	
			
		} catch(UnsupportedOperationException e) {
			//logger.warn(e);
		} catch(ChromatogramIsNullException e) {
			//logger.warn(e);
		}
		*/
		
		
	}
}
