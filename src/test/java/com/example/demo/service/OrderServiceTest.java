package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.impl.OrderServiceImpl;
import com.example.demo.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDetail testOrderDetail;
    private User testUser;
    private Product testProduct;
    private List<Order> testOrders;

    @BeforeEach
    public void setup() {
        testUser = TestDataFactory.createTestUser();
        testProduct = TestDataFactory.createTestProduct();
        testOrder = TestDataFactory.createTestOrder();
        testOrderDetail = TestDataFactory.createTestOrderDetail();
        
        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(testOrderDetail);
        testOrder.setOrderDetails(orderDetails);
        
        testOrders = new ArrayList<>();
        testOrders.add(testOrder);
        
        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(testUser);
        order2.setOrderDate(LocalDateTime.now());
        order2.setStatus("SHIPPED");
        order2.setTotalPrice(new BigDecimal("299.99"));
        testOrders.add(order2);
    }

    @Test
    public void testGetAllOrders() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(testOrders, pageable, testOrders.size());
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // Act
        Page<Order> result = orderService.getAllOrders(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(testOrders.get(0).getId(), result.getContent().get(0).getId());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    public void testGetOrdersByUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserId(anyLong())).thenReturn(testOrders);

        // Act
        List<Order> result = orderService.getOrdersByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testOrders.get(0).getId(), result.get(0).getId());
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    public void testGetOrderById_Found() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getStatus(), result.getStatus());
        verify(orderRepository).findById(1L);
    }

    @Test
    public void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository).findById(999L);
    }

    @Test
    public void testCreateOrderFromCart() {
        // Arrange
        Cart cart = TestDataFactory.createTestCart();
        CartItem cartItem = TestDataFactory.createTestCartItem();
        List<CartItem> cartItems = Collections.singletonList(cartItem);
        cart.setCartItems(cartItems);
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartService.getUserCart(anyLong())).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(testOrderDetail);
        doNothing().when(cartService).clearCart(anyLong());

        // Act
        Order result = orderService.createOrderFromCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals("PENDING", result.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).save(any(OrderDetail.class));
        verify(cartService).clearCart(1L);
    }

    @Test
    public void testUpdateOrderStatus() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, "SHIPPED");

        // Assert
        assertNotNull(result);
        assertEquals("SHIPPED", result.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    public void testCancelOrder() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(orderRepository).save(testOrder);
    }
} 