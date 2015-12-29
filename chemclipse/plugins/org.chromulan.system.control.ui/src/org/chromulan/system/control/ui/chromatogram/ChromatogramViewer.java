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
package org.chromulan.system.control.ui.chromatogram;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.model.data.IChromatogramData;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class ChromatogramViewer {

	private class RedrawChromatogram implements Runnable {

		public RedrawChromatogram() {
		}

		@Override
		public void run() {

			chromatogramOverView.reloadData();
			if(autoRedraw) {
				if(diplayChromatogram == DISPLAY_ALL_CHROMATOGRAM) {
					chromatogramOverView.displayAllChromatogram();
				} else if(diplayChromatogram == DISPLAY_INTERVAL_CHROMATOGRAM) {
					chromatogramOverView.displayInteval();
				}
			}
			display.timerExec(2000, this);
		}
	}

	final static public int DISPLAY_ALL_CHROMATOGRAM = 1;
	final static public int DISPLAY_INTERVAL_CHROMATOGRAM = 2;
	private boolean autoRedraw;
	private int diplayChromatogram;
	@Inject
	private Display display;
	@Inject
	private EHandlerService handlerService;
	private IChromatogramData chromatogramData;
	private ChromatogramOverviewUI chromatogramOverView;
	@Inject
	private MPart part;
	@Inject
	private EPartService partService;
	private RedrawChromatogram redrawChromatogram;

	public ChromatogramViewer() {
		diplayChromatogram = DISPLAY_INTERVAL_CHROMATOGRAM;
		autoRedraw = true;
		redrawChromatogram = new RedrawChromatogram();
	}

	private void addHandler() {

		Object handler = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = DISPLAY_ALL_CHROMATOGRAM;
				chromatogramOverView.displayAllChromatogram();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayAllData", handler);
		Object handler2 = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = DISPLAY_INTERVAL_CHROMATOGRAM;
				chromatogramOverView.displayInteval();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayLastData", handler2);
		Object handler3 = new Object() {

			@Execute
			public void execute() {

				autoRedraw = !autoRedraw;
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.command.chromatogram.displayRefreshData", handler3);
		Object handler4 = new Object() {

			@Execute
			public void execute() {

				PreferenceManager manager = new PreferenceManager();
				ChromatogramPreferencePage page = new ChromatogramPreferencePage(chromatogramOverView);
				PreferenceNode mainNode = new PreferenceNode("Main", page);
				manager.addToRoot(mainNode);
				PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
				dialog.open();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.command.chromatogram.displaySettings", handler4);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		chromatogramData = (IChromatogramData)part.getObject();
		redrawChromatogram(autoRedraw);
		chromatogramOverView = new ChromatogramOverviewUI(parent, SWT.NONE);
		chromatogramOverView.setChromatogram(chromatogramData);
		addHandler();
	}

	@PreDestroy
	public void destroyPart() {

		partService.hidePart(part, true);
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
