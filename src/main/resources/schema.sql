CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_name VARCHAR(128) NOT NULL,
    available_balance DECIMAL(19, 4) NOT NULL,
    frozen_balance DECIMAL(19, 4) NOT NULL,
    initial_asset DECIMAL(19, 4) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_accounts_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS stocks (
    code VARCHAR(16) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    latest_price DECIMAL(19, 4) NOT NULL,
    previous_close DECIMAL(19, 4) NOT NULL,
    change_rate DECIMAL(10, 4) NOT NULL,
    trading_enabled BIT NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS positions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    stock_code VARCHAR(16) NOT NULL,
    stock_name VARCHAR(128) NOT NULL,
    quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    average_cost DECIMAL(19, 4) NOT NULL,
    realized_profit DECIMAL(19, 4) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_positions_account_stock (account_id, stock_code)
);

CREATE TABLE IF NOT EXISTS trade_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(16) NOT NULL,
    stock_name VARCHAR(128) NOT NULL,
    side VARCHAR(8) NOT NULL,
    status VARCHAR(16) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS trade_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(16) NOT NULL,
    stock_name VARCHAR(128) NOT NULL,
    side VARCHAR(8) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    traded_at DATETIME NOT NULL
);
