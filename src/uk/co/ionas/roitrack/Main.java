package uk.co.ionas.roitrack;

import uk.co.ionas.roitrack.extractor.ROIExtractor;
import uk.co.ionas.roitrack.properties.ROITrackProperties;
import uk.co.ionas.roitrack.properties.ROITrackPropertiesException;

public class Main {

	public static void main(String[] args) {
		String path = "./roitrack.properties";
		if (args.length == 1) path = args[0];
		try {
			ROITrackProperties props = new ROITrackProperties(path);
			ROIExtractor extractor = new ROIExtractor(props);
			extractor.extract();
		} catch (ROITrackPropertiesException e) {
			System.err.println(e.getMessage());
		}
	}
	
}
