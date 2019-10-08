package com.ors.interview.mariner.access.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;

public interface RecordOutputService {
	
	public void writeRecords(Stream<AccessRecord> records,OutputStream stream) throws IOException;

}
