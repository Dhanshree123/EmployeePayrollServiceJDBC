package com.capgemini.employeePayrollServiceJDBC;

public class EmployeePayrollData {
	public int empId;
	public String name;
	public double salary;

	public EmployeePayrollData(int empId, String name, double salary) {
		this.empId = empId;
		this.name = name;
		this.salary = salary;
	}

	public String toString() {
		return "EmployeeId =" + empId + " ,name=" + name + " ,salary=" + salary;
	}

}
