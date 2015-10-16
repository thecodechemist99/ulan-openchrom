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
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.chromatogram.IChromatogramRecording;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ChromatogramViewer {

	final private int DISPLAY_ALL_CHROMATOGRAM = 1;
	final private int DISPLAY_INTERVAL_CHROMATOGRAM = 2;
	private long interval;
	private int intervalOverWrite;
	private int diplayChromatogram;
	private boolean autoRedraw;
	RedrawChromatogram redrawChromatogram;

	public ChromatogramViewer() {

		interval = 1;
		diplayChromatogram = DISPLAY_INTERVAL_CHROMATOGRAM;
		autoRedraw = true;
		redrawChromatogram = new RedrawChromatogram();
	}

	@Inject
	private MPart part;
	@Inject
	private Display display;
	private Button buttonUpdate;
	private Button buttonRedraw;
	private Button buttonViewAll;
	private Button buttonViewLast;
	private ChromatogramOverviewUI chromatogramOverView;
	private IChromatogramRecording chromatogramRecording;

	@PostConstruct
	public void createPartControl(Composite parent) {

		chromatogramRecording = (IChromatogramRecording)part.getObject();
		//
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);
		Composite part1 = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		part1.setLayout(rowLayout);
		buttonUpdate = new Button(part1, SWT.CHECK);
		buttonUpdate.setText("Actualize data");
		buttonUpdate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				redrawChromatogram(buttonUpdate.getSelection());
			}
		});
		buttonRedraw = new Button(part1, SWT.CHECK);
		buttonRedraw.setText("Redraw");
		buttonRedraw.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(buttonRedraw.getSelection()) {
					autoRedraw = true;
				} else {
					autoRedraw = false;
				}
			}
		});
		buttonUpdate.setSelection(true);
		redrawChromatogram(autoRedraw);
		buttonViewAll = new Button(part1, SWT.RADIO);
		buttonViewAll.setText("All scans");
		buttonViewAll.setSelection(true);
		buttonViewLast = new Button(part1, SWT.RADIO);
		buttonViewLast.setText("Last scans");
		Composite part2 = new Composite(parent, SWT.NONE);
		part2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		part2.setLayout(new FillLayout());
		chromatogramOverView = new ChromatogramOverviewUI(part2, SWT.NONE);
		chromatogramOverView.setChromatogram(chromatogramRecording);
	}

	@PreDestroy
	public void preDestroy() {

		display.timerExec(-1, redrawChromatogram);
	}

	private void redrawChromatogram(boolean b) {

		if(b) {
			buttonRedraw.setEnabled(true);
			display.timerExec(0, redrawChromatogram);
		} else {
			buttonRedraw.setEnabled(false);
			display.timerExec(-1, redrawChromatogram);
		}
	}

	private class RedrawChromatogram implements Runnable {

		public RedrawChromatogram() {

		}

		@Override
		public void run() {

			if(autoRedraw) {
				if(diplayChromatogram == DISPLAY_ALL_CHROMATOGRAM) {
					chromatogramOverView.displayAllChromatogram(true, 0.001);
				} else if(diplayChromatogram == DISPLAY_INTERVAL_CHROMATOGRAM) {
					chromatogramOverView.displayInteval(60000, true, 0.001);
				}
			} else {
				chromatogramOverView.reloadData();
			}
			display.timerExec(1000, this);
		}
	}
}
