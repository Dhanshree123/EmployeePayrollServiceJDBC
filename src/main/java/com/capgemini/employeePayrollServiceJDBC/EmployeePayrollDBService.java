package com.capgemini.employeePayrollServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	public List<EmployeePayrollData> readData(){
		String sql="SELECT * FROM employee_payroll";
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		try (Connection connection =this.getConnection();){
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(sql);
			while(resultSet.next()) {
				int id=resultSet.getInt("id");
				String name=resultSet.getString("name");
				double basic_pay =resultSet.getDouble("basic_pay");
				LocalDate start=resultSet.getDate("start").toLocalDate();
				char gender=resultSet.getString("gender").charAt(0);
				employeePayrollList.add(new EmployeePayrollData(id,name,basic_pay,start,gender));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
		
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName="root";
		String password="Dhpatil@23";
		Connection con;
		System.out.println("Connecting to database:"+jdbcURL);
		con=DriverManager.getConnection(jdbcURL,userName,password);
		System.out.println("Connection is successful!!"+con);
		return con;
	}
}
