package uk.co.ionas.roitrack.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import uk.co.ionas.roitrack.models.Range;
import uk.co.ionas.roitrack.properties.ROITrackProperties;
import uk.co.ionas.roitrack.properties.ROITrackPropertiesException;

public class ROIExtractor {

	private final ROITrackProperties props;
	
	public ROIExtractor(ROITrackProperties props) {
		this.props = props;
	}
	
	public void extract() {
		advise();
		String outputPath = this.props.getOutput();
		emptyFile(outputPath);
		for (String path : this.props.getPaths()) {
			read(path, outputPath);
		}
		removeTrailingCommas();
	}
	
	private void read(String inputPath, String outputPath) {
		FileReader fr = null;
		FileWriter fw = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			try {
				fr = new FileReader(inputPath);
				br = new BufferedReader(fr);
				
				fw = new FileWriter(outputPath, true);
				bw = new BufferedWriter(fw);
				
				int count = 1;
				String line = null;
				while ((line = br.readLine()) != null) {
					for (int i = 0; i< this.props.getRanges().size(); i++) {
						Range range = this.props.getRanges().get(i);
						if (count >= range.getStart() && count <= range.getEnd()) {
							bw.write(line + ", ");
						}
					}
					count++;
				}
				bw.write('\n');
				bw.flush();
				validate(inputPath, count);
			} finally {
				if (fr != null) fr.close();
				if (br != null) br.close();
				if (fw != null) fw.close();
				if (bw != null) bw.close();
			}
		} catch (FileNotFoundException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		} catch (IOException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		}
	}
	
	private void validate(String filePath, int lines) {
		for (Range range : this.props.getRanges()) {
			if (lines < range.getStart() | lines < range.getEnd()) {
				throw new ROITrackPropertiesException(filePath + " contains " + lines 
						+ " lines while you have declared a range of " + range.getStart() + " and " + range.getEnd());
			}
		}
	}
	
	private void advise() {
		int pSize = this.props.getPaths().size();
		String pSizep = pSize > 1 ? "s" : "";
		int rSize = this.props.getRanges().size();
		String rSizep = rSize > 1 ? "s" : "";
		String msg = "%d path%s and %d range%s read in. Writing to %s";
		System.out.println(String.format(msg, pSize, pSizep, rSize, rSizep, this.props.getOutput()));
		for (int i = 0; i<this.props.getPaths().size(); i++) {
			String path = this.props.getPaths().get(i);
			System.out.println(path);
		}
	}
	
	
	public void removeTrailingCommas() {
		try {
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(this.props.getOutput(), "rw");
				int c = 0;
				while (( c = raf.read()) != -1) {
					if (c == '\n') {
						raf.seek(raf.getFilePointer() - 3);
						raf.write("  \n".getBytes());
						raf.seek(raf.getFilePointer() + 3);
					}
				}
			} finally {
				if (raf != null) raf.close();
			}
		} catch (FileNotFoundException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		} catch (IOException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		}
	}
	
	public void emptyFile(String filePath) {
		try {
	        FileWriter fw = null;
	        PrintWriter pw = null;
			try {
				fw = new FileWriter(filePath, false);	
				pw = new PrintWriter(fw, false);
		        pw.flush();
		        pw.close();
		        fw.close();
			} finally {
				if (fw != null) fw.close();
				if (pw != null) pw.close();
			}
		} catch (IOException e) {
			throw new ROITrackPropertiesException("Could not empty " + filePath + ": " + e.getMessage());
		} 
    }
	
}
