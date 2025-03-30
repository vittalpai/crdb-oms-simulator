package com.oms.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
@Transactional
@RequestMapping("/api/oms")
public class OmsController {

    @PersistenceContext
    private EntityManager entityManager;

    // 1. Create Order
    @PostMapping("/create/{orderId}")
    public ResponseEntity<String> createOrder(@PathVariable Long orderId) {
        try {
            insertOrderDetails(orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
        }
    }

    // 2. Get Order
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrder(@PathVariable Long orderId) {
        OmsInfo order = entityManager.find(OmsInfo.class, orderId);
        return (order != null)
                ? ResponseEntity.ok(order)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
    }

    // 3. Update Order Status
    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId) {
        try {
            String updateQuery = """
                UPDATE public.oms_details SET
                    order_status = :status,
                    updated_at = :updatedAt
                WHERE order_id = :orderId
            """;

            Query query = entityManager.createNativeQuery(updateQuery);
            query.setParameter("orderId", orderId);
            query.setParameter("status", getRandomStatus());
            query.setParameter("updatedAt", LocalDateTime.now());

            int updated = query.executeUpdate();
            return (updated > 0)
                    ? ResponseEntity.ok("Order status updated.")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed.");
        }
    }

    // 4. Process Order (Insert -> Select -> Update)
    @PostMapping("/process/{createOrderId}/{updateOrderId}")
    public ResponseEntity<String> processOrderDetails(
            @PathVariable Long createOrderId,
            @PathVariable Long updateOrderId) {
        try {
            insertOrderDetails(createOrderId);
            OmsInfo order = entityManager.find(OmsInfo.class, updateOrderId);
            if (order == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
            updateOrderStatus(updateOrderId);
            return ResponseEntity.ok("Order processed. Selected Order: " + order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Order processing failed.");
        }
    }

    // 5. Cancel Order
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        try {
            String cancelQuery = """
                UPDATE public.oms_details SET
                    order_status = 'CANCELLED',
                    updated_at = :updatedAt
                WHERE order_id = :orderId
            """;

            Query query = entityManager.createNativeQuery(cancelQuery);
            query.setParameter("orderId", orderId);
            query.setParameter("updatedAt", LocalDateTime.now());

            int cancelled = query.executeUpdate();
            return (cancelled > 0)
                    ? ResponseEntity.ok("Order cancelled.")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cancellation failed.");
        }
    }

    // 6. Mocked List by Status
    @GetMapping("/list/{status}")
    public ResponseEntity<String> listOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok("Returning mocked list of orders with status: " + status);
    }

    private void insertOrderDetails(Long orderId) {
        String insertQuery = """
            INSERT INTO public.oms_details (
                order_id, customer_id, order_status, total_amount, item_details,
                created_at, updated_at, version
            ) VALUES (
                :orderId, :customerId, :orderStatus, :totalAmount, :itemDetails,
                :createdAt, :updatedAt, :version
            )
        """;

        Query query = entityManager.createNativeQuery(insertQuery);
        query.setParameter("orderId", orderId);
        query.setParameter("customerId", "CUST-" + new Random().nextInt(1000));
        query.setParameter("orderStatus", "PENDING");
        query.setParameter("totalAmount", String.format("%.2f", 100 + new Random().nextDouble() * 900));
        query.setParameter("itemDetails", "{\"items\": \"Sample Item #" + new Random().nextInt(999) + "\"}");
        query.setParameter("createdAt", LocalDateTime.now());
        query.setParameter("updatedAt", LocalDateTime.now());
        query.setParameter("version", 1);

        query.executeUpdate();
    }

    private String getRandomStatus() {
        String[] statuses = {"CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED"};
        return statuses[new Random().nextInt(statuses.length)];
    }
}