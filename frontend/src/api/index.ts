import type { ApiResponse, StockQuote, AuthResponse, AssetSummaryResponse, TradeOrder, LeaderboardItem } from '../types';

const BASE = '/api';

function getToken(): string {
  return localStorage.getItem('token') || '';
}

export function isLoggedIn(): boolean {
  return !!getToken();
}

export function logout(): void {
  localStorage.removeItem('token');
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  const res = await fetch(`${BASE}${path}`, { ...options, headers });
  const json: ApiResponse<T> = await res.json();
  if (!json.success) {
    throw new Error(json.message || 'Request failed');
  }
  return json.data;
}

export const authApi = {
  register: (data: { username: string; password: string; nickname: string; initialBalance?: number }) =>
    request<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  login: (data: { username: string; password: string }) =>
    request<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
};

export const marketApi = {
  getQuotes: () => request<StockQuote[]>('/market-data/quotes'),
};

export const tradingApi = {
  placeOrder: (data: { stockCode: string; side: 'BUY' | 'SELL'; price: number; quantity: number }) =>
    request<TradeOrder>('/trading/orders', { method: 'POST', body: JSON.stringify(data) }),
};

export const accountApi = {
  getAccount: () => request<{ id: number; userId: number; accountName: string; availableBalance: number; frozenBalance: number; initialAsset: number }>('/accounts/me'),
};

export const positionApi = {
  getAssets: () => request<AssetSummaryResponse>('/positions/assets'),
};

export const leaderboardApi = {
  getLeaderboard: (limit = 10) => request<LeaderboardItem[]>(`/leaderboard?limit=${limit}`),
};
