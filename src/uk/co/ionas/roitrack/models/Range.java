package uk.co.ionas.roitrack.models;

import uk.co.ionas.roitrack.properties.ROITrackPropertiesException;

public class Range {

	private final int start;
	private final int end;
	
	public Range(int start, int end) {
		if (start > end) throw new ROITrackPropertiesException(start + " > " + end + " ?!");
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
	
}
