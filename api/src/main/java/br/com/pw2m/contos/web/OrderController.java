package br.com.pw2m.contos.web;

import br.com.pw2m.contos.dto.CreateOrderRequest;
import br.com.pw2m.contos.dto.OrderResponse;
import br.com.pw2m.contos.security.CurrentUser;
import br.com.pw2m.contos.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orders.create(CurrentUser.id(), request.storySlug());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return orders.get(CurrentUser.id(), id);
    }

    @PostMapping("/{id}/pay")
    public OrderResponse pay(@PathVariable UUID id) {
        return orders.pay(CurrentUser.id(), id);
    }
}
