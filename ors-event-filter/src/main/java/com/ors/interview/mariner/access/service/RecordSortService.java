package com.ors.interview.mariner.access.service;

import java.util.Comparator;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;

public interface RecordSortService {
	
	
	public Stream<AccessRecord> sortRecords(Stream<AccessRecord> accessRecords, Comparator<AccessRecord> comparator);

}
