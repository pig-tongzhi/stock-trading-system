# 数据库设计

## 表结构说明

### users

用户表，保存登录用户信息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| username | varchar(64) | 用户名，唯一 |
| password | varchar(255) | BCrypt 加密后的密码 |
| nickname | varchar(128) | 用户昵称 |
| role | varchar(32) | 用户角色 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### accounts

资金账户表，保存模拟交易账户。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| user_id | bigint | 所属用户 ID |
| account_name | varchar(128) | 账户名称 |
| available_balance | decimal(19,4) | 可用资金 |
| frozen_balance | decimal(19,4) | 冻结资金，预留给挂单场景 |
| initial_asset | decimal(19,4) | 初始资产 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### stocks

股票表，保存股票基础信息和最新行情。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | varchar(16) | 股票代码 |
| name | varchar(128) | 股票名称 |
| latest_price | decimal(19,4) | 最新价格 |
| previous_close | decimal(19,4) | 上一轮价格 |
| change_rate | decimal(10,4) | 当前涨跌幅 |
| trading_enabled | bit | 是否允许交易 |
| updated_at | datetime | 行情更新时间 |

### positions

持仓表，保存账户当前股票持仓。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| account_id | bigint | 账户 ID |
| stock_code | varchar(16) | 股票代码 |
| stock_name | varchar(128) | 股票名称 |
| quantity | int | 当前持仓数量 |
| available_quantity | int | 可卖数量 |
| average_cost | decimal(19,4) | 持仓均价 |
| realized_profit | decimal(19,4) | 已实现收益 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`(account_id, stock_code)`。

### trade_orders

订单表，保存交易订单生命周期。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| account_id | bigint | 账户 ID |
| user_id | bigint | 用户 ID |
| stock_code | varchar(16) | 股票代码 |
| stock_name | varchar(128) | 股票名称 |
| side | varchar(8) | 交易方向，BUY / SELL |
| status | varchar(16) | 订单状态 |
| price | decimal(19,4) | 委托价格 |
| quantity | int | 委托数量 |
| amount | decimal(19,4) | 委托金额 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### trade_records

成交记录表，保存已成交交易流水。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| order_id | bigint | 订单 ID |
| account_id | bigint | 账户 ID |
| user_id | bigint | 用户 ID |
| stock_code | varchar(16) | 股票代码 |
| stock_name | varchar(128) | 股票名称 |
| side | varchar(8) | 交易方向，BUY / SELL |
| price | decimal(19,4) | 成交价格 |
| quantity | int | 成交数量 |
| amount | decimal(19,4) | 成交金额 |
| traded_at | datetime | 成交时间 |

## DDL 文件

建表 SQL 见：`src/main/resources/schema.sql`。
