package com.ors.interview.mariner.access.model;

import java.time.Instant;

public interface AccessRecord {
	public enum Field {
		CLIENT_ADDRESS("client-address"), CLIENT_GUID("client-guid"), REQUEST_TIME("request-time"),
		SERVICE_GUID("service-guid"), RETRIES_REQUEST("retries-request"), PACKETS_REQUESTED("packets-requested"),
		PACKETS_SERVICED("packets-serviced"), MAKX_HOLE("max-hole-size");
	
		String name;
	
		Field(String name) {
			this.name = name;
		}
	
		@Override
		public String toString() {
			return name;
		}
	}
	public String getClientAddress();
	public String getClientGuid();
	public Instant getRequestTime();
	public String getServiceGuid();
	public int getRetriesRequest();
	public int getPacketsRequested();
	public int getPacketsServiced();
	public int getMaxHoleSize();
}
