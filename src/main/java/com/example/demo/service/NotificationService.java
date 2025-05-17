package com.example.demo.service;

import com.example.demo.entities.Notification;
import java.time.LocalDate;
import java.util.List;

public interface NotificationService {

    // Create a new notification
    Notification createNotification(Notification notification);

    // Get notification by ID
    Notification getNotificationById(Integer id);

    // Get all notifications for a customer
    List<Notification> getNotificationsByCustomer(Integer customerId);

    // Get all notifications for a store
    List<Notification> getNotificationsByStore(Integer storeId);

    // Get all notifications for a product
    List<Notification> getNotificationsByProduct(Integer productId);

    // Get all notifications for an employee
    List<Notification> getNotificationsByEmployee(Integer employeeId);

    // Get notifications within date range
    List<Notification> getNotificationsByDateRange(LocalDate startDate, LocalDate endDate);

    // Mark notification as read
    Notification markAsRead(Integer notificationId);

    // Delete notification
    void deleteNotification(Integer notificationId);

    // Search notifications by message content
    List<Notification> searchNotifications(String searchTerm);

    // Get count of unread notifications for customer
    Long getUnreadCountForCustomer(Integer customerId);
}