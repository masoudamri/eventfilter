package com.ors.interview.mariner.access.driver;

import java.util.Comparator;

import com.ors.interview.mariner.access.model.AccessRecord;

public class AccessRecordComparator implements Comparator<AccessRecord>{

	@Override
	public int compare(AccessRecord o1, AccessRecord o2) {
		return o1.getRequestTime().compareTo(o2.getRequestTime());
	}
	
	

}
