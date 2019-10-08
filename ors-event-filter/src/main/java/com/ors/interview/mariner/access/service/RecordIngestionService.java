package com.ors.interview.mariner.access.service;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;

public interface RecordIngestionService {
	
	static final DateTimeFormatter DATE_TIME_FORMAT_PARSER=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz");
	
	Stream<AccessRecord> toRecords(Path path);
	String[] extentionsHandled();
}
