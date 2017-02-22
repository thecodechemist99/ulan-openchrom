package org.chromulan.system.control.model;

import java.io.File;
import java.util.List;

import org.chromulan.system.control.report.ProccessMiscellaneousDataChromatogram;
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
			if(chromatogram instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				File nfile = setFile(saveChromatogram.getName(), supplier.getFileExtension());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterWSD.convert(nfile, chromatogramWSD, supplier.getId(), progressMonitor);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
