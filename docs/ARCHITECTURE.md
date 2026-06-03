# 架构设计

## 系统概述

`stock-trading-system` 是一个面向 Java 后端面试的模拟股票交易系统。系统基于 Spring Boot 3 和 Java 17，使用 MySQL 存储核心业务数据，使用 Redis 缓存股票行情，通过 JWT 完成用户认证，通过定时任务模拟行情波动，并使用 AOP 记录交易日志。

## 分层架构

```text
controller  -> REST API 入口，负责请求校验和统一响应
service     -> 业务规则、事务边界、风控校验和资产计算
repository  -> JPA 数据访问层
entity      -> 持久化领域模型
dto         -> API 请求和响应模型
config      -> Security、Swagger、Redis、JPA 等基础配置
exception   -> 业务异常和全局异常处理
aspect      -> 横切逻辑，例如交易日志
security    -> JWT 生成、解析和当前用户识别
```

## 核心模块

- 用户系统：注册、登录、JWT 签发和认证。
- 行情系统：初始化模拟股票，定时生成随机涨跌，使用 Redis 缓存最新行情。
- 交易系统：买入、卖出、订单落库、成交记录落库。
- 持仓系统：维护股票数量、可卖数量、持仓均价、已实现收益。
- 风控系统：校验交易数量、交易金额、账户余额、可用持仓和股票交易状态。
- 排行榜系统：根据账户总资产和收益率生成收益排行榜。
- 前端页面：由 Spring Boot 托管静态资源，提供轻量级股票交易界面。

## 事务边界

- `TradingService.placeOrder` 是交易写入的核心事务入口。
- 买入订单会在同一事务内更新现金、持仓、订单和成交记录。
- 卖出订单会在同一事务内更新现金、持仓、订单和成交记录。
- 行情定时更新独立于交易事务，避免行情刷新影响交易链路。

## 缓存策略

- MySQL 保存股票、账户、持仓、订单、成交等核心数据。
- Redis 缓存最新行情列表，缓存键默认为 `stock:quotes`。
- 当 Redis 不可用时，行情接口会回源 MySQL，保证系统仍可演示。

## 接口文档

Swagger UI 地址：

```text
http://localhost:8080/swagger-ui.html
```
