import type { CapacitorConfig } from '@capacitor/cli';

const appUrl = process.env.STOCK_APP_WEB_URL || 'https://stock-app-mu-three.vercel.app/';
const isHttps = appUrl.startsWith('https://');

if (!isHttps) {
  throw new Error('STOCK_APP_WEB_URL must start with https://');
}

const config: CapacitorConfig = {
  appId: 'com.kmw1wlog.phonestockapp',
  appName: 'Phone Stock App',
  webDir: 'www',
  server: {
    url: appUrl,
    cleartext: false,
    androidScheme: 'https',
  },
  android: {
    allowMixedContent: false,
    captureInput: true,
    webContentsDebuggingEnabled: true,
  },
};

export default config;
