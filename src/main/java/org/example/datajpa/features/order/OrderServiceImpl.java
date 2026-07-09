package org.example.datajpa.features.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.datajpa.config.SecurityUtils;
import org.example.datajpa.features.order.dto.*;
import org.example.datajpa.features.orderLine.OrderLine;
import org.example.datajpa.features.orderLine.OrderLineRepository;
import org.example.datajpa.features.product.Product;
import org.example.datajpa.features.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {

        List<OrderLine> orderLines = new ArrayList<>();
        final Order order = orderMapper.toOrder(orderRequest);
        boolean isValidTrue = orderRequest.orderLine().stream()
                .allMatch(orderLineDto -> {
                    Optional<Product> productOptional = productRepository.findByCode(orderLineDto.code());
                    if(productOptional.isPresent()){
                        OrderLine orderLine = new OrderLine();
                        orderLine.setProduct(productOptional.get());
                        orderLine.setQty(orderLineDto.qty());
                        orderLine.setUnitPrice(orderLineDto.unitPrice());
                        orderLine.setOrder(order);
                        orderLines.add(orderLine);
                        return true;
                    }
                    return false;
                });
        if(!isValidTrue){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid order line.");
        }
        order.setCustomerId(SecurityUtils.extractUserId());
        order.setIsDelete(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(false);
        order.setOrderLines(orderLines);
        Order orders = orderRepository.save(order);
        return orderMapper.toOrderResponse(orders);
    }

    @Override
    public Page<OrderResponse> getAllOrder(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        Order exits = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return orderMapper.toOrderResponse(exits);
    }

    @Override
    public void softDeleteById(UUID id, SoftDeleteRequest softDeleteRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        order.setIsDelete(softDeleteRequest.isDelete());
        orderRepository.save(order);
    }

    @Override
    public void hardDeleteById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        orderRepository.delete(order);
    }

    @Override
    public OrderResponse setPaymentStatusById(UUID id, UpdatePaymentRequest updatePaymentRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        order.setStatus(updatePaymentRequest.status());
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }


}
