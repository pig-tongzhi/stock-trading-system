import { useState, useEffect, useCallback, useRef } from 'react';
import { marketApi } from '../api';
import type { StockQuote } from '../types';

export function useQuotes(intervalMs = 5000) {
  const [quotes, setQuotes] = useState<StockQuote[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCode, setSelectedCode] = useState<string | null>(null);
  const prevRef = useRef<Map<string, number>>(new Map());

  const fetch = useCallback(async () => {
    try {
      const data = await marketApi.getQuotes();
      setQuotes(data);
      if (!selectedCode && data.length > 0) {
        setSelectedCode(data[0].code);
      }
    } catch {
      // silent
    } finally {
      setLoading(false);
    }
  }, [selectedCode]);

  useEffect(() => {
    fetch();
    const timer = setInterval(fetch, intervalMs);
    return () => clearInterval(timer);
  }, [fetch, intervalMs]);

  const selectedQuote = quotes.find((q) => q.code === selectedCode) || null;

  const getFlash = (code: string, price: number): 'up' | 'down' | null => {
    const prev = prevRef.current.get(code);
    prevRef.current.set(code, price);
    if (prev === undefined) return null;
    if (price > prev) return 'up';
    if (price < prev) return 'down';
    return null;
  };

  return { quotes, loading, selectedCode, setSelectedCode, selectedQuote, getFlash };
}
