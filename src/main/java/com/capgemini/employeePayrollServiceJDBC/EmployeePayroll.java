package com.capgemini.employeePayrollServiceJDBC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayroll {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayroll(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = new ArrayList<>(employeePayrollList);
	}

	public EmployeePayroll() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
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

	public List<EmployeePayrollData> readEmployeePayrollData(IOService io) throws EmployeePayrollException {
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

	public List<EmployeePayrollData> getEmployeeWithJoinedInDateRange() throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollDBService
				.getEmployeePayrollDataWithStartDateInGivenRange();
		return employeePayrollData;
	}

	public double getEmployeeSalarySum() throws EmployeePayrollException {
		double sum = employeePayrollDBService.getSumOfSalary();
		return sum;
	}

	public double getEmployeeSalaryMax() throws EmployeePayrollException {
		double max = employeePayrollDBService.getMaxOfSalary();
		return max;
	}

	public double getEmployeeSalaryMin() throws EmployeePayrollException {
		double min = employeePayrollDBService.getMinOfSalary();
		return min;
	}

	public int getNumOfMaleEmployee() throws EmployeePayrollException {
		int numOfMale = employeePayrollDBService.getNumOfMale();
		return numOfMale;
	}

	public int getNumOfFemaleEmployee() throws EmployeePayrollException {
		int numOfFemale = employeePayrollDBService.getNumOfFemale();
		return numOfFemale;
	}

	public void addEmployeePayroll(String name, double basic_pay, LocalDate start, String gender, int companyId,
			String companyName, String departmentName) throws EmployeePayrollException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, basic_pay, start, gender, companyId,
				departmentName, companyName));
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, char gender)
			throws EmployeePayrollException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollUC7(name, salary, startDate, gender));
	}

	/*
	 * public void addEmployeeToPayroll(int id, String name, double salary,
	 * LocalDate startDate, char gender) throws EmployeePayrollException {
	 * employeePayrollList.add( employeePayrollDBService.addEmployeeToPayroll(id,
	 * name, salary, startDate, Character.toString(gender))); }
	 */
	public void addEmployeeToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDataList.forEach(employeePayrollData -> {
			System.out.println("Employee Being Added" + employeePayrollData.name);
			try {
				this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.basic_pay,
						employeePayrollData.start, employeePayrollData.gender);
			} catch (EmployeePayrollException e) {
				e.printStackTrace();
			}

			System.out.println("Employee Added" + employeePayrollData.name);

		});
		System.out.print(this.employeePayrollList);
	}

	public long countNumberOfEmployees(IOService io) {
		if (io.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return employeePayrollList.size();
	}

	public void addEmployeeToPayrollWithThread(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		employeePayrollDataList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				System.out.println("Employee Being Added: " + Thread.currentThread().getName());
				try {
					this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.basic_pay,
							employeePayrollData.start, employeePayrollData.gender);
				} catch (EmployeePayrollException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee Added" + Thread.currentThread().getName());

			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this.employeePayrollList);
	}

	public List<EmployeePayrollData> readEmployeePayrollDataThread(IOService io) throws EmployeePayrollException {
		if (io.equals(IOService.FILE_IO))
			employeePayrollList = new EmployeePayrollFileIOService().readData();
		if (io.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readDataFromEmployeePayroll();
		return employeePayrollList;
	}
}
