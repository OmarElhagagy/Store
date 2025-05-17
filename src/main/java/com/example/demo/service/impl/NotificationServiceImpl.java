package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        // Set sent date to current date if not provided
        if (notification.getSentDate() == null) {
            notification.setSentDate(LocalDate.now());
        }

        // Validate relationships if they exist
        if (notification.getCustomer() != null && 
            !customerRepository.existsById(notification.getCustomer().getId())) {
            return null;
        }
        if (notification.getStore() != null && 
            !storeRepository.existsById(notification.getStore().getId())) {
            return null;
        }
        if (notification.getProduct() != null && 
            !productRepository.existsById(notification.getProduct().getId())) {
            return null;
        }
        if (notification.getEmployee() != null && 
            !employeeRepository.existsById(notification.getEmployee().getId())) {
            return null;
        }

        return notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(Integer id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Notification> getNotificationsByCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            return List.of();
        }
        return notificationRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Notification> getNotificationsByStore(Integer storeId) {
        if (!storeRepository.existsById(storeId)) {
            return List.of();
        }
        return notificationRepository.findByStoreId(storeId);
    }

    @Override
    public List<Notification> getNotificationsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return notificationRepository.findByProductId(productId);
    }

    @Override
    public List<Notification> getNotificationsByEmployee(Integer employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return List.of();
        }
        return notificationRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Notification> getNotificationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return notificationRepository.findBySentDateBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Notification markAsRead(Integer notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<Notification> searchNotifications(String searchTerm) {
        return notificationRepository.searchByMessage(searchTerm);
    }

    @Override
    public Long getUnreadCountForCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            return 0L;
        }
        return notificationRepository.countUnreadByCustomerId(customerId);
    }
}