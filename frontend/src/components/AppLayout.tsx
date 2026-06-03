import { useState, useEffect, type ReactNode } from 'react';
import { Layout, Menu, Button, Modal, Input, message, Dropdown, Space, Typography } from 'antd';
import {
  DashboardOutlined,
  SwapOutlined,
  WalletOutlined,
  TrophyOutlined,
  UserOutlined,
  LoginOutlined,
  UserAddOutlined,
  LogoutOutlined,
  StockOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { authApi, isLoggedIn, logout } from '../api';

const { Header, Content } = Layout;
const { Text } = Typography;

const navItems = [
  { key: '/', icon: <DashboardOutlined />, label: '行情' },
  { key: '/trading', icon: <SwapOutlined />, label: '交易' },
  { key: '/portfolio', icon: <WalletOutlined />, label: '资产' },
  { key: '/leaderboard', icon: <TrophyOutlined />, label: '排行榜' },
];

interface Props {
  children: ReactNode;
}

export default function AppLayout({ children }: Props) {
  const navigate = useNavigate();
  const location = useLocation();
  const [loggedIn, setLoggedIn] = useState(isLoggedIn());
  const [nickname, setNickname] = useState(localStorage.getItem('nickname') || '');
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [nick, setNick] = useState('');
  const [balance, setBalance] = useState('100000');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoggedIn(isLoggedIn());
    setNickname(localStorage.getItem('nickname') || '');
  }, [location]);

  const handleAuth = async () => {
    setLoading(true);
    try {
      if (authMode === 'login') {
        const res = await authApi.login({ username, password });
        localStorage.setItem('token', res.token);
        localStorage.setItem('nickname', res.nickname);
        setNickname(res.nickname);
        message.success(`欢迎回来，${res.nickname}`);
      } else {
        const res = await authApi.register({
          username,
          password,
          nickname: nick || username,
          initialBalance: Number(balance),
        });
        localStorage.setItem('token', res.token);
        localStorage.setItem('nickname', res.nickname);
        setNickname(res.nickname);
        message.success(`注册成功，${res.nickname}`);
      }
      setLoggedIn(true);
      setAuthOpen(false);
    } catch (err: any) {
      message.error(err.message || '操作失败');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    setLoggedIn(false);
    setNickname('');
    message.success('已退出');
    navigate('/');
  };

  const userMenu = {
    items: [
      { key: 'nick', label: nickname, disabled: true },
      { type: 'divider' as const },
      { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout },
    ],
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#0f172a' }}>
      <Header
        style={{
          background: '#1e293b',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: '1px solid #334155',
          height: 56,
          position: 'sticky',
          top: 0,
          zIndex: 100,
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 32 }}>
          <Space
            style={{ cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            <StockOutlined style={{ fontSize: 22, color: '#3b82f6' }} />
            <Text strong style={{ color: '#f1f5f9', fontSize: 17, letterSpacing: 1 }}>
              StockVue
            </Text>
          </Space>
          <Menu
            mode="horizontal"
            selectedKeys={[location.pathname]}
            items={navItems}
            onClick={({ key }) => navigate(key)}
            style={{
              background: 'transparent',
              borderBottom: 'none',
              flex: 1,
              minWidth: 400,
            }}
            theme="dark"
          />
        </div>
        <Space>
          {loggedIn ? (
            <Dropdown menu={userMenu}>
              <Button type="text" style={{ color: '#f1f5f9' }}>
                <Space>
                  <UserOutlined />
                  {nickname}
                </Space>
              </Button>
            </Dropdown>
          ) : (
            <>
              <Button
                icon={<LoginOutlined />}
                type="text"
                style={{ color: '#94a3b8' }}
                onClick={() => { setAuthMode('login'); setAuthOpen(true); }}
              >
                登录
              </Button>
              <Button
                icon={<UserAddOutlined />}
                type="primary"
                onClick={() => { setAuthMode('register'); setAuthOpen(true); }}
              >
                注册
              </Button>
            </>
          )}
        </Space>
      </Header>

      <Content style={{ padding: 16, maxWidth: 1440, margin: '0 auto', width: '100%' }}>
        {children}
      </Content>

      <Modal
        title={authMode === 'login' ? '登录' : '注册'}
        open={authOpen}
        onCancel={() => setAuthOpen(false)}
        footer={null}
        destroyOnClose
        styles={{ body: { paddingTop: 16 } }}
      >
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Input
            placeholder="用户名"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            onPressEnter={handleAuth}
            size="large"
          />
          <Input.Password
            placeholder="密码"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onPressEnter={handleAuth}
            size="large"
          />
          {authMode === 'register' && (
            <>
              <Input
                placeholder="昵称（选填）"
                value={nick}
                onChange={(e) => setNick(e.target.value)}
                size="large"
              />
              <Input
                placeholder="初始资金"
                type="number"
                value={balance}
                onChange={(e) => setBalance(e.target.value)}
                size="large"
                addonBefore="¥"
              />
            </>
          )}
          <Button
            type="primary"
            block
            size="large"
            loading={loading}
            onClick={handleAuth}
          >
            {authMode === 'login' ? '登录' : '注册并登录'}
          </Button>
          <Button
            type="link"
            block
            onClick={() => setAuthMode(authMode === 'login' ? 'register' : 'login')}
          >
            {authMode === 'login' ? '没有账号？立即注册' : '已有账号？去登录'}
          </Button>
        </Space>
      </Modal>
    </Layout>
  );
}
