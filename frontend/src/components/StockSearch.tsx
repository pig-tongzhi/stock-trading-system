import { useState, useEffect, useRef, useMemo } from 'react';
import { AutoComplete, Input } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { pinyin } from 'pinyin-pro';
import type { StockQuote } from '../types';
import { marketApi } from '../api';

interface StockMaster {
  code: string;
  name: string;
}

let cachedStocks: StockMaster[] | null = null;
let fetchPromise: Promise<StockMaster[]> | null = null;

function loadAllStocks(): Promise<StockMaster[]> {
  if (cachedStocks) return Promise.resolve(cachedStocks);
  if (fetchPromise) return fetchPromise;
  fetchPromise = fetch('/api/stocks/all')
    .then((r) => r.json())
    .then((r) => {
      cachedStocks = r.data || [];
      return cachedStocks;
    });
  return fetchPromise;
}

function getPinyinInitials(name: string): string {
  return pinyin(name, { pattern: 'first', toneType: 'none' });
}

function matchStock(s: StockMaster, q: string): boolean {
  const lower = q.toLowerCase();
  if (s.code.includes(lower)) return true;
  if (s.name.includes(q)) return true;
  const initials = getPinyinInitials(s.name);
  return initials.includes(lower);
}

interface StockSearchProps {
  onSelect?: (stock: StockMaster) => void;
  placeholder?: string;
}

export default function StockSearch({ onSelect, placeholder = '搜股票（代码/名称/拼音）' }: StockSearchProps) {
  const [options, setOptions] = useState<{ label: string; value: string; stock: StockMaster }[]>([]);
  const [inputValue, setInputValue] = useState('');
  const allStocksRef = useRef<StockMaster[]>([]);

  useEffect(() => {
    loadAllStocks().then((list) => {
      allStocksRef.current = list;
    });
  }, []);

  const handleSearch = (value: string) => {
    setInputValue(value);
    if (!value.trim()) {
      setOptions([]);
      return;
    }
    const q = value.trim();
    const all = allStocksRef.current;
    const matched = all.filter((s) => matchStock(s, q)).slice(0, 50);
    setOptions(
      matched.map((s) => ({
        label: `${s.code} ${s.name}`,
        value: s.code,
        stock: s,
      }))
    );
  };

  const handleSelect = (_: string, option: { stock: StockMaster }) => {
    setInputValue('');
    setOptions([]);
    onSelect?.(option.stock);
  };

  return (
    <AutoComplete
      value={inputValue}
      options={options}
      onSearch={handleSearch}
      onSelect={handleSelect}
      onChange={setInputValue}
      style={{ width: '100%' }}
    >
      <Input
        prefix={<SearchOutlined style={{ color: '#64748b' }} />}
        placeholder={placeholder}
        style={{
          background: '#0f172a',
          border: '1px solid #334155',
          borderRadius: 8,
          color: '#f1f5f9',
          height: 40,
        }}
      />
    </AutoComplete>
  );
}
