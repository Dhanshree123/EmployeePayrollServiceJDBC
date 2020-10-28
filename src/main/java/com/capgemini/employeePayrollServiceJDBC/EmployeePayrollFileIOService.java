package com.capgemini.employeePayrollServiceJDBC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeePayrollFileIOService {
	public static String PAYROLL_FILE_NAME = "payroll-file.txt";

	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		StringBuffer empBuffer = new StringBuffer();
		employeePayrollList.forEach(employee -> {
			String employeeDataString = employee.toString().concat("\n");
			empBuffer.append(employeeDataString);
		});

		try {
			Files.write(Paths.get(PAYROLL_FILE_NAME), empBuffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printData() {
		try {
			Files.lines(new File("payroll-file.txt").toPath()).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File("payroll-file.txt").toPath()).count();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	public List<EmployeePayrollData> readData() {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			Files.lines(new File("payroll-file.txt").toPath()).map(line -> line.trim())
					.forEach(line -> System.out.println(line));

			List<String> list = Files.lines(new File("payroll-file.txt").toPath()).map(line -> line.trim())
					.collect(Collectors.toList());
			for (String s : list) {
				s = s.replaceAll("\\s", "");
				String[] str = s.split(",");
				int i = 0;
				for (String a : str) {
					String[] b = a.split("=");
					str[i] = b[1];
					i++;
				}
				employeePayrollList
						.add(new EmployeePayrollData(Integer.parseInt(str[0]), str[1], Double.parseDouble(str[2])));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
}
