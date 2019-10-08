package com.ors.interview.mariner.access.service.impl.xml;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.XMLStreamException;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordIngestionService;

public class XmlAcessRecordIngestionService implements RecordIngestionService{

	String[] EXTENTIONS={"xml"};
	public  Stream<AccessRecord> toRecords(Path path) {
		try {			
			XmlAccessRecordIterator iterator;
			iterator = new XmlAccessRecordIterator(path);
			Spliterator<AccessRecord> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
			return StreamSupport.stream(spliterator, false);
		} catch (FileNotFoundException | XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] extentionsHandled() {
		return EXTENTIONS;

	}
}
