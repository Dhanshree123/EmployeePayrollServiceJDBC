package com.capgemini.employeePayrollServiceJDBC;

public class EmployeePayrollData {
	public int empId;
	public String name;
	public double basic_pay;
	public char gender;

	public EmployeePayrollData(int empId, String name, double basic_pay) {
		this.empId = empId;
		this.name = name;
		this.basic_pay = basic_pay;
	}

	public EmployeePayrollData(int id, String name, double basic_pay, char gender) {
		this(id,name,basic_pay);
		this.gender = gender;
	}

	public String toString() {
		return "EmployeeId =" + empId + " ,name=" + name + " ,basic_pay=" + basic_pay;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null|| this.getClass()!=o.getClass()) return false;
		EmployeePayrollData that=(EmployeePayrollData)o;
		
		return empId==that.empId &&
				Double.compare(that.basic_pay, basic_pay)==0 &&
				name.equals(that.name) &&
				that.gender==gender ;
	}

}
