import { useState, useEffect, useMemo } from 'react';
import { Card, Row, Col, Input, Button, Tabs, Statistic, message, Spin, Tag, Space, Typography, Divider } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, SwapOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons';
import ReactECharts from 'echarts-for-react';
import { useQuotes } from '../hooks/useQuotes';
import { tradingApi, isLoggedIn } from '../api';

const { Text, Title } = Typography;

export default function Trading() {
  const { quotes, loading: quotesLoading, selectedCode, setSelectedCode, selectedQuote, getFlash } = useQuotes();
  const [side, setSide] = useState<'BUY' | 'SELL'>('BUY');
  const [price, setPrice] = useState('');
  const [quantity, setQuantity] = useState(100);
  const [placing, setPlacing] = useState(false);
  const [balance, setBalance] = useState(0);

  useEffect(() => {
    if (selectedQuote) {
      setPrice(selectedQuote.latestPrice.toFixed(2));
    }
  }, [selectedQuote]);

  useEffect(() => {
    if (isLoggedIn()) {
      fetch('/api/accounts/me', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
        .then((r) => r.json())
        .then((d) => {
          if (d.success) setBalance(d.data.availableBalance);
        })
        .catch(() => {});
    }
  }, []);

  const totalAmount = useMemo(() => {
    const p = parseFloat(price) || 0;
    return p * quantity;
  }, [price, quantity]);

  const valid = useMemo(() => {
    const p = parseFloat(price) || 0;
    if (p <= 0 || quantity <= 0) return false;
    if (side === 'BUY' && totalAmount > balance) return false;
    return true;
  }, [price, quantity, side, totalAmount, balance]);

  const handlePlace = async () => {
    if (!isLoggedIn()) {
      message.warning('请先登录');
      return;
    }
    if (!selectedCode) {
      message.warning('请选择股票');
      return;
    }
    setPlacing(true);
    try {
      const res = await tradingApi.placeOrder({
        stockCode: selectedCode,
        side,
        price: parseFloat(price),
        quantity,
      });
      message.success(`订单已成交！#${res.orderId}`);
      setQuantity(100);
    } catch (err: any) {
      message.error(err.message || '下单失败');
    } finally {
      setPlacing(false);
    }
  };

  const priceHistory = useMemo(() => {
    if (!selectedQuote) return [];
    const base = selectedQuote.latestPrice;
    return Array.from({ length: 30 }, (_, i) => ({
      value: base * (1 + (Math.random() - 0.5) * 0.06),
      name: `${9 + Math.floor(i / 2)}:${i % 2 === 0 ? '00' : '30'}`,
    }));
  }, [selectedQuote?.latestPrice]);

  const chartOption = selectedQuote
    ? {
        backgroundColor: 'transparent',
        grid: { left: 50, right: 20, top: 30, bottom: 30 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#f1f5f9' },
        },
        xAxis: {
          type: 'category',
          data: priceHistory.map((p) => p.name),
          axisLabel: { color: '#64748b', fontSize: 10 },
          axisLine: { lineStyle: { color: '#334155' } },
          splitLine: { show: false },
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: '#1e293b', type: 'dashed' } },
          axisLabel: { color: '#64748b', fontSize: 11 },
        },
        visualMap: {
          show: false,
          pieces: [
            { min: 0, max: 99999, color: '#22c55e' },
          ],
          calculable: false,
        },
        series: [
          {
            type: 'candlestick',
            data: priceHistory.map((p) => {
              const open = p.value * (1 + (Math.random() - 0.5) * 0.008);
              const close = p.value * (1 + (Math.random() - 0.5) * 0.008);
              const high = Math.max(open, close) * (1 + Math.random() * 0.006);
              const low = Math.min(open, close) * (1 - Math.random() * 0.006);
              return [open.toFixed(2), close.toFixed(2), low.toFixed(2), high.toFixed(2)];
            }),
            itemStyle: {
              color: '#22c55e',
              color0: '#ef4444',
              borderColor: '#22c55e',
              borderColor0: '#ef4444',
            },
          },
        ],
      }
    : {};

  const quoteList = quotes
    .map((q) => {
      const flash = getFlash(q.code, q.latestPrice);
      return { ...q, flash };
    });

  return (
    <Spin spinning={quotesLoading}>
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={5}>
          <Card
            title={<Text style={{ color: '#f1f5f9' }}>📋 自选</Text>}
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { padding: 8, background: '#1e293b' },
            }}
            style={{ border: '1px solid #334155', borderRadius: 12, height: '100%' }}
          >
            <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
              {quoteList.map((q) => (
                <div
                  key={q.code}
                  onClick={() => setSelectedCode(q.code)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '8px 10px',
                    borderRadius: 6,
                    cursor: 'pointer',
                    background:
                      selectedCode === q.code
                        ? 'rgba(59,130,246,0.12)'
                        : 'transparent',
                    border:
                      selectedCode === q.code
                        ? '1px solid rgba(59,130,246,0.3)'
                        : '1px solid transparent',
                    transition: 'all 0.15s',
                  }}
                >
                  <div>
                    <Text style={{ color: '#f1f5f9', fontSize: 13, fontWeight: 600, display: 'block' }}>
                      {q.name}
                    </Text>
                    <Text style={{ color: '#64748b', fontSize: 11 }}>{q.code}</Text>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <Text style={{ color: q.changeRate >= 0 ? '#22c55e' : '#ef4444', fontSize: 13, fontWeight: 700, display: 'block' }}>
                      {q.latestPrice.toFixed(2)}
                    </Text>
                    <Text style={{ color: '#64748b', fontSize: 11 }}>
                      {q.changeRate >= 0 ? '+' : ''}{q.changeRate.toFixed(2)}%
                    </Text>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { padding: 0, background: '#1e293b' },
            }}
            style={{ border: '1px solid #334155', borderRadius: 12 }}
          >
            {selectedQuote ? (
              <>
                <div style={{ padding: '16px 20px 0' }}>
                  <Space align="baseline">
                    <Title level={4} style={{ color: '#f1f5f9', margin: 0 }}>
                      {selectedQuote.name}
                    </Title>
                    <Text style={{ color: '#64748b' }}>{selectedQuote.code}</Text>
                    <Text
                      strong
                      style={{
                        fontSize: 28,
                        color: selectedQuote.changeRate >= 0 ? '#22c55e' : '#ef4444',
                      }}
                    >
                      {selectedQuote.latestPrice.toFixed(2)}
                    </Text>
                    <Tag
                      color={selectedQuote.changeRate >= 0 ? '#22c55e' : '#ef4444'}
                      style={{ borderRadius: 4, border: 'none', fontSize: 13, padding: '2px 10px' }}
                    >
                      {selectedQuote.changeRate >= 0 ? '+' : ''}
                      {selectedQuote.changeRate.toFixed(2)}%
                    </Tag>
                  </Space>
                </div>
                <ReactECharts
                  option={chartOption}
                  style={{ height: 380, width: '100%' }}
                  theme="dark"
                />
              </>
            ) : (
              <div style={{ height: 440, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#64748b' }}>
                请从左侧选择一只股票
              </div>
            )}
          </Card>
        </Col>

        <Col xs={24} lg={7}>
          <Card
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { background: '#1e293b' },
            }}
            style={{ border: '1px solid #334155', borderRadius: 12 }}
          >
            <Tabs
              activeKey={side}
              onChange={(k) => setSide(k as 'BUY' | 'SELL')}
              items={[
                {
                  key: 'BUY',
                  label: (
                    <Space>
                      <ArrowUpOutlined style={{ color: '#22c55e' }} />
                      <span style={{ color: '#22c55e' }}>买入</span>
                    </Space>
                  ),
                },
                {
                  key: 'SELL',
                  label: (
                    <Space>
                      <ArrowDownOutlined style={{ color: '#ef4444' }} />
                      <span style={{ color: '#ef4444' }}>卖出</span>
                    </Space>
                  ),
                },
              ]}
              style={{ marginBottom: 8 }}
            />

            {selectedQuote && (
              <>
                <Space align="baseline" style={{ marginBottom: 16 }}>
                  <Text style={{ color: '#94a3b8' }}>当前价</Text>
                  <Text
                    strong
                    style={{
                      fontSize: 28,
                      color: selectedQuote.changeRate >= 0 ? '#22c55e' : '#ef4444',
                    }}
                  >
                    {selectedQuote.latestPrice.toFixed(2)}
                  </Text>
                </Space>

                <Divider style={{ borderColor: '#334155', margin: '12px 0' }} />

                <div style={{ marginBottom: 12 }}>
                  <Text style={{ color: '#94a3b8', display: 'block', marginBottom: 6 }}>价格</Text>
                  <Input
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                    size="large"
                    prefix="¥"
                    type="number"
                    step={0.01}
                  />
                </div>

                <div style={{ marginBottom: 12 }}>
                  <Text style={{ color: '#94a3b8', display: 'block', marginBottom: 6 }}>数量</Text>
                  <Input
                    value={quantity}
                    onChange={(e) => setQuantity(Number(e.target.value) || 0)}
                    size="large"
                    type="number"
                    min={1}
                    step={100}
                    addonAfter={
                      <Space size={4}>
                        <Button
                          size="small"
                          type="text"
                          icon={<MinusOutlined />}
                          onClick={() => setQuantity(Math.max(1, quantity - 100))}
                          style={{ color: '#94a3b8' }}
                        />
                        <Button
                          size="small"
                          type="text"
                          icon={<PlusOutlined />}
                          onClick={() => setQuantity(quantity + 100)}
                          style={{ color: '#94a3b8' }}
                        />
                      </Space>
                    }
                  />
                </div>

                <div
                  style={{
                    background: 'rgba(255,255,255,0.04)',
                    borderRadius: 8,
                    padding: '12px 16px',
                    marginBottom: 16,
                  }}
                >
                  <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                    <Text style={{ color: '#94a3b8' }}>交易金额</Text>
                    <Text strong style={{ color: '#f1f5f9', fontSize: 18 }}>
                      ¥{totalAmount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </Text>
                  </Space>
                  <Space style={{ width: '100%', justifyContent: 'space-between', marginTop: 6 }}>
                    <Text style={{ color: '#94a3b8' }}>可用资金</Text>
                    <Text style={{ color: '#f1f5f9' }}>
                      ¥{balance.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </Text>
                  </Space>
                  {side === 'BUY' && totalAmount > balance && (
                    <Text style={{ color: '#ef4444', fontSize: 12, marginTop: 4, display: 'block' }}>
                      余额不足，还差 ¥{(totalAmount - balance).toFixed(2)}
                    </Text>
                  )}
                </div>

                <Button
                  type="primary"
                  size="large"
                  block
                  loading={placing}
                  disabled={!valid}
                  onClick={handlePlace}
                  style={{
                    height: 48,
                    fontSize: 16,
                    fontWeight: 700,
                    background: side === 'BUY' ? '#22c55e' : '#ef4444',
                    borderColor: side === 'BUY' ? '#22c55e' : '#ef4444',
                  }}
                >
                  {side === 'BUY' ? '买入' : '卖出'}
                </Button>
              </>
            )}

            {!selectedQuote && (
              <div style={{ textAlign: 'center', color: '#64748b', padding: 40 }}>
                请从左侧选择股票
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </Spin>
  );
}
