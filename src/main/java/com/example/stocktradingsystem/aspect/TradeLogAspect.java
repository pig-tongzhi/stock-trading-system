package com.example.stocktradingsystem.aspect;

import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.dto.TradeOrderResponse;
import com.example.stocktradingsystem.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// AOP 切面：统一记录交易日志，成功/失败都记录 userId、股票代码、耗时等关键信息
// 通过 @Around 环绕增强，不入侵核心交易逻辑
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TradeLogAspect {

    private final CurrentUserService currentUserService;

    @Around("execution(* com.example.stocktradingsystem.service.TradingService.placeOrder(..))")
    public Object logTrade(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        OrderPlaceRequest request = args.length > 0 && args[0] instanceof OrderPlaceRequest orderRequest
                ? orderRequest
                : null;
        Long userId = currentUserService.currentUser().userId();
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            if (result instanceof TradeOrderResponse response) {
                log.info(
                        "trade success userId={}, orderId={}, stockCode={}, side={}, price={}, quantity={}, costMs={}",
                        userId,
                        response.orderId(),
                        response.stockCode(),
                        response.side(),
                        response.price(),
                        response.quantity(),
                        System.currentTimeMillis() - start
                );
            }
            return result;
        } catch (Throwable throwable) {
            log.warn(
                    "trade failed userId={}, stockCode={}, side={}, quantity={}, reason={}",
                    userId,
                    request == null ? null : request.stockCode(),
                    request == null ? null : request.side(),
                    request == null ? null : request.quantity(),
                    throwable.getMessage()
            );
            throw throwable;
        }
    }
}
