package uk.co.ionas.roitrack.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import uk.co.ionas.roitrack.models.Range;
import uk.co.ionas.roitrack.util.Strings;

public class ROITrackProperties extends Properties {
	
	private final List<String> paths;
	private final List<Range> ranges;
	private final String output;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2679151668290952075L;

	public ROITrackProperties(String path) {
		FileReader fr = null;
		BufferedReader bin = null;
		try {
			try {
				fr = new FileReader(path);
				bin = new BufferedReader(fr);
				this.load(bin);
				String filePaths = (String) this.get("filePath");
				if (!Strings.isNotNullOrEmpty(filePaths)) {
					throw new ROITrackPropertiesException("Properties file must define 'filePath' property!");
				}
				this.paths = setPaths(filePaths);

				String fileRanges = (String) this.get("rangeFile");
				if (!Strings.isNotNullOrEmpty(fileRanges)) {
					throw new ROITrackPropertiesException("Properties file must define 'rangeFile' property!");
				}
				this.ranges = setRanges(fileRanges);
				
				this.output = this.getProperty("output", "./roitrack-results.txt");
			} finally {
				if (fr != null) fr.close();
				if (bin != null) bin.close();
			}
		} catch (FileNotFoundException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		} catch (IOException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		}
	}
	
	
	private List<String> setPaths(String filePath) throws IOException {
		List<String> paths = new ArrayList<String>();
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++)
	    	if (listOfFiles[i].isFile())
	    		paths.add(listOfFiles[i].getPath());
	    if (paths.size() == 0)
			throw new ROITrackPropertiesException("Have not found any files in " + filePath);
		return paths;
	}
	
	
	private List<Range> setRanges(String rangePath) throws IOException {
		FileReader fr = null;
		BufferedReader bin = null;
		StringBuilder sb = new StringBuilder();
		try {
			fr = new FileReader(rangePath);
			bin = new BufferedReader(fr);
			String line = null;
			while ((line = bin.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			if (bin != null) bin.close();
			if (fr != null) fr.close();
		}
		List<String> stringRanges =  Strings.toList(sb.toString());
		List<Range> ranges = new ArrayList<Range>();
		for (String stringRange : stringRanges) {
			String[] tokens = stringRange.split("-");
			Range range = null;
			if (tokens.length == 1) {
				String rangeStart = tokens[0].trim();
				range = addRange(rangeStart, rangeStart);
			} else if (tokens.length == 2) {
				String rangeStart = tokens[0].trim();
				String rangeEnd = tokens[1].trim();
				range = addRange(rangeStart, rangeEnd);
			} else {
				throw new ROITrackPropertiesException(stringRange + 
						" does not adhere to string range format. "
						+ "Ranges should be numbers separated by a dash ('-') e.g. 12-15");
			} 
			ranges.add(range);
		}
		return ranges;
	}
	
	private Range addRange(String rangeStart, String rangeEnd) {
		try {
			int start = Integer.parseInt(rangeStart);
			int end = Integer.parseInt(rangeEnd);
			return new Range(start, end);
		} catch (NumberFormatException e) {
			throw new ROITrackPropertiesException(e.getMessage());
		}
	}
	
	public List<String> getPaths() {
		return paths;
	}

	public List<Range> getRanges() {
		return ranges;
	}

	public String getOutput() {
		return output;
	}

}
