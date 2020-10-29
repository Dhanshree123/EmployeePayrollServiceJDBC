package com.capgemini.employeePayrollServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	
	private PreparedStatement employeePayrollDataStatement;

	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM employee_payroll";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double basic_pay = resultSet.getDouble("basic_pay");
				char gender = resultSet.getString("gender").charAt(0);
				employeePayrollList.add(new EmployeePayrollData(id, name, basic_pay, gender));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;

	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Dhpatil@23";
		Connection con;
		System.out.println("Connecting to database:" + jdbcURL);
		con = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful!!" + con);
		return con;
	}

	public int updateEmployeeData(String name, double basic_pay) throws EmployeePayrollException {
		return this.updateEmployeeDataUsingStatement(name, basic_pay);
	}

	private int updateEmployeeDataUsingStatement(String name, double basic_pay) throws EmployeePayrollException {
		String sql = String.format("update employee_payroll set basic_pay=%.02f where name='%s';", basic_pay, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to connect to database");
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList=null;
		if(this.employeePayrollDataStatement==null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet=employeePayrollDataStatement.executeQuery();
			employeePayrollDataList=this.getEmployeePayrollData(resultSet);
		}
		catch(SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return employeePayrollDataList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList=new ArrayList<>();
		try {
			while(resultSet.next()) {
				int id=resultSet.getInt("id");
				String name=resultSet.getString("name");
				double basic_pay=resultSet.getDouble("basic_pay");
				char gender=resultSet.getString("gender").charAt(0);
				employeePayrollDataList.add(new EmployeePayrollData(id,name,basic_pay,gender));
			}
		}
		catch(SQLException e) {
			throw new EmployeePayrollException("unable to read data from database");
		}
		return employeePayrollDataList;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException{
		try {
			Connection connection=this.getConnection();
			String sql="SELECT * FROM employee_payroll WHERE name=?";
			employeePayrollDataStatement=connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			throw new EmployeePayrollException("unable to prepare statement");
		}
	}
}
