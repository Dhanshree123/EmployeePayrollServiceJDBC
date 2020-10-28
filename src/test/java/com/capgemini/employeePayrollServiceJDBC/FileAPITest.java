package com.capgemini.employeePayrollServiceJDBC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class FileAPITest {

	private static String HOME = System.getProperty("user.home");
	private static String DIRECTORY = "Demo";

	@Test
	public void givenPathWhenCheckedThenConfirm() throws IOException {

		// Check File Exists
		Path homePath = Paths.get(HOME);
		Assert.assertTrue(Files.exists(homePath));

		// Delete File and Check File Not Exists
		Path pathLoc = Paths.get(HOME + "/" + DIRECTORY);
		if (Files.exists(pathLoc))
			FileUtils.deleteFiles(pathLoc.toFile());

		Assert.assertTrue(Files.notExists(pathLoc));

		// Create Directory
		Files.createDirectory(pathLoc);
		Assert.assertTrue(Files.exists(pathLoc));

		// Create File
		IntStream.range(1, 10).forEach(cntr -> {
			Path tempFile = Paths.get(pathLoc + "/temp" + cntr);
			Assert.assertTrue(Files.notExists(tempFile));
			try {
				Files.createFile(tempFile);
			} catch (IOException e) {
			}
			Assert.assertTrue(Files.exists(tempFile));
		});

		// List Files, Directories as well as Files with Extension
		Files.list(pathLoc).filter(Files::isRegularFile).forEach(System.out::println);
		Files.newDirectoryStream(pathLoc).forEach(System.out::println);
		Files.newDirectoryStream(pathLoc, path -> path.toFile().isFile() && path.toString().startsWith("temp"))
				.forEach(System.out::println);

	}

	@Test
	public void givenADirectoryWhenWatchedListAllTheActivities() throws IOException {
		Path pathLoc = Paths.get(HOME + "/" + DIRECTORY);
		Files.list(pathLoc).filter(Files::isRegularFile).forEach(System.out::println);
		new WatchServiceExample(pathLoc).processEvents();
	}
}
