package org.chromulan.system.control.net;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import net.sourceforge.ulan.base.ULanDrv;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {

		Activator.context = bundleContext;
		ULanDrv.loadLibrary(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		Activator.context = null;
	}
}
