package com.fitted.order;

import com.fitted.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
@Service
public class OrderNotificationService {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final AuthorizationService authService;

    public void checkAdmin(String supabaseId) {
        authService.checkAdmin(supabaseId);
    }

    public void registerEmitter(String adminId, SseEmitter emitter) {
        emitters
                .computeIfAbsent(adminId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(adminId, emitter));
        emitter.onTimeout(() -> removeEmitter(adminId, emitter));
        emitter.onError((e) -> removeEmitter(adminId, emitter));
    }

    private void removeEmitter(String adminId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(adminId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    public void notifyOrderCreated(Order order) {
        emitters.forEach((adminId, list) -> {
            for (Iterator<SseEmitter> it = list.iterator(); it.hasNext(); ) {
                SseEmitter emitter = it.next();
                try {
                    emitter.send(SseEmitter.event()
                            .name("order-created")
                            .data(Map.of(
                                    "orderId", order.getId(),
                                    "userId", order.getUserId(),
                                    "total", order.getTotalPrice()
                            ))
                            .id(UUID.randomUUID().toString())
                    );
                } catch (IOException e) {
                    it.remove();
                }
            }
        });
    }
}
