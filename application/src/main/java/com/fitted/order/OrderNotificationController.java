package com.fitted.order;

import com.fitted.loginUtils.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class OrderNotificationController {

    private final OrderNotificationService orderNotificationService;

    @GetMapping("/stream")
    public SseEmitter streamNotifications(@LoginMemberId String supabaseId) {
        // supabaseId로 권한 검증 후, 관리자만 구독할 수 있게 처리
        orderNotificationService.checkAdmin(supabaseId);

        // 타임아웃 없이 무한 유지되는 emitter 생성
        SseEmitter emitter = new SseEmitter(0L);
        orderNotificationService.registerEmitter(supabaseId, emitter);
        return emitter;
    }
}
