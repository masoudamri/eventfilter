package com.ors.interview.mariner.access.service.impl.mem;

import java.util.Comparator;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordSortService;

public class InMemeoryRecordSortService implements RecordSortService{

	@Override
	public Stream<AccessRecord> sortRecords(Stream<AccessRecord> accessRecords, Comparator<AccessRecord> comparator) {
		return accessRecords.sorted(comparator);
	}

}
