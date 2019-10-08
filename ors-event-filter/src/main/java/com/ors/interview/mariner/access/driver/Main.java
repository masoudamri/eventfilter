package com.ors.interview.mariner.access.driver;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.RecordSortService;
import com.ors.interview.mariner.access.service.impl.csv.CsvAccessRecordAdapter;
import com.ors.interview.mariner.access.service.impl.ext.ExternalRecordSortService;
import com.ors.interview.mariner.access.service.impl.json.JsonAccessRecordIngestionService;
import com.ors.interview.mariner.access.service.impl.mem.InMemeoryRecordSortService;
import com.ors.interview.mariner.access.service.impl.xml.XmlAcessRecordIngestionService;
import com.ors.interview.mariner.access.time.AdtTimeZoneContainer;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Main implements Callable<Integer> {

	static {
		AdtTimeZoneContainer.registerAdtTimeZoneRules();
	}
	@Option(required = true, names = { "-i", "--input" }, description = "files to process")
	String[] inputFiles;

	@Option(names = { "-o", "--output" }, description = "output file location", defaultValue = "output.csv")
	String outputFile;

	@Option(names = { "-e", "--externalSort" })
	boolean useExternalSort;

	public static void main(String... args) {
		new CommandLine(new Main()).execute(args);
	}

	@Override
	public Integer call() {
		CsvAccessRecordAdapter csvAccessRecordAdapter = new CsvAccessRecordAdapter();
		RecordSortService recordSortService = useExternalSort ? new ExternalRecordSortService(csvAccessRecordAdapter)
				: new InMemeoryRecordSortService();
		Driver driver = new Driver(recordSortService, csvAccessRecordAdapter, csvAccessRecordAdapter,
				new XmlAcessRecordIngestionService(), new JsonAccessRecordIngestionService());
		ConcurrentMap<String, LongAdder> summary = new ConcurrentHashMap<>();
		Stream<AccessRecord> records = driver.complieSummary(driver.processRecords(inputFiles), summary);
		driver.writeResult(records, outputFile);
		driver.printSummary(summary, System.out);
		return 0;
	}
}
