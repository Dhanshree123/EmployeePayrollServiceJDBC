package com.capgemini.employeePayrollServiceJDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService = null;

	private EmployeePayrollDBService() {

	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();

		return employeePayrollDBService;
	}

	public List<EmployeePayrollData> readData() throws EmployeePayrollException {
		String sql = "SELECT * FROM employee,payroll,department,company;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
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
		return this.updateEmployeeDataUsingPreparedStatement(name, basic_pay);
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

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = String.format("UPDATE employee_payroll SET basic_pay=%.2f WHERE name='%s' ;", salary, name);

			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			return preparedStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollException("unable to create prepared statement");
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollDataList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return employeePayrollDataList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				double basic_pay = resultSet.getDouble("basic_pay");
				char gender = resultSet.getString("gender").charAt(0);
				int companyId = resultSet.getInt("companyId");
				String departmentName = resultSet.getString("departmentName");
				String companyName = resultSet.getString("companyName");
				employeePayrollDataList.add(new EmployeePayrollData(id, name, start, basic_pay, gender, companyId,
						departmentName, companyName));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to read data from database");
		}
		return employeePayrollDataList;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to prepare statement");
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollDataWithStartDateInGivenRange() throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();

		try {
			Connection connection = this.getConnection();
			String sql = "select * from employee_payroll where start BETWEEN CAST('2018-01-01' AS DATE) and DATE(NOW());";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = getEmployeePayrollData(resultSet);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public double getSumOfSalary() throws EmployeePayrollException {
		double sum = 0;
		try {
			Connection connection = this.getConnection();
			String sql = "select sum(basic_pay) from employee_payroll where gender='M' group by gender";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.first();
			sum = resultSet.getDouble(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return sum;
	}

	public double getMaxOfSalary() throws EmployeePayrollException {
		double max = 0;
		try {
			Connection connection = this.getConnection();
			String sql = "select max(basic_pay) from employee_payroll where gender='M' group by gender";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.first();
			max = resultSet.getDouble(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return max;
	}

	public double getMinOfSalary() throws EmployeePayrollException {
		double min = 0;
		try {
			Connection connection = this.getConnection();
			String sql = "select min(basic_pay) from employee_payroll where gender='M' group by gender";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.first();
			min = resultSet.getDouble(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return min;
	}

	public int getNumOfMale() throws EmployeePayrollException {
		int numOfMale = 0;
		try {
			Connection connection = this.getConnection();
			String sql = "select count(id) from employee_payroll where gender='M' group by gender";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.first();
			numOfMale = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return numOfMale;
	}

	public int getNumOfFemale() throws EmployeePayrollException {
		int numOfFemale = 0;
		try {
			Connection connection = this.getConnection();
			String sql = "select count(id) from employee_payroll where gender='F' group by gender";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.first();
			numOfFemale = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to execute query");
		}
		return numOfFemale;
	}

	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double basic_pay, LocalDate startDate, char gender)
			throws EmployeePayrollException {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,basic_pay,start) " + "VALUES	('%s','%s',%s,'%s')", name,
				gender, basic_pay, Date.valueOf(startDate));
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, basic_pay, startDate, gender);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, double basic_pay, LocalDate startDate, String gender,
			int companyId, String departmentName, String companyName) throws EmployeePayrollException {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee(name,gender,start,companyid)" + "VALUES ('%s','%s','%s','%s')", name, gender,
					Date.valueOf(startDate), companyId);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new EmployeePayrollException("insert error");
			}
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO department(empid,departmentName)" + "VALUES ('%s','%s')",
					employeeId, departmentName);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new EmployeePayrollException("insert error");
			}
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO company(companyid,companyName)" + "VALUES ('%s','%s')", companyId,
					companyName);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new EmployeePayrollException("insert error");
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = basic_pay * 0.2;
			double taxablePay = basic_pay - deductions;
			double tax = taxablePay * 0.1;
			double netPay = basic_pay - tax;
			String sql = String.format(
					"insert into payroll(empid,basicpay,deduction,taxablepay,tax,netpay)"
							+ " VALUES ('%s','%s','%s','%s','%s','%s' )",
					employeeId, basic_pay, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, startDate, basic_pay, gender.charAt(0),
						companyId, departmentName, companyName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {

				e1.printStackTrace();
				throw new EmployeePayrollException("query error");
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollException("error in commiting transaction");
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new EmployeePayrollException("error in closing connection");
			}
		}
		return employeePayrollData;
	}

	public List<EmployeePayrollData> readDataFromEmployeePayroll() throws EmployeePayrollException {
		String sql = "SELECT * FROM employee;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollDataThread(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollDataThread(ResultSet resultSet)
			throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("empid");
				String name = resultSet.getString("name");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				double basic_pay = resultSet.getDouble("basic_pay");
				char gender = resultSet.getString("gender").charAt(0);
				employeePayrollDataList.add(new EmployeePayrollData(id, name, basic_pay, start, gender));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollException("unable to read data from database");
		}
		return employeePayrollDataList;
	}
}
