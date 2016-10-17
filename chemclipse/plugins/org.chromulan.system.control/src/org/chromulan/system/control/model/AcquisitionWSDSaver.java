package org.chromulan.system.control.model;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AcquisitionWSDSaver extends AbstractAcquisitionSaver implements IAcquisitionWSDSaver {

	public AcquisitionWSDSaver(IAcquisitionWSD acquisitionWSD) {
		super(acquisitionWSD);
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor, List<IChromatogram> chromatograms) {

		List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos = getChromatogramExportConverterProcessInfo();
		ISupplier supplier = getSupplier();
		if(chromatograms == null) {
			throw new NullPointerException();
		}
		for(IChromatogram chromatogram : chromatograms) {
			if(chromatogram instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				File file = chromatogramWSD.getFile();
				if(file != null) {
					getNames().clear();
					File nfile = setFile(file, supplier.getFileExtension());
					IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterWSD.convert(nfile, chromatogramWSD, supplier.getId(), progressMonitor);
					chromatogramExportConverterProcessingInfos.add(procesInfo);
				}
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
