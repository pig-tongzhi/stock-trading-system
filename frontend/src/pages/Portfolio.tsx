import { useState, useEffect } from 'react';
import { Card, Row, Col, Table, Statistic, Spin, Tag, Typography, Space } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, WalletOutlined, DollarOutlined, RiseOutlined, FallOutlined } from '@ant-design/icons';
import { positionApi, isLoggedIn } from '../api';
import type { AssetSummaryResponse } from '../types';

const { Text } = Typography;

export default function Portfolio() {
  const [data, setData] = useState<AssetSummaryResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn()) {
      setLoading(false);
      return;
    }
    positionApi.getAssets().then((assets) => {
        setData(assets);
      })
      .catch(() => {
        setData(null);
      })
      .finally(() => setLoading(false));
  }, []);

  if (!isLoggedIn()) {
    return (
      <Card
        style={{
          border: '1px solid #334155',
          borderRadius: 12,
          background: '#1e293b',
          textAlign: 'center',
          padding: 80,
        }}
      >
        <WalletOutlined style={{ fontSize: 48, color: '#64748b' }} />
        <Text style={{ display: 'block', color: '#64748b', marginTop: 16, fontSize: 16 }}>
          请先登录查看资产
        </Text>
      </Card>
    );
  }

  const columns = [
    {
      title: '股票',
      dataIndex: 'stockName',
      key: 'stockName',
      render: (name: string, record: any) => (
        <div>
          <Text strong style={{ color: '#f1f5f9' }}>{name}</Text>
          <br />
          <Text style={{ color: '#64748b', fontSize: 12 }}>{record.stockCode}</Text>
        </div>
      ),
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 100,
      render: (v: number) => <Text style={{ color: '#f1f5f9' }}>{v}</Text>,
    },
    {
      title: '均价',
      dataIndex: 'averageCost',
      key: 'averageCost',
      width: 120,
      align: 'right' as const,
      render: (v: number) => (
        <Text style={{ color: '#f1f5f9' }}>¥{v.toFixed(2)}</Text>
      ),
    },
    {
      title: '现价',
      dataIndex: 'latestPrice',
      key: 'latestPrice',
      width: 120,
      align: 'right' as const,
      render: (v: number) => (
        <Text style={{ color: '#f1f5f9' }}>¥{v.toFixed(2)}</Text>
      ),
    },
    {
      title: '市值',
      dataIndex: 'marketValue',
      key: 'marketValue',
      width: 120,
      align: 'right' as const,
      render: (v: number) => (
        <Text style={{ color: '#f1f5f9' }}>¥{v.toFixed(2)}</Text>
      ),
    },
    {
      title: '盈亏',
      dataIndex: 'unrealizedProfit',
      key: 'unrealizedProfit',
      width: 120,
      align: 'right' as const,
      render: (v: number) => (
        <Text style={{ color: v >= 0 ? '#22c55e' : '#ef4444' }}>
          {v >= 0 ? '+' : ''}{v.toFixed(2)}
        </Text>
      ),
    },
    {
      title: '收益率',
      key: 'profitRate',
      width: 100,
      align: 'right' as const,
      render: (_: any, record: any) => {
        const rate = record.averageCost > 0
          ? ((record.latestPrice - record.averageCost) / record.averageCost) * 100
          : 0;
        return (
          <Tag
            color={rate >= 0 ? '#22c55e' : '#ef4444'}
            style={{ borderRadius: 4, border: 'none', margin: 0 }}
          >
            {rate >= 0 ? '+' : ''}{rate.toFixed(2)}%
          </Tag>
        );
      },
    },
  ];

  return (
    <Spin spinning={loading}>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} lg={6}>
              <Card
                style={{
                  border: '1px solid #334155',
                  borderRadius: 12,
                  background: '#1e293b',
                }}
                styles={{ body: { padding: '20px 24px' } }}
              >
                <Statistic
                  title={<Text style={{ color: '#94a3b8' }}>总资产</Text>}
                  value={data?.totalAsset || 0}
                  precision={2}
                  prefix={<DollarOutlined />}
                  valueStyle={{ color: '#f1f5f9', fontSize: 28 }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card
                style={{
                  border: '1px solid #334155',
                  borderRadius: 12,
                  background: '#1e293b',
                }}
                styles={{ body: { padding: '20px 24px' } }}
              >
                <Statistic
                  title={<Text style={{ color: '#94a3b8' }}>可用资金</Text>}
                  value={data?.account.availableBalance || 0}
                  precision={2}
                  prefix={<WalletOutlined />}
                  valueStyle={{ color: '#f1f5f9', fontSize: 28 }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card
                style={{
                  border: '1px solid #334155',
                  borderRadius: 12,
                  background: '#1e293b',
                }}
                styles={{ body: { padding: '20px 24px' } }}
              >
                <Statistic
                  title={<Text style={{ color: '#94a3b8' }}>持仓市值</Text>}
                  value={data?.positionMarketValue || 0}
                  precision={2}
                  prefix={<RiseOutlined />}
                  valueStyle={{ color: '#f1f5f9', fontSize: 28 }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card
                style={{
                  border: '1px solid #334155',
                  borderRadius: 12,
                  background: '#1e293b',
                }}
                styles={{ body: { padding: '20px 24px' } }}
              >
                <Statistic
                  title={<Text style={{ color: '#94a3b8' }}>收益率</Text>}
                  value={data?.profitRate || 0}
                  precision={2}
                  suffix="%"
                  prefix={data && data.profitRate >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
                  valueStyle={{
                    color: data && data.profitRate >= 0 ? '#22c55e' : '#ef4444',
                    fontSize: 28,
                  }}
                />
              </Card>
            </Col>
          </Row>
        </Col>

        <Col span={24}>
          <Card
            title={<Text style={{ color: '#f1f5f9' }}>📦 持仓明细</Text>}
            styles={{
              header: { background: '#1e293b', borderBottom: '1px solid #334155' },
              body: { padding: 0, background: '#1e293b' },
            }}
            style={{ border: '1px solid #334155', borderRadius: 12 }}
          >
            <Table
              dataSource={data?.positions || []}
              columns={columns}
              rowKey="stockCode"
              pagination={false}
              locale={{ emptyText: <Text style={{ color: '#64748b' }}>暂无持仓</Text> }}
              style={{ background: 'transparent' }}
              className="dark-table"
            />
          </Card>
        </Col>
      </Row>
    </Spin>
  );
}
