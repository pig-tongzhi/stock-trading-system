export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface StockQuote {
  code: string;
  name: string;
  latestPrice: number;
  previousClose: number;
  changeRate: number;
  tradingEnabled: boolean;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  nickname: string;
}

export interface AccountInfo {
  id: number;
  userId: number;
  accountName: string;
  availableBalance: number;
  frozenBalance: number;
  initialAsset: number;
}

export interface Position {
  stockCode: string;
  stockName: string;
  quantity: number;
  availableQuantity: number;
  averageCost: number;
  latestPrice: number;
  marketValue: number;
  unrealizedProfit: number;
  profitRate: number;
}

export interface AssetSummary {
  account: AccountInfo;
  totalAsset: number;
  positionMarketValue: number;
  profitRate: number;
  positions: Position[];
}

export interface AssetSummaryResponse {
  account: AccountInfo;
  positionMarketValue: number;
  totalAsset: number;
  profitRate: number;
  positions: Position[];
}

export interface TradeOrder {
  orderId: number;
}

export interface LeaderboardItem {
  rank: number;
  nickname: string;
  totalAsset: number;
  totalProfit: number;
  profitRate: number;
}
