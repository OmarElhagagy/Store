package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Employee createEmployee(String firstName, String middleName, String lastName, 
                                 String email, String gender, String role, 
                                 Store store, Storage storage, Employee supervisor) {
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use: " + email);
        }
        
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        
        Employee employee = new Employee();
        employee.setFName(firstName);
        employee.setMName(middleName);
        employee.setLName(lastName);
        employee.setEmail(email);
        employee.setGender(gender);
        employee.setRole(role);
        employee.setStore(store);
        employee.setStorageNo(storage); // Can be null
        employee.setSupervisor(supervisor); // Can be null
        
        // Initialize sets
        employee.setAuditLogs(new HashSet<>());
        employee.setCustomerOrders(new HashSet<>());
        employee.setEmployees(new HashSet<>());
        employee.setNotifications(new HashSet<>());
        employee.setPhoneNumbersEmployees(new HashSet<>());
        employee.setSupplyOrders(new HashSet<>());
        
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> findEmployeeById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> findEmployeeByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Name keyword cannot be null or empty");
        }
        return employeeRepository.findByFNameContainingOrLNameContaining(nameKeyword, nameKeyword);
    }

    @Override
    public List<Employee> getEmployeesByGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }
        return employeeRepository.findByGender(gender);
    }

    @Override
    public List<Employee> getEmployeesByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return employeeRepository.findByRole(role);
    }
    
    @Override
    public List<Employee> getEmployeesByRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles list cannot be null or empty");
        }
        return employeeRepository.findByRoleIn(roles);
    }

    @Override
    public List<Employee> getEmployeesByStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        return employeeRepository.findByStore(store);
    }

    @Override
    public List<Employee> getEmployeesByStorage(Storage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null");
        }
        return employeeRepository.findByStorageNo(storage);
    }

    @Override
    public List<Employee> getEmployeesBySupervisor(Employee supervisor) {
        if (supervisor == null) {
            throw new IllegalArgumentException("Supervisor cannot be null");
        }
        return employeeRepository.findBySupervisor(supervisor);
    }

    @Override
    public List<Employee> getEmployeesWithoutSupervisor() {
        return employeeRepository.findBySupervisorIsNull();
    }

    @Override
    public List<Employee> getStoreManagers(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        return employeeRepository.findByRoleAndStore("Manager", store);
    }

    @Override
    public List<Employee> getEmployeesWithDirectReports() {
        return employeeRepository.findEmployeesWithDirectReports();
    }

    @Override
    public List<Employee> getEmployeesWithSales() {
        return employeeRepository.findEmployeesWithSales();
    }

    @Override
    public List<Employee> getEmployeesWithSupplyOrders() {
        return employeeRepository.findEmployeesWithSupplyOrders();
    }

    @Override
    public List<Employee> getEmployeesWithAuditLogs() {
        return employeeRepository.findEmployeesWithAuditLogs();
    }
    
    @Override
    public List<Employee> getEmployeesByStoreAndRole(Store store, String role) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return employeeRepository.findByStoreAndRole(store, role);
    }

    @Override
    public Map<Employee, Long> getTopSellingEmployees(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        
        List<Object[]> results = employeeRepository.findTopSellingEmployees();
        
        Map<Employee, Long> topEmployees = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            Employee employee = (Employee) result[0];
            Long orderCount = (Long) result[1];
            topEmployees.put(employee, orderCount);
            count++;
        }
        
        return topEmployees;
    }
    
    @Override
    public List<Employee> getAllSupervisors() {
        return employeeRepository.findAllSupervisors();
    }
    
    @Override
    public List<Employee> getEmployeeHierarchy(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        return employeeRepository.findEmployeeHierarchy(employeeId);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        if (!employeeRepository.existsById(employee.getId())) {
            throw new IllegalArgumentException("Employee not found with ID: " + employee.getId());
        }
        
        // Verify email uniqueness if it was changed
        Optional<Employee> existingEmployee = employeeRepository.findById(employee.getId());
        if (existingEmployee.isPresent() && !existingEmployee.get().getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(employee.getEmail())) {
                throw new IllegalArgumentException("Email is already in use: " + employee.getEmail());
            }
        }
        
        // Verify that store is not null
        if (employee.getStore() == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee updateEmployeeContactInfo(Integer employeeId, String email) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            
            // Check if the email already exists for another employee
            if (!email.equals(employee.getEmail()) && employeeRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already in use: " + email);
            }
            
            employee.setEmail(email);
            return employeeRepository.save(employee);
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    @Transactional
    public Employee updateEmployeeRole(Integer employeeId, String role) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setRole(role);
            return employeeRepository.save(employee);
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    @Transactional
    public Employee updateEmployeeSupervisor(Integer employeeId, Employee supervisor) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            
            // Prevent self-supervision
            if (supervisor != null && supervisor.getId().equals(employeeId)) {
                throw new IllegalArgumentException("An employee cannot be their own supervisor");
            }
            
            employee.setSupervisor(supervisor);
            return employeeRepository.save(employee);
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    @Transactional
    public Employee updateEmployeeStore(Integer employeeId, Store store) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setStore(store);
            return employeeRepository.save(employee);
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public Set<Employee> getEmployeeDirectReports(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get().getEmployees();
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public Set<CustomerOrder> getEmployeeCustomerOrders(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get().getCustomerOrders();
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public Set<PhoneNumbersEmployee> getEmployeePhoneNumbers(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get().getPhoneNumbersEmployees();
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public Set<SupplyOrder> getEmployeeSupplyOrders(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get().getSupplyOrders();
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public Set<AuditLog> getEmployeeAuditLogs(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get().getAuditLogs();
        }
        
        throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
    }

    @Override
    public long countEmployeesByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return employeeRepository.countByRole(role);
    }

    @Override
    public long countEmployeesByStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        return employeeRepository.countByStore(store);
    }

    @Override
    public boolean isEmailTaken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return employeeRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
        }
        
        // Check if employee has direct reports before deleting
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent() && !employee.get().getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete employee with direct reports. Reassign direct reports first.");
        }
        
        employeeRepository.deleteById(employeeId);
    }
}