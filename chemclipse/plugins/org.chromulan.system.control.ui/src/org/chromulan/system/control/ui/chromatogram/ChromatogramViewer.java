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

import org.chromulan.system.control.model.chromatogram.IChromatogramRecording;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ChromatogramViewer {

	private class RedrawChromatogram implements Runnable {

		public RedrawChromatogram() {

		}

		@Override
		public void run() {

			chromatogramOverView.reloadData();
			if(autoRedraw) {
				if(diplayChromatogram == ChromatogramViewerModel.DISPLAY_ALL_CHROMATOGRAM) {
					chromatogramOverView.displayAllChromatogram();
				} else if(diplayChromatogram == ChromatogramViewerModel.DISPLAY_INTERVAL_CHROMATOGRAM) {
					chromatogramOverView.displayInteval();
				}
			}
			display.timerExec(2000, this);
		}
	}

	private boolean autoRedraw;
	@SuppressWarnings("restriction")
	@Inject
	private ECommandService commandService;
	@Inject
	MContext context;
	private int diplayChromatogram;
	@Inject
	private Display display;
	@SuppressWarnings("restriction")
	@Inject
	private EHandlerService handlerService;
	private ChromatogramOverviewUI chromatogramOverView;
	private IChromatogramRecording chromatogramRecording;
	@Inject
	private MPart part;
	private RedrawChromatogram redrawChromatogram;

	public ChromatogramViewer() {

		diplayChromatogram = ChromatogramViewerModel.DISPLAY_INTERVAL_CHROMATOGRAM;
		autoRedraw = true;
		redrawChromatogram = new RedrawChromatogram();
	}

	@SuppressWarnings("restriction")
	private void addHandler() {

		commandService.getCommand("org.chromulan.system.control.ui.chromatogram.displayAllData");
		Object handler = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = ChromatogramViewerModel.DISPLAY_ALL_CHROMATOGRAM;
				chromatogramOverView.displayAllChromatogram();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayAllData", handler);
		commandService.getCommand("org.chromulan.system.control.ui.chromatogram.displayLastData");
		Object handler2 = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = ChromatogramViewerModel.DISPLAY_INTERVAL_CHROMATOGRAM;
				chromatogramOverView.displayInteval();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayLastData", handler2);
		commandService.getCommand("org.chromulan.system.control.ui.command.chromatogram.displayRefreshData");
		Object handler3 = new Object() {

			@Execute
			public void execute() {

				autoRedraw = !autoRedraw;
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.command.chromatogram.displayRefreshData", handler3);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		chromatogramRecording = (IChromatogramRecording)part.getObject();
		redrawChromatogram(autoRedraw);
		chromatogramOverView = new ChromatogramOverviewUI(parent, SWT.NONE);
		chromatogramOverView.setChromatogram(chromatogramRecording);
		addHandler();
	}

	@PreDestroy
	public void destroyPart() {

		display.timerExec(-1, redrawChromatogram);
	}

	private void redrawChromatogram(boolean b) {

		if(b) {
			display.timerExec(0, redrawChromatogram);
		} else {
			display.timerExec(-1, redrawChromatogram);
		}
	}
}
