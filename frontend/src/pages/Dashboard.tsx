import { useState, useCallback } from 'react';
import { Card, Spin, Tag, Space, Row, Col, Typography, message } from 'antd';
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined,
} from '@ant-design/icons';
import ReactECharts from 'echarts-for-react';
import { useQuotes } from '../hooks/useQuotes';
import StockSearch from '../components/StockSearch';

const { Text, Title } = Typography;

export default function Dashboard() {
  const { quotes, loading, selectedQuote, setSelectedCode, getFlash } = useQuotes();
  const [flashMap, setFlashMap] = useState<Record<string, 'up' | 'down' | null>>({});

  const handleStockSelect = useCallback(async (stock: { code: string; name: string }) => {
    try {
      await fetch('/api/market-data/add?code=' + stock.code, { method: 'POST' });
      message.success(`已添加 ${stock.name}`);
    } catch {
      message.error('添加失败');
    }
  }, []);

  const handleSelect = (code: string) => {
    setSelectedCode(code);
  };

  const now = quotes.map((q) => {
    const flash = getFlash(q.code, q.latestPrice);
    return { ...q, flash };
  });

  const option = selectedQuote
    ? {
        backgroundColor: 'transparent',
        grid: { left: 60, right: 20, top: 40, bottom: 30 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#f1f5f9' },
        },
        xAxis: {
          type: 'category',
          data: Array.from({ length: 20 }, (_, i) => `${i + 1}:30`),
          axisLabel: { color: '#64748b', fontSize: 11 },
          axisLine: { lineStyle: { color: '#334155' } },
          splitLine: { show: false },
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: '#1e293b', type: 'dashed' } },
          axisLabel: { color: '#64748b', fontSize: 11 },
        },
        series: [
          {
            type: 'line',
            data: Array.from({ length: 20 }, () =>
              Number(
                (
                  selectedQuote.latestPrice *
                  (1 + (Math.random() - 0.5) * 0.02)
                ).toFixed(2),
              ),
            ),
            smooth: true,
            showSymbol: false,
            lineStyle: { color: '#3b82f6', width: 2 },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(59,130,246,0.3)' },
                  { offset: 1, color: 'rgba(59,130,246,0.02)' },
                ],
              },
            },
          },
        ],
      }
    : {};

  return (
    <Spin spinning={loading}>
      <div style={{ marginBottom: 16 }}>
        <StockSearch onSelect={handleStockSelect} />
      </div>
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={8}>
          <Card
            title={<Text style={{ color: '#f1f5f9' }}>📋 股票列表</Text>}
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { padding: 8, background: '#1e293b' },
            }}
            style={{ border: '1px solid #334155', borderRadius: 12 }}
          >
            <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
              {now.map((q) => (
                <div
                  key={q.code}
                  onClick={() => handleSelect(q.code)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '10px 12px',
                    borderRadius: 8,
                    cursor: 'pointer',
                    background:
                      selectedQuote?.code === q.code
                        ? 'rgba(59,130,246,0.12)'
                        : 'transparent',
                    border:
                      selectedQuote?.code === q.code
                        ? '1px solid rgba(59,130,246,0.3)'
                        : '1px solid transparent',
                    transition: 'all 0.2s',
                  }}
                  onMouseEnter={(e) => {
                    if (selectedQuote?.code !== q.code) {
                      e.currentTarget.style.background = 'rgba(255,255,255,0.04)';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (selectedQuote?.code !== q.code) {
                      e.currentTarget.style.background = 'transparent';
                    }
                  }}
                >
                  <div>
                    <Text strong style={{ color: '#f1f5f9', fontSize: 14, display: 'block' }}>
                      {q.name}
                    </Text>
                    <Text style={{ color: '#64748b', fontSize: 12 }}>{q.code}</Text>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <Text
                      strong
                      style={{
                        fontSize: 15,
                        color: '#f1f5f9',
                        display: 'block',
                        transition: 'color 0.3s',
                      }}
                    >
                      {q.latestPrice.toFixed(2)}
                    </Text>
                    <Tag
                      color={q.changeRate >= 0 ? '#22c55e' : '#ef4444'}
                      style={{
                        borderRadius: 4,
                        fontSize: 12,
                        padding: '0 6px',
                        lineHeight: '20px',
                        border: 'none',
                        margin: 0,
                      }}
                    >
                      {q.changeRate >= 0 ? '+' : ''}{q.changeRate.toFixed(2)}%
                    </Tag>
                  </div>
                  <div style={{ marginLeft: 8 }}>
                    {q.flash === 'up' && (
                      <ArrowUpOutlined style={{ color: '#22c55e', fontSize: 16 }} />
                    )}
                    {q.flash === 'down' && (
                      <ArrowDownOutlined style={{ color: '#ef4444', fontSize: 16 }} />
                    )}
                    {!q.flash && (
                      <MinusOutlined style={{ color: '#64748b', fontSize: 16 }} />
                    )}
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={16}>
          <Card
            title={
              selectedQuote ? (
                <Space>
                  <Text style={{ color: '#f1f5f9', fontSize: 16, fontWeight: 600 }}>
                    {selectedQuote.name}
                  </Text>
                  <Text style={{ color: '#64748b' }}>{selectedQuote.code}</Text>
                  <Text
                    strong
                    style={{
                      fontSize: 20,
                      color: selectedQuote.changeRate >= 0 ? '#22c55e' : '#ef4444',
                    }}
                  >
                    {selectedQuote.latestPrice.toFixed(2)}
                  </Text>
                  <Tag
                    color={selectedQuote.changeRate >= 0 ? '#22c55e' : '#ef4444'}
                    style={{ borderRadius: 4, border: 'none', margin: 0 }}
                  >
                    {selectedQuote.changeRate >= 0 ? '+' : ''}
                    {selectedQuote.changeRate.toFixed(2)}%
                  </Tag>
                </Space>
              ) : (
                <Text style={{ color: '#f1f5f9' }}>📈 走势图</Text>
              )
            }
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { padding: 0, background: '#1e293b' },
            }}
            style={{
              border: '1px solid #334155',
              borderRadius: 12,
              height: '100%',
            }}
          >
            {selectedQuote ? (
              <ReactECharts
                option={option}
                style={{ height: 420, width: '100%' }}
                theme="dark"
              />
            ) : (
              <div
                style={{
                  height: 420,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: '#64748b',
                }}
              >
                请选择一只股票
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </Spin>
  );
}
