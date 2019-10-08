package com.ors.interview.mariner.access.driver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.ors.interview.mariner.access.model.AccessRecord;
import com.ors.interview.mariner.access.service.impl.csv.CsvAccessRecordAdapter;

class MainTest {

	@Test
	void csvTest() {
		Main main = new Main();
		main.outputFile = "output.csv";
		URL res = getClass().getClassLoader().getResource("reports.csv");
		String[] input = { res.getPath().toString() };
		main.inputFiles = input;
		main.call();
		checkFile("output.csv");
	}

	@Test
	void jsonTest() {
		Main main = new Main();
		main.outputFile = "output.csv";
		URL res = getClass().getClassLoader().getResource("reports.json");
		String[] input = { res.getPath().toString() };
		main.inputFiles = input;
		main.call();
		checkFile("output.csv");
	}

	@Test
	void xmlTest() {
		Main main = new Main();
		main.outputFile = "output.csv";
		URL res = getClass().getClassLoader().getResource("reports.xml");
		String[] input = { res.getPath().toString() };
		main.inputFiles = input;
		main.call();
		checkFile("output.csv");
	}

	@Test
	void allTest() throws URISyntaxException {
		Main main = new Main();
		main.outputFile = "output.csv";
		String[] input = { getClass().getClassLoader().getResource("reports.xml").getPath().toString(),
				getClass().getClassLoader().getResource("reports.csv").getPath().toString(),
				getClass().getClassLoader().getResource("reports.json").getPath().toString() };
		main.inputFiles = input;
		main.call();
		checkFile("output.csv");
	}

	@Test
	void externalTest() throws URISyntaxException, IOException {
		Main main = new Main();
		main.outputFile = "output.csv";
		String[] input = { getClass().getClassLoader().getResource("reports.xml").getPath().toString(),
				getClass().getClassLoader().getResource("reports.csv").getPath().toString(),
				getClass().getClassLoader().getResource("reports.json").getPath().toString() };
		main.inputFiles = input;
		main.call();

		Main main2 = new Main();
		main2.outputFile = "output2.csv";
		main2.inputFiles = input;
		main2.useExternalSort = true;
		main2.call();
		@SuppressWarnings("deprecation")
		HashCode hc1 = Files.asByteSource(new File("output.csv")).hash(Hashing.sha1());
		@SuppressWarnings("deprecation")
		HashCode hc2 = Files.asByteSource(new File("output.csv")).hash(Hashing.sha1());
		assertTrue(Arrays.equals(hc2.asBytes(), hc1.asBytes()), "two sorts give different files!");
	}

	void checkFile(String fileName) {
		CsvAccessRecordAdapter adapter = new CsvAccessRecordAdapter();
		Stream<AccessRecord> records = adapter.toRecords(new File(fileName).toPath());
		AccessRecord[] recordArray = records.toArray(AccessRecord[]::new);
		for (int i = 0; i < recordArray.length - 1; i++) {
			assertFalse(recordArray[i].getRequestTime().isAfter(recordArray[i + 1].getRequestTime()),
					"file is not properly sorted!");
			assertNotEquals(0, recordArray[i].getPacketsServiced(), "invalid record in file!");
		}
	}
}
