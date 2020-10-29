package com.capgemini.employeePayrollServiceJDBC;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayroll {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;

	public EmployeePayroll() {

	}

	public EmployeePayroll(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
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
		List<EmployeePayrollData> employePayrollDataList=new ArrayList<EmployeePayrollData>();
		if(io.equals(IOService.FILE_IO))
			employePayrollDataList=new EmployeePayrollFileIOService().readData();
		if(io.equals(IOService.DB_IO))
			employePayrollDataList=new EmployeePayrollDBService().readData();
		return employePayrollDataList;
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

}
