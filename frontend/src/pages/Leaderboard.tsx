import { useState, useEffect } from 'react';
import { Card, List, Tag, Typography, Spin, Space, Avatar } from 'antd';
import { TrophyOutlined, CrownOutlined, GoldOutlined, ArrowUpOutlined, ArrowDownOutlined, MinusOutlined } from '@ant-design/icons';
import { leaderboardApi } from '../api';
import type { LeaderboardItem } from '../types';

const { Text, Title } = Typography;

const rankColors: Record<number, string> = {
  1: '#ffd700',
  2: '#c0c0c0',
  3: '#cd7f32',
};

const rankIcons: Record<number, React.ReactNode> = {
  1: <CrownOutlined style={{ color: '#ffd700', fontSize: 20 }} />,
  2: <GoldOutlined style={{ color: '#c0c0c0', fontSize: 18 }} />,
  3: <GoldOutlined style={{ color: '#cd7f32', fontSize: 16 }} />,
};

export default function Leaderboard() {
  const [data, setData] = useState<LeaderboardItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    leaderboardApi
      .getLeaderboard(10)
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  return (
    <Spin spinning={loading}>
      <Card
        title={
          <Space>
            <TrophyOutlined style={{ color: '#ffd700', fontSize: 20 }} />
            <Text style={{ color: '#f1f5f9', fontSize: 16, fontWeight: 600 }}>
              收益排行榜
            </Text>
          </Space>
        }
        styles={{
          header: { background: '#1e293b', borderBottom: '1px solid #334155' },
          body: { padding: 0, background: '#1e293b' },
        }}
        style={{
          border: '1px solid #334155',
          borderRadius: 12,
          maxWidth: 800,
          margin: '0 auto',
        }}
      >
        <List
          dataSource={data}
          renderItem={(item, index) => (
            <List.Item
              style={{
                padding: '14px 20px',
                borderBottom: index < data.length - 1 ? '1px solid #1e293b' : 'none',
                transition: 'background 0.2s',
                cursor: 'pointer',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = 'rgba(255,255,255,0.03)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = 'transparent';
              }}
            >
              <Space size="middle" style={{ width: '100%', justifyContent: 'space-between' }}>
                <Space size="middle">
                  <div
                    style={{
                      width: 36,
                      height: 36,
                      borderRadius: 8,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      background: rankColors[index + 1]
                        ? `${rankColors[index + 1]}22`
                        : 'rgba(255,255,255,0.06)',
                      border: rankColors[index + 1]
                        ? `1px solid ${rankColors[index + 1]}44`
                        : '1px solid transparent',
                    }}
                  >
                    {rankIcons[index + 1] || (
                      <Text
                        strong
                        style={{
                          color: index + 1 <= 3 ? rankColors[index + 1] : '#64748b',
                          fontSize: 16,
                        }}
                      >
                        {index + 1}
                      </Text>
                    )}
                  </div>
                  <Avatar
                    size={40}
                    style={{
                      background: rankColors[index + 1] || '#334155',
                      verticalAlign: 'middle',
                      fontSize: 16,
                      fontWeight: 700,
                    }}
                  >
                    {item.nickname.charAt(0).toUpperCase()}
                  </Avatar>
                  <div>
                    <Text strong style={{ color: '#f1f5f9', fontSize: 15, display: 'block' }}>
                      {item.nickname}
                    </Text>
                    <Text style={{ color: '#64748b', fontSize: 12 }}>
                      总资产 ¥{item.totalAsset.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </Text>
                  </div>
                </Space>

                <Space size="large">
                  <div style={{ textAlign: 'right' }}>
                    <Text style={{ color: '#64748b', fontSize: 11, display: 'block' }}>收益</Text>
                    <Text
                      strong
                      style={{
                        color: item.totalProfit >= 0 ? '#22c55e' : '#ef4444',
                        fontSize: 15,
                      }}
                    >
                      {item.totalProfit >= 0 ? '+' : ''}
                      {item.totalProfit.toFixed(2)}
                    </Text>
                  </div>
                  <Tag
                    color={item.profitRate >= 0 ? '#22c55e' : '#ef4444'}
                    style={{
                      borderRadius: 6,
                      border: 'none',
                      fontSize: 14,
                      fontWeight: 700,
                      padding: '4px 14px',
                    }}
                  >
                    <Space size={4}>
                      {item.profitRate >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
                      {item.profitRate >= 0 ? '+' : ''}{item.profitRate.toFixed(2)}%
                    </Space>
                  </Tag>
                </Space>
              </Space>
            </List.Item>
          )}
          locale={{ emptyText: <Text style={{ color: '#64748b', padding: 40, display: 'block', textAlign: 'center' }}>暂无排行数据</Text> }}
        />
      </Card>
    </Spin>
  );
}
