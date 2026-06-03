import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider, theme } from 'antd';
import App from './App';

const darkTheme = {
  algorithm: theme.darkAlgorithm,
  token: {
    colorPrimary: '#3b82f6',
    colorBgBase: '#0f172a',
    colorBgContainer: '#1e293b',
    colorBgElevated: '#1e293b',
    colorBorderSecondary: '#334155',
    colorTextBase: '#f1f5f9',
    colorTextSecondary: '#94a3b8',
    borderRadius: 8,
    fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
  },
};

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider theme={darkTheme}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ConfigProvider>
  </React.StrictMode>,
);
