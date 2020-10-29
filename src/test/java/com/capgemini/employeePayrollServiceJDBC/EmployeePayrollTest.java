package com.capgemini.employeePayrollServiceJDBC;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmployeePayrollTest {

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

}
