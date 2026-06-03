# stock-trading-system

`stock-trading-system` 是一个基于 Java 17 和 Spring Boot 3 的金融模拟股票交易系统，适合用于 Java 后端面试展示。项目覆盖用户认证、股票行情、交易一致性、持仓资产、风控、Redis 缓存、AOP 交易日志、Swagger 接口文档，以及一个简洁的股票交易前端页面。

## 技术栈

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Security + JWT
- Spring Data JPA
- Spring Data Redis
- Spring Validation
- Spring AOP
- MySQL
- Redis
- Lombok
- Maven
- Swagger / springdoc-openapi

## 项目结构

```text
src/main/java/com/example/stocktradingsystem
├── aspect        # AOP 切面，例如交易日志
├── config        # Security、Swagger、JPA 等配置
├── controller    # REST API 控制层
├── dto           # 请求和响应 DTO
├── entity        # JPA 实体和领域枚举
├── exception     # 业务异常和全局异常处理
├── repository    # Spring Data JPA 数据访问层
├── security      # JWT 生成、解析和当前用户识别
└── service       # 业务接口和实现
```

更多设计说明：

- `docs/ARCHITECTURE.md`
- `docs/DATABASE_DESIGN.md`
- `src/main/resources/schema.sql`

## 功能清单

- 用户注册和登录，登录后签发 JWT
- 注册后自动创建模拟资金账户
- 股票行情定时模拟，价格每轮随机波动 `-5% ~ +5%`
- Redis 缓存最新行情，Redis 不可用时回源 MySQL
- 买入和卖出接口，使用 `@Transactional` 保证一致性
- 账户余额、持仓、订单、成交记录同事务更新
- 持仓均价、浮动盈亏、已实现收益计算
- 风控校验：交易金额、交易数量、账户余额、可用持仓、股票可交易状态
- 收益排行榜
- AOP 记录交易成功和失败日志
- 统一 API 返回结构
- Swagger 接口文档
- 静态前端交易台

## 快速启动

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE stock_trading_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 启动本地 Redis，默认地址为 `localhost:6379`。

3. 修改 `src/main/resources/application.yml` 中的 MySQL 用户名和密码。

4. 启动应用：

```bash
mvn spring-boot:run
```

访问地址：

- 前端页面：`http://localhost:8080`
- Swagger：`http://localhost:8080/swagger-ui.html`
- 健康检查：`http://localhost:8080/actuator/health`

## 核心接口

### 用户认证

- `POST /api/auth/register`
- `POST /api/auth/login`

### 股票行情

- `GET /api/market-data/quotes`
- `POST /api/market-data/refresh`

### 股票交易

- `POST /api/trading/orders`
- `DELETE /api/trading/orders/{orderId}`

### 账户与持仓

- `GET /api/accounts/me`
- `POST /api/accounts`
- `GET /api/positions`
- `GET /api/positions/assets`

### 收益排行榜

- `GET /api/leaderboard?limit=10`

## 演示流程

1. 注册用户并设置初始资金。
2. 查看行情列表并选择股票。
3. 提交买入订单。
4. 查看资产和持仓变化。
5. 卖出部分持仓。
6. 查看收益排行榜。

## 面试讲解重点

- 交易事务边界放在 `TradingServiceImpl.placeOrder`。
- Redis 用作行情缓存，MySQL 是最终数据源。
- 风控逻辑独立在 `RiskControlService`，便于扩展更多交易限制。
- JWT 认证是无状态的，由 Spring Security Filter 统一处理。
- AOP 记录交易日志，避免污染核心业务代码。
- 前端保持轻量，让面试重点集中在后端架构和业务一致性。
