package com.example.employeefpt.service;

import com.example.employeefpt.model.Employee;
import com.example.employeefpt.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }


    public Employee addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee không được null");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            throw new IllegalArgumentException("Lương > 0");
        }

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        if (employee == null || employee.getId() == null) {
            throw new IllegalArgumentException("Employee ID không null");
        }

        Optional<Employee> existingEmployee = employeeRepository.findById(employee.getId());
        if (existingEmployee.isEmpty()) {
            throw new RuntimeException("Không tìm thấy nhân viên  " + employee.getId());
        }

        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không để trống ");
        }
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            throw new IllegalArgumentException("Lương > 0");
        }

        Employee emp = existingEmployee.get();
        emp.setName(employee.getName());
        emp.setSalary(employee.getSalary());
        
        return employeeRepository.save(emp);
    }



    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    


    public List<Employee> searchEmployeesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getEmployees();
        }
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    public long getTotalEmployees() {
        return employeeRepository.count();
    }

    public List<Employee> getEmployeesBySalaryRange(Double minSalary, Double maxSalary) {
        return employeeRepository.findBySalaryBetween(minSalary, maxSalary);
    }
}
