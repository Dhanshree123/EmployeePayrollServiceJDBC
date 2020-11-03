package com.capgemini.employeePayrollServiceJDBC;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int empId;
	public String name;
	public double basic_pay;
	public char gender;
	public LocalDate start;
	public int companyId;
	public String companyName;
	public String departmentName;

	public EmployeePayrollData(int empId, String name, double basic_pay) {
		this.empId = empId;
		this.name = name;
		this.basic_pay = basic_pay;
	}

	public EmployeePayrollData(int id, String name, double basic_pay, LocalDate start, char gender) {
		this(id, name, basic_pay);
		this.start = start;
		this.gender = gender;
	}

	public EmployeePayrollData(int id, String name, LocalDate start, double basic_pay, char gender, int companyId,
			String departmentName, String companyName) {
		this(id, name, basic_pay);
		this.start = start;
		this.gender = gender;
		this.companyId = companyId;
		this.departmentName = departmentName;
		this.companyName = companyName;
	}

	public String toString() {
		return "EmployeeId =" + empId + " ,name=" + name + " ,basic_pay=" + basic_pay + " ,start=" + start;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		EmployeePayrollData that = (EmployeePayrollData) o;

		return empId == that.empId && companyId == that.companyId && Double.compare(that.basic_pay, basic_pay) == 0
				&& name.equals(that.name) && that.gender == gender && companyName.equals(that.companyName)
				&& departmentName.equals(that.departmentName) && start.compareTo(that.start) == 0;

	}

}
