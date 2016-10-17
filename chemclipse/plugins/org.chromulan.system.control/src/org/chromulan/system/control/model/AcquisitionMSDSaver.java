package org.chromulan.system.control.model;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AcquisitionMSDSaver extends AbstractAcquisitionSaver implements IAcquisitionMSDSaver {

	public AcquisitionMSDSaver(IAcquisitionMSD acquisitionMSD) {
		super(acquisitionMSD);
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor, List<IChromatogram> chromatograms) {

		List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos = getChromatogramExportConverterProcessInfo();
		ISupplier supplier = getSupplier();
		if(chromatograms == null) {
			throw new NullPointerException();
		}
		for(IChromatogram chromatogram : chromatograms) {
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				File file = chromatogramMSD.getFile();
				if(file != null) {
					getNames().clear();
					File nfile = setFile(file, supplier.getFileExtension());
					IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterMSD.convert(nfile, chromatogramMSD, supplier.getId(), progressMonitor);
					chromatogramExportConverterProcessingInfos.add(procesInfo);
				}
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
