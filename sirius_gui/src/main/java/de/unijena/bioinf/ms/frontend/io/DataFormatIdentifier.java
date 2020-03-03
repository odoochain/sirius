package de.unijena.bioinf.ms.frontend.io;

import de.unijena.bioinf.ChemistryBase.utils.FileUtils;
import de.unijena.bioinf.ms.gui.io.spectrum.csv.CSVFormatReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataFormatIdentifier {

	private CSVFormatReader csv;
	
	public DataFormatIdentifier() {
		csv = new CSVFormatReader();
	}
	
	public DataFormat identifyFormat(File f){
		if(f.getName().toLowerCase().endsWith(".ms")) return DataFormat.JenaMS;
		else if(csv.isCompatible(f)) return DataFormat.CSV;
		else if(f.getName().toLowerCase().endsWith(".mgf")) return DataFormat.MGF;
		else if (f.getName().toLowerCase().endsWith(".txt")) return DataFormat.CSV;
		else return DataFormat.NotSupported;
	}

}

class MGFCompatibilityValidator{
	public boolean isCompatible(File f){
		try(BufferedReader reader = FileUtils.ensureBuffering(new FileReader(f))){
			String temp = null;
			while((temp = reader.readLine()) != null){
				temp = temp.trim();
				if(temp.isEmpty()) continue;
                return temp.toUpperCase().equals("BEGIN IONS");
			}
		}catch(IOException e){
			return false;
		}
		return false;
	}
}