package com.capgemini.employeePayrollServiceJDBC;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int empId;
	public String name;
	public double basic_pay;
	public LocalDate start;
	public char gender;

	public EmployeePayrollData(int empId, String name, double basic_pay) {
		this.empId = empId;
		this.name = name;
		this.basic_pay = basic_pay;
	}

	public EmployeePayrollData(int id, String name, double basic_pay, LocalDate start, char gender) {
		this(id,name,basic_pay);
		this.start = start;
		this.gender = gender;
	}

	public String toString() {
		return "EmployeeId =" + empId + " ,name=" + name + " ,salary=" + basic_pay;
	}

}
