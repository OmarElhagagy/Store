package com.example.demo.service;

import com.example.demo.entities.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface EmployeeService {
    
    // Create a new employee
    Employee createEmployee(String firstName, String middleName, String lastName, 
                          String email, String gender, String role, 
                          Store store, Storage storage, Employee supervisor);
    
    // Find employee by ID
    Optional<Employee> findEmployeeById(Integer id);
    
    // Find employee by email
    Optional<Employee> findEmployeeByEmail(String email);
    
    // Get all employees
    List<Employee> getAllEmployees();
    
    // Get employees by name search
    List<Employee> getEmployeesByNameSearch(String nameKeyword);
    
    // Get employees by gender
    List<Employee> getEmployeesByGender(String gender);
    
    // Get employees by role
    List<Employee> getEmployeesByRole(String role);
    
    // Get employees by multiple roles
    List<Employee> getEmployeesByRoles(List<String> roles);
    
    // Get employees by store
    List<Employee> getEmployeesByStore(Store store);
    
    // Get employees by storage
    List<Employee> getEmployeesByStorage(Storage storage);
    
    // Get employees by supervisor
    List<Employee> getEmployeesBySupervisor(Employee supervisor);
    
    // Get employees without a supervisor
    List<Employee> getEmployeesWithoutSupervisor();
    
    // Get store managers
    List<Employee> getStoreManagers(Store store);
    
    // Get employees with direct reports
    List<Employee> getEmployeesWithDirectReports();
    
    // Get employees with sales
    List<Employee> getEmployeesWithSales();
    
    // Get employees with supply orders
    List<Employee> getEmployeesWithSupplyOrders();
    
    // Get employees with audit logs
    List<Employee> getEmployeesWithAuditLogs();
    
    // Get employees by store and role
    List<Employee> getEmployeesByStoreAndRole(Store store, String role);
    
    // Get top selling employees
    Map<Employee, Long> getTopSellingEmployees(int limit);
    
    // Get all supervisors
    List<Employee> getAllSupervisors();
    
    // Get employee hierarchy (all supervisors up the chain)
    List<Employee> getEmployeeHierarchy(Integer employeeId);
    
    // Update employee
    Employee updateEmployee(Employee employee);
    
    // Update employee contact information
    Employee updateEmployeeContactInfo(Integer employeeId, String email);
    
    // Update employee role
    Employee updateEmployeeRole(Integer employeeId, String role);
    
    // Update employee supervisor
    Employee updateEmployeeSupervisor(Integer employeeId, Employee supervisor);
    
    // Update employee store
    Employee updateEmployeeStore(Integer employeeId, Store store);
    
    // Get employee direct reports
    Set<Employee> getEmployeeDirectReports(Integer employeeId);
    
    // Get employee customer orders
    Set<CustomerOrder> getEmployeeCustomerOrders(Integer employeeId);
    
    // Get employee phone numbers
    Set<PhoneNumbersEmployee> getEmployeePhoneNumbers(Integer employeeId);
    
    // Get employee supply orders
    Set<SupplyOrder> getEmployeeSupplyOrders(Integer employeeId);
    
    // Get employee audit logs
    Set<AuditLog> getEmployeeAuditLogs(Integer employeeId);
    
    // Count employees by role
    long countEmployeesByRole(String role);
    
    // Count employees by store
    long countEmployeesByStore(Store store);
    
    // Check if email exists
    boolean isEmailTaken(String email);
    
    // Delete employee
    void deleteEmployee(Integer employeeId);
}