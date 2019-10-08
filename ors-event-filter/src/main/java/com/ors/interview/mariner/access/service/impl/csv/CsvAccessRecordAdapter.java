package com.ors.interview.mariner.access.service.impl.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordIngestionService;
import com.ors.interview.mariner.access.service.RecordOutputService;

public class CsvAccessRecordAdapter implements RecordIngestionService,
			RecordOutputService{

	
	private final DateTimeFormatter DATE_TIME_FORMATTER=DATE_TIME_FORMAT_PARSER
			.withZone(ZoneId.of("ADT"));

	private static final String[] EXTENTIONS = {"csv"};

	
	public Stream<AccessRecord> toRecords(Path path,boolean hasHeaders) {
		try {
			int skip=hasHeaders?1:0;
			return Files.lines(path).skip(skip).map(this::parseLine);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	@Override
	public Stream<AccessRecord> toRecords(Path path) {
		return toRecords(path,true);
	}

	public void writeRecords(Stream<AccessRecord> records, OutputStream outputStream, boolean writeHeaders) throws IOException {
		if(writeHeaders) {
			CsvAccessRecordAdapter.writeCsvHeaders(outputStream);
		}
		writeAccessRecords(outputStream, records);
	}
	
	@Override
	public void writeRecords(Stream<AccessRecord> records, OutputStream outputStream) throws IOException {
		writeRecords( records,outputStream,true);
	}
	
	public void writeAccessRecords(OutputStream stream, Stream<AccessRecord> records) throws IOException {
		records.forEach(record -> {
			try {
				stream.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", record.getClientAddress(), record.getClientGuid(),
						DATE_TIME_FORMATTER.format(record.getRequestTime()),record.getServiceGuid(), record.getRetriesRequest(),
						record.getPacketsRequested(), record.getPacketsServiced(),record.getMaxHoleSize()).getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	static public void writeCsvHeaders(OutputStream outputStream) throws IOException {
		outputStream.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",(Object[])AccessRecord.Field.values()).getBytes());
	}

	public AccessRecord parseLine(String line) {
		String[] fields = line.split(",");
		return new AccessRecord() {

			@Override
			public int getMaxHoleSize() {
				return Integer.parseInt(fields[7]);
			}

			@Override
			public int getPacketsServiced() {
				return Integer.parseInt(fields[6]);
			}


			@Override
			public int getPacketsRequested() {
				return Integer.parseInt(fields[5]);
			}

			@Override
			public int getRetriesRequest() {
				return Integer.parseInt(fields[4]);
			}
						
			@Override
			public String getServiceGuid() {
				return fields[3].trim();
			}

			@Override
			public Instant getRequestTime() {
				return Instant.from(DATE_TIME_FORMAT_PARSER.parse(fields[2]));
			}

			@Override
			public String getClientGuid() {
				return fields[1].trim();
			}

			@Override
			public String getClientAddress() {
				return fields[0].trim();
			}
		};
	}

	@Override
	public String[] extentionsHandled() {
		return EXTENTIONS;
	}

}
