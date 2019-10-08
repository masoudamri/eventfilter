package com.ors.interview.mariner.access.service.impl.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;

import com.google.code.externalsorting.ExternalSort;
import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordSortService;
import com.ors.interview.mariner.access.service.impl.csv.CsvAccessRecordAdapter;

public class ExternalRecordSortService implements RecordSortService {

	CsvAccessRecordAdapter csvAccessRecordAdapter;

	public ExternalRecordSortService(CsvAccessRecordAdapter csvAccessRecordAdapter) {
		this.csvAccessRecordAdapter = csvAccessRecordAdapter;
	}

	@Override
	public Stream<AccessRecord> sortRecords(Stream<AccessRecord> accessRecords, Comparator<AccessRecord> comparator) {
		try {
			File input = File.createTempFile("buffer", "csv");
			File output = File.createTempFile("output", "csv");
			csvAccessRecordAdapter.writeRecords(accessRecords, new FileOutputStream(input),false);
			ExternalSort.sort(input, output, csvComparator(comparator));
			return csvAccessRecordAdapter.toRecords(output.toPath(),false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	Comparator<String> csvComparator(Comparator<AccessRecord> arcmp) {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return arcmp.compare(csvAccessRecordAdapter.parseLine(o1), csvAccessRecordAdapter.parseLine(o2));
			}
		};
	}

}
