package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Entitlement;
import br.com.pw2m.contos.domain.EntitlementSource;
import br.com.pw2m.contos.domain.OrderStatus;
import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.domain.StoryOrder;
import br.com.pw2m.contos.dto.OrderResponse;
import br.com.pw2m.contos.repository.EntitlementRepository;
import br.com.pw2m.contos.repository.StoryOrderRepository;
import br.com.pw2m.contos.repository.UserRepository;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

    private final StoryOrderRepository orders;
    private final EntitlementRepository entitlements;
    private final UserRepository users;
    private final StoryService stories;

    public OrderService(
            StoryOrderRepository orders,
            EntitlementRepository entitlements,
            UserRepository users,
            StoryService stories
    ) {
        this.orders = orders;
        this.entitlements = entitlements;
        this.users = users;
        this.stories = stories;
    }

    @Transactional
    public OrderResponse create(UUID userId, String storySlug) {
        Story story = stories.requireActive(storySlug);
        if (entitlements.existsByUser_IdAndStory_Id(userId, story.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Você já tem esta história na biblioteca.");
        }
        if (story.getPriceCents() <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Esta história não está à venda.");
        }
        return orders.findByUser_IdAndStory_IdAndStatus(userId, story.getId(), OrderStatus.PENDING)
                .map(this::toResponse)
                .orElseGet(() -> toResponse(orders.save(new StoryOrder(
                        UUID.randomUUID(),
                        users.getReferenceById(userId),
                        story,
                        story.getPriceCents(),
                        Instant.now()
                ))));
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID userId, UUID orderId) {
        return toResponse(findOwned(userId, orderId));
    }

    @Transactional
    public OrderResponse pay(UUID userId, UUID orderId) {
        StoryOrder order = findOwned(userId, orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException(HttpStatus.CONFLICT, "Este pedido foi cancelado.");
        }
        if (order.getStatus() == OrderStatus.PENDING) {
            order.markPaid(Instant.now());
        }
        grant(order);
        return toResponse(order);
    }

    private void grant(StoryOrder order) {
        if (entitlements.existsByUser_IdAndStory_Id(order.getUser().getId(), order.getStory().getId())) {
            return;
        }
        entitlements.save(new Entitlement(
                UUID.randomUUID(),
                order.getUser(),
                order.getStory(),
                EntitlementSource.PURCHASE,
                null,
                Instant.now()
        ));
    }

    private StoryOrder findOwned(UUID userId, UUID orderId) {
        return orders.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    private OrderResponse toResponse(StoryOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getStory().getSlug(),
                order.getStory().getTitle(),
                order.getAmountCents(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getPaidAt()
        );
    }
}
