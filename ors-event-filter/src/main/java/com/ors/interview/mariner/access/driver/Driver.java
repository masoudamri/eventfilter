package com.ors.interview.mariner.access.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordIngestionService;
import com.ors.interview.mariner.access.service.RecordOutputService;
import com.ors.interview.mariner.access.service.RecordSortService;

public class Driver {
	static final PathMatcher EXTENTION_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/*.{csv,json,xml}");
	Map<String, RecordIngestionService> parsers;

	RecordOutputService recordOutputService;

	RecordSortService recordSortService;

	Driver(RecordSortService recordSortService, RecordOutputService recordOutputService,
			RecordIngestionService... recordIngestionServices) {
		parsers = new HashMap<>();
		for (RecordIngestionService service : recordIngestionServices) {
			for (String ext : service.extentionsHandled()) {
				parsers.put(ext.toLowerCase(), service);
			}
		}
		this.recordSortService = recordSortService;
		this.recordOutputService = recordOutputService;
	}

	public Stream<AccessRecord> processRecords(String[] inputFiles) {
		Stream<AccessRecord> accessRecords = Stream.of(inputFiles).map(String::trim).map(File::new).map(File::toPath)
				.filter(EXTENTION_MATCHER::matches)
				.flatMap(p -> parsers.get(getExtention(p.getFileName().toString().toLowerCase())).toRecords(p))
				.filter(ar -> ar.getPacketsServiced() != 0);
		return recordSortService.sortRecords(accessRecords, new AccessRecordComparator());
	}

	public Stream<AccessRecord> complieSummary(Stream<AccessRecord> records, ConcurrentMap<String, LongAdder> summary) {
		return records.peek(ar -> summary.compute(ar.getServiceGuid(), (s, l) -> {
			if (l == null) {
				l = new LongAdder();
			}
			l.increment();
			return l;
		}));
	}

	public void writeResult(Stream<AccessRecord> sortedAccessRecords, String outputFile) {
		try {
			OutputStream outputStream = new FileOutputStream(Paths.get(outputFile).toFile());
			recordOutputService.writeRecords(sortedAccessRecords, outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void printSummary(ConcurrentMap<String, LongAdder> summary, OutputStream out) {
		summary.forEach((s, l) -> {
			try {
				out.write(String.format("%s:%s\n", s, l.sum()).getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static String getExtention(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}
}
