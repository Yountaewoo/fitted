package com.fitted.order;

// Enum 이름은 구체적으로 OrderStatus 로 짓는 걸 추천합니다.
public enum OrderStatus {
    // 주문이 생성만 되고 결제 전인 상태
    CREATED,            // 또는 PAYMENT_PENDING

    // 결제 요청은 보냈으나 아직 결과 대기 중
    PAYMENT_PENDING,    // (CREATED 대신 쓸 수도 있습니다)

    // 결제 성공
    PAID,               // 또는 PAYMENT_COMPLETED

    // 주문 취소
    CANCELLED,          // 또는 ORDER_CANCELLED

    // 상품이 발송된 상태
    SHIPPED,

    // 고객에게 배송 완료된 상태
    DELIVERED,

    // 최종 완료 (예: 반품·환불 처리까지 완료)
    COMPLETED,          // 또는 FINISHED

    // 환불 완료
    REFUNDED,

    // 반품 요청 중 혹은 완료
    RETURN_REQUESTED,
    RETURNED
}
