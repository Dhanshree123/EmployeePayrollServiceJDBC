package com.capgemini.employeePayrollServiceJDBC;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class EmployeePayrollTest {

	@Before
	public void setup() {
		RestAssured.baseURI = "https://localhost";
		RestAssured.port = 3000;
	}

	@Test
	public void given3EmployeesWhenWrittenToFileShouldReturnEmployeeEntries() {
		EmployeePayrollData[] employeeArray = { new EmployeePayrollData(1, "Emma Stone", 50000),
				new EmployeePayrollData(2, "John Snow", 55000), new EmployeePayrollData(3, "Ryan Ice", 50000) };
		EmployeePayroll employeePayroll;
		employeePayroll = new EmployeePayroll(Arrays.asList(employeeArray));
		employeePayroll.writeEmployeePayrollData(EmployeePayroll.IOService.FILE_IO);
		employeePayroll.printData(EmployeePayroll.IOService.FILE_IO);
		long entries = employeePayroll.countEntries(EmployeePayroll.IOService.FILE_IO);
		System.out.println("Number of entries:- " + entries);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenFileReadingFromFileShouldMatchEmployeeCount() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		long entries = employeePayroll.readEmployeePayrollData(EmployeePayroll.IOService.FILE_IO).size();
		System.out.println("Number of entries:- " + entries);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenEmployeePayrollInDB_whenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		long entries = employeePayroll.readEmployeePayrollData(EmployeePayroll.IOService.DB_IO).size();
		System.out.println("Number of entries:- " + entries);
		Assert.assertEquals(4, entries);
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		long entries = employeePayroll.readEmployeePayrollData(EmployeePayroll.IOService.DB_IO).size();
		Assert.assertEquals(4, entries);
		employeePayroll.updateEmployeeSalary("Terisa", 300000.00);
		boolean result = employeePayroll.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);

	}

	@Test
	public void givenDateRange_WhenEmployeeDataRetrieved_ShouldMatch() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		List<EmployeePayrollData> employeePayrollDataList = employeePayroll.getEmployeeWithJoinedInDateRange();
		boolean result = employeePayrollDataList.get(0).name.equals("Bill")
				&& employeePayrollDataList.get(1).name.equals("Terisa")
				&& employeePayrollDataList.get(2).name.equals("Charlie");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeeDataRetrieved_ShouldMatchSum() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		double sum = employeePayroll.getEmployeeSalarySum();
		double max = employeePayroll.getEmployeeSalaryMax();
		double min = employeePayroll.getEmployeeSalaryMin();
		int numOfMale = employeePayroll.getNumOfMaleEmployee();
		int numOfFemale = employeePayroll.getNumOfFemaleEmployee();
		boolean result = (sum == 4000000.0) && (max == 3000000.0) && (min == 1000000.0) && (numOfFemale == 2)
				&& (numOfMale == 2);
		Assert.assertTrue(result);

	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayroll employeePayroll = new EmployeePayroll();
		employeePayroll.readEmployeePayrollData(EmployeePayroll.IOService.DB_IO);
		employeePayroll.addEmployeePayroll("Mini", 5000000.0, LocalDate.now(), "F", 101, "Finance", "Capgemini");
		boolean result = employeePayroll.checkEmployeePayrollInSyncWithDB("Mini");
		Assert.assertTrue(result);
	}

	@Test
	public void given6Employee_WhenAddedDatabase_ShouldMatchEntries() throws EmployeePayrollException {
		EmployeePayrollData[] arrayEmps = { new EmployeePayrollData(11, "Seema", 100000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(12, "hema", 200000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(13, "riya", 700000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(14, "nidhi", 900000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(15, "jayesh", 800000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(16, "chintu", 500000.0, LocalDate.now(), 'M'),

		};
		EmployeePayroll employeePayroll = new EmployeePayroll();
		// employeePayroll.readEmployeePayrollDataThread(EmployeePayroll.IOService.DB_IO);
		Instant start = Instant.now();
		employeePayroll.addEmployeeToPayrollWithThread(Arrays.asList(arrayEmps));
		Instant end = Instant.now();
		System.out.println("Duration with thread" + Duration.between(start, end));
		Assert.assertEquals(9, employeePayroll.countNumberOfEmployees(EmployeePayroll.IOService.DB_IO));
	}

	@Test
	public void givenEmployeeDataInJsonServer_whenRetreived_shouldMatchCount() {
		EmployeePayrollData[] arrayEmps = getEmployeeList();
		EmployeePayroll employeePayroll;
		employeePayroll = new EmployeePayroll(Arrays.asList(arrayEmps));
		long entries = employeePayroll.countEntries(EmployeePayroll.IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}

	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee");
		System.out.println("Employee Data at jsonserver:\n" + response.asString());
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmps;
	}
}
