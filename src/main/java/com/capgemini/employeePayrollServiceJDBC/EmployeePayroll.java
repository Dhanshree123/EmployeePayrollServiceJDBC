package com.capgemini.employeePayrollServiceJDBC;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayroll {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayroll(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public EmployeePayroll() {
		employeePayrollDBService=EmployeePayrollDBService.getInstance();
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		EmployeePayroll employeePayroll = new EmployeePayroll(employeePayrollList);
		Scanner sc = new Scanner(System.in);
		employeePayroll.readFromConsole(sc);
		employeePayroll.writeToConsole();

	}

	private void readFromConsole(Scanner sc) {
		System.out.println("Enter the Employee id");
		int empId = sc.nextInt();
		System.out.println("Enter employee name");
		String name = sc.next();
		System.out.println("Enter employee salary");
		Double salary = sc.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(empId, name, salary));
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService io) {
		if (io.equals(IOService.FILE_IO))
			employeePayrollList = new EmployeePayrollFileIOService().readData();
		if (io.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readData();
		return employeePayrollList;
	}

	public void writeEmployeePayrollData(IOService io) {
		if (io.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);

		else if (io.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
	}

	private void writeToConsole() {
		System.out.println("Check the entered details " + employeePayrollList);

	}

	public long countEntries(IOService io) {
		if (io.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return 0;
	}

	public void printData(IOService io) {
		if (io.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();
	}

	public void updateEmployeeSalary(String name, double basic_pay) throws EmployeePayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, basic_pay);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.basic_pay = basic_pay;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

}
