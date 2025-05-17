package com.example.demo.repositories;

import com.example.demo.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Find notifications by customer
    List<Notification> findByCustomerId(Integer customerId);

    // Find notifications by store
    List<Notification> findByStoreId(Integer storeId);

    // Find notifications by product
    List<Notification> findByProductId(Integer productId);

    // Find notifications by employee
    List<Notification> findByEmployeeId(Integer employeeId);

    // Find notifications within date range
    List<Notification> findBySentDateBetween(LocalDate startDate, LocalDate endDate);

    // Find unread notifications
    @Query("SELECT n FROM Notification n WHERE n.read = false")
    List<Notification> findUnreadNotifications();

    // Find notifications by message content
    @Query("SELECT n FROM Notification n WHERE LOWER(n.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Notification> searchByMessage(@Param("searchTerm") String searchTerm);

    // Count unread notifications for a customer
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.customer.id = :customerId AND n.read = false")
    Long countUnreadByCustomerId(@Param("customerId") Integer customerId);
}