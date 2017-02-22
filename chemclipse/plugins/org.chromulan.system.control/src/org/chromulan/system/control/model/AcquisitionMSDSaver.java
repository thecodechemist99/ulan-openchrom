package org.chromulan.system.control.model;

import java.io.File;
import java.util.List;

import org.chromulan.system.control.report.ProccessMiscellaneousDataChromatogram;
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
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor, List<SaveChromatogram> chromatograms) {

		List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos = getChromatogramExportConverterProcessInfo();
		File file = getFile();
		ISupplier supplier = getSupplier();
		IAcquisition acquisition = getAcquisition();
		if(acquisition == null || chromatograms == null || file == null || supplier == null) {
			throw new NullPointerException();
		}
		namesRemove();
		chromatogramExportConverterProcessingInfos.clear();
		for(SaveChromatogram saveChromatogram : chromatograms) {
			ProccessMiscellaneousDataChromatogram.setChromatogramParameters(saveChromatogram, acquisition);
			IChromatogram chromatogram = saveChromatogram.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				File nfile = setFile(saveChromatogram.getName(), supplier.getFileExtension());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterMSD.convert(nfile, chromatogramMSD, supplier.getId(), progressMonitor);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
