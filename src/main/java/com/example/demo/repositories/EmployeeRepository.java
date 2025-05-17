// package com.example.demo.repositories;

// import com.example.demo.entities.Employee;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
//     // Custom finders for active employees
//     @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.active = true")
//     Optional<Employee> findByEmail(@Param("email") String email);
    
//     @Query("SELECT e FROM Employee e WHERE LOWER(e.fName) LIKE LOWER(CONCAT('%', :name, '%')) AND e.active = true")
//     List<Employee> findByFNameContainingIgnoreCase(@Param("name") String name);
    
//     @Query("SELECT e FROM Employee e WHERE LOWER(e.lName) LIKE LOWER(CONCAT('%', :name, '%')) AND e.active = true")
//     List<Employee> findByLNameContainingIgnoreCase(@Param("name") String name);
    
//     @Query("SELECT e FROM Employee e WHERE e.hireDate > :date AND e.active = true")
//     List<Employee> findByHireDateAfter(@Param("date") LocalDate date);
    
//     @Query("SELECT e FROM Employee e WHERE e.hireDate BETWEEN :startDate AND :endDate AND e.active = true")
//     List<Employee> findByHireDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
//     @Query("SELECT e FROM Employee e WHERE e.position = :position AND e.active = true")
//     List<Employee> findByPosition(@Param("position") String position);
    
//     @Query("SELECT e FROM Employee e WHERE e.department = :department AND e.active = true")
//     List<Employee> findByDepartment(@Param("department") String department);
    
//     @Query("SELECT e FROM Employee e WHERE e.store.id = :storeId AND e.active = true")
//     List<Employee> findByStoreId(@Param("storeId") Integer storeId);
    
//     @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.email = :email AND e.active = true")
//     boolean existsByEmail(@Param("email") String email);
    
//     // Salary range queries
//     @Query("SELECT e FROM Employee e WHERE e.salary >= :minSalary AND e.salary <= :maxSalary AND e.active = true")
//     List<Employee> findBySalaryBetween(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);
    
//     @Query("SELECT e FROM Employee e WHERE e.salary > :salary AND e.active = true")
//     List<Employee> findBySalaryGreaterThan(@Param("salary") Double salary);
    
//     // Override JPA repository default methods to filter by active status
//     @Override
//     @Query("SELECT e FROM Employee e WHERE e.active = true")
//     List<Employee> findAll();
    
//     @Override
//     @Query("SELECT e FROM Employee e WHERE e.id = :id AND e.active = true")
//     Optional<Employee> findById(@Param("id") Integer id);
    
//     @Override
//     @Query("SELECT COUNT(e) FROM Employee e WHERE e.active = true")
//     long count();
    
//     // For admin/recovery purposes - can find inactive employees too
//     @Query("SELECT e FROM Employee e WHERE e.id = :id")
//     Optional<Employee> findEmployeeById(@Param("id") Integer id);
    
//     @Query("SELECT e FROM Employee e WHERE e.active = false")
//     List<Employee> findInactiveEmployees();
    
//     // Additional employee-specific queries
//     @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId AND e.active = true")
//     List<Employee> findByManagerId(@Param("managerId") Integer managerId);
    
//     @Query("SELECT e FROM Employee e WHERE e.isManager = true AND e.active = true")
//     List<Employee> findAllManagers();
    
//     @Query("SELECT e FROM Employee e WHERE e.terminationDate IS NOT NULL AND e.active = false")
//     List<Employee> findTerminatedEmployees();
    
//     @Query("SELECT e FROM Employee e WHERE e.terminationDate BETWEEN :startDate AND :endDate")
//     List<Employee> findEmployeesTerminatedBetween(@Param("startDate") LocalDate startDate, 
//                                                 @Param("endDate") LocalDate endDate);
    
//     // Search by multiple criteria
//     @Query("SELECT e FROM Employee e WHERE " +
//            "(:department IS NULL OR e.department = :department) AND " +
//            "(:position IS NULL OR e.position = :position) AND " +
//            "(:storeId IS NULL OR e.store.id = :storeId) AND " +
//            "e.active = true")
//     List<Employee> searchEmployees(@Param("department") String department,
//                                   @Param("position") String position,
//                                   @Param("storeId") Integer storeId);
// }
package com.example.demo.repositories;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Store;
import com.example.demo.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
    // Find by email
    Optional<Employee> findByEmail(String email);
    
    // Find by name (partial match)
    List<Employee> findByFNameContainingOrLNameContaining(String firstNameKeyword, String lastNameKeyword);
    
    // Find by gender
    List<Employee> findByGender(String gender);
    
    // Find by role
    List<Employee> findByRole(String role);
    
    // Find by store
    List<Employee> findByStore(Store store);
    
    // Find by storage
    List<Employee> findByStorageNo(Storage storage);
    
    // Find by supervisor
    List<Employee> findBySupervisor(Employee supervisor);
    
    // Find employees without a supervisor
    List<Employee> findBySupervisorIsNull();
    
    // Find store managers
    List<Employee> findByRoleAndStore(String role, Store store);
    
    // Find employees with direct reports
    @Query("SELECT DISTINCT e FROM Employee e WHERE SIZE(e.employees) > 0")
    List<Employee> findEmployeesWithDirectReports();
    
    // Find employees with sales
    @Query("SELECT DISTINCT e FROM Employee e WHERE SIZE(e.customerOrders) > 0")
    List<Employee> findEmployeesWithSales();
    
    // Find employees with supply orders
    @Query("SELECT DISTINCT e FROM Employee e WHERE SIZE(e.supplyOrders) > 0")
    List<Employee> findEmployeesWithSupplyOrders();
    
    // Find employees with audit logs
    @Query("SELECT DISTINCT e FROM Employee e WHERE SIZE(e.auditLogs) > 0")
    List<Employee> findEmployeesWithAuditLogs();
    
    // Find top selling employees
    @Query("SELECT e, COUNT(o) as orderCount FROM Employee e JOIN e.customerOrders o GROUP BY e ORDER BY orderCount DESC")
    List<Object[]> findTopSellingEmployees();
    
    // Count employees by role
    long countByRole(String role);
    
    // Count employees by store
    long countByStore(Store store);
    
    // Find employees by multiple roles
    List<Employee> findByRoleIn(List<String> roles);
    
    // Find employees by store and role
    List<Employee> findByStoreAndRole(Store store, String role);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find all supervisors
    @Query("SELECT DISTINCT e.supervisor FROM Employee e WHERE e.supervisor IS NOT NULL")
    List<Employee> findAllSupervisors();
    
    // Find hierarchy for employee (all supervisors up the chain)
    @Query(value = "WITH RECURSIVE supervisors AS (" +
                   "  SELECT e.*, 1 as level FROM \"Employee\" e WHERE e.\"Employee_ID\" = :employeeId " +
                   "  UNION ALL " +
                   "  SELECT e.*, s.level + 1 FROM \"Employee\" e " +
                   "  JOIN supervisors s ON e.\"Employee_ID\" = s.\"Supervisor\" " +
                   ") " +
                   "SELECT * FROM supervisors ORDER BY level", nativeQuery = true)
    List<Employee> findEmployeeHierarchy(@Param("employeeId") Integer employeeId);
}
