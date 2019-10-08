package com.ors.interview.mariner.access.service.impl.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.model.AccessRecord.Field;
import com.ors.interview.mariner.access.service.RecordIngestionService;

public class XmlAccessRecordIterator implements Iterator<AccessRecord>, AutoCloseable {
	XMLStreamReader xmlStreamReader;
	private static final String ACCESS_RECORD_ELEMENT_NAME = "report";

	public XmlAccessRecordIterator(Path path) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(path.toFile()));
	}

	@Override
	public boolean hasNext() {
		return xmlStreamReader.getEventType() != XMLStreamReader.END_DOCUMENT;
	}

	@Override
	public AccessRecord next() {
		if (!hasNext()) {
			return null;
		}
		try {
			AccessRecord record;
			if (!atRecordStart(xmlStreamReader)) {
				advanceToRecordStartOrDocumentEnd(xmlStreamReader);
			}
			record = parseAccessRecord(xmlStreamReader);
			advanceToRecordStartOrDocumentEnd(xmlStreamReader);
			return record;
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	void advanceToRecordStartOrDocumentEnd(XMLStreamReader xmlStreamReader) throws XMLStreamException {
		while (xmlStreamReader.hasNext() && !atRecordStart(xmlStreamReader)) {
			xmlStreamReader.next();
		}
	}

	boolean atRecordStart(XMLStreamReader xmlStreamReader) {
		return atElement(xmlStreamReader, ACCESS_RECORD_ELEMENT_NAME, XMLStreamReader.START_ELEMENT);
	}

	boolean atRecordEnd(XMLStreamReader xmlStreamReader) {
		return atElement(xmlStreamReader, ACCESS_RECORD_ELEMENT_NAME, XMLStreamReader.END_ELEMENT);
	}

	boolean atStartElement(XMLStreamReader xmlStreamReader, String name) {
		return atElement(xmlStreamReader, name, XMLStreamReader.START_ELEMENT);
	}

	boolean atElement(XMLStreamReader xmlStreamReader, String name, int type) {
		return xmlStreamReader.getEventType() == type && (type == XMLStreamReader.START_DOCUMENT
				|| type == XMLStreamReader.END_DOCUMENT || Objects.equals(name, xmlStreamReader.getLocalName()));

	}

	AccessRecord parseAccessRecord(XMLStreamReader xmlStreamReader) throws XMLStreamException {
		XmlAccessRecord accessRecord = new XmlAccessRecord();
		for (; xmlStreamReader.hasNext() && !atRecordEnd(xmlStreamReader); xmlStreamReader.next()) {
			if (xmlStreamReader.isStartElement()) {
				if (atStartElement(xmlStreamReader, Field.CLIENT_ADDRESS.toString())) {
					accessRecord.setClientAddress(xmlStreamReader.getElementText());
				} if (atStartElement(xmlStreamReader, Field.CLIENT_GUID.toString())) {
					accessRecord.setClientGuid(xmlStreamReader.getElementText());
				} else if (atStartElement(xmlStreamReader, Field.MAKX_HOLE.toString())) {
					accessRecord.setMaxHoleSize(Integer.parseInt(xmlStreamReader.getElementText()));
				} else if (atStartElement(xmlStreamReader, Field.PACKETS_REQUESTED.toString())) {
					accessRecord.setPacketsRequested(Integer.parseInt(xmlStreamReader.getElementText()));
				} else if (atStartElement(xmlStreamReader, Field.PACKETS_SERVICED.toString())) {
					accessRecord.setPacketsServiced(Integer.parseInt(xmlStreamReader.getElementText()));
				} else if (atStartElement(xmlStreamReader, Field.REQUEST_TIME.toString())) {
					accessRecord.setRequestTime(Instant.from(
							RecordIngestionService.DATE_TIME_FORMAT_PARSER.parse(xmlStreamReader.getElementText())));
				} else if (atStartElement(xmlStreamReader, Field.RETRIES_REQUEST.toString())) {
					accessRecord.setRetriesRequest(Integer.parseInt(xmlStreamReader.getElementText()));
				} else if (atStartElement(xmlStreamReader, Field.SERVICE_GUID.toString())) {
					accessRecord.setServiceGuid(xmlStreamReader.getElementText());
				}
			}
		}
		return accessRecord;
	}

	@Override
	public void close() throws Exception {
		xmlStreamReader.close();
	}
}
