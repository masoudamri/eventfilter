package com.ors.interview.mariner.access.service.impl.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordIngestionService;

public class JsonAccessRecordIngestionService implements RecordIngestionService {

	private static final String[] EXTENTIONS = { "json" };

	@Override
	public Stream<AccessRecord> toRecords(Path path) {
		try {
			JsonParser parser;
			parser = Json.createParser(new FileInputStream(path.toFile()));
			while (parser.hasNext()) {
				if (parser.next() == JsonParser.Event.START_ARRAY) {
					break;
				}
			}
			return parser.getArrayStream().map(JsonValue::asJsonObject)
					.map(JsonAccessRecordIngestionService::parseJson);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	static AccessRecord parseJson(JsonObject json) {
		return new AccessRecord() {

			@Override
			public String getServiceGuid() {
				return json.getString(AccessRecord.Field.SERVICE_GUID.toString());
			}

			@Override
			public Instant getRequestTime() {
				return Instant.ofEpochMilli(json.getJsonNumber(AccessRecord.Field.REQUEST_TIME.toString()).longValue());
			}

			@Override
			public int getPacketsServiced() {
				return json.getInt(AccessRecord.Field.PACKETS_SERVICED.toString());
			}

			@Override
			public int getPacketsRequested() {
				return json.getInt(AccessRecord.Field.PACKETS_REQUESTED.toString());
			}

			@Override
			public int getMaxHoleSize() {
				return json.getInt(AccessRecord.Field.PACKETS_REQUESTED.toString());
			}

			@Override
			public int getRetriesRequest() {
				return json.getInt(AccessRecord.Field.RETRIES_REQUEST.toString());
			}

			@Override
			public String getClientGuid() {
				return json.getString(AccessRecord.Field.CLIENT_GUID.toString());
			}

			@Override
			public String getClientAddress() {
				return json.getString(AccessRecord.Field.CLIENT_ADDRESS.toString());
			}
		};
	}

	@Override
	public String[] extentionsHandled() {
		return EXTENTIONS;

	}
}
