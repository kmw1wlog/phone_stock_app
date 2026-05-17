# Phone Stock App

`phone_stock_app` is a Capacitor Android wrapper for testing the mobile web `stock_app` on a real Android phone.

## What This Repo Does

- wraps the deployed `stock_app` web app in an Android WebView
- builds a testable debug APK
- uploads the APK as a GitHub Actions artifact

This repo does not copy the `stock_app` codebase.

## Connected Web App URL

```text
https://stock-app-mu-three.vercel.app/
```

This is the `stock_app` Vercel Production URL and is the default `STOCK_APP_WEB_URL`.

## stock_app and phone_stock_app

- `stock_app`: Next.js web app deployed on Vercel
- `phone_stock_app`: Android wrapper only

The Android app opens `stock_app` in a secure HTTPS WebView.

## Local Setup

```bash
npm install
npx cap sync android
npx cap open android
```

Local debug APK build prerequisites:

- Node `24.x`
- JDK `21`
- Android SDK with platform/build tools installed

Windows PowerShell example:

```powershell
npm install
npx cap sync android
npx cap open android
cd "android"
.\gradlew assembleDebug
```

## GitHub Actions APK Download

1. Open the repository `Actions` tab.
2. Run `Build Debug APK`.
3. Keep the default `stock_app_web_url`:

```text
https://stock-app-mu-three.vercel.app/
```

4. After the workflow succeeds, download the artifact:

```text
phone-stock-app-debug-apk
```

5. Unzip it and install `app-debug.apk` on the Android phone.

## Android Install Guide

- this is a test `debug APK`, not a Play Store app
- Android may show a security warning for sideloaded apps
- allow `Install unknown apps` for the browser or file manager you use
- after installation, it is safer to disable that permission again

## Test Checklist

```text
[설치]
- APK 다운로드 가능
- APK 설치 가능
- 앱 아이콘 표시
- 앱 실행 가능

[첫 화면]
- 앱 실행 후 5초 안에 홈 화면 표시
- 카드 피드 표시
- 화면이 흰 화면에서 멈추지 않음
- 하단/상단 영역이 상태바나 내비게이션바에 가려지지 않음

[저장]
- 관심종목 추가 버튼 동작
- 저장 상태가 UI에 반영
- 앱 종료 후 재실행해도 저장 상태 유지 여부 확인
- 단, DB 미연결 fallback 상태에서는 서버 영구 저장이 아닐 수 있음을 표시

[조건식 알림]
- 홈 카드에서 “이 조건 알림 받기” 클릭 가능
- 알림 설정 modal 표시
- 알림 저장 API 응답 확인
- /alerts 화면 진입 가능
- fallback mode에서는 실제 영구 저장이 아닐 수 있음
- 실제 push notification은 이번 APK 범위에 포함하지 않음

[API/데이터]
- /api/cards/feed?mode=fast 정상 응답
- /api/live-signals 정상 응답
- /api/live-alert-triggers 정상 응답
- /api/cron/live-runtime-sync 정상 응답
- 네트워크가 느려도 기본 오류 화면으로 죽지 않음

[앱 UX]
- Android 뒤로가기 정상
- 스크롤 자연스러움
- 상세 화면 진입 가능
- 외부 링크 클릭 시 먹통 없음
- 개인정보처리방침/투자 유의사항 접근 가능

[주의]
- 현재 APK는 debug APK
- Play Store 제출용은 추후 release AAB
- 실제 Android push notification은 이번 범위에 포함하지 않음
- KIS 실시간 worker는 Vercel/phone_stock_app이 아니라 별도 서버에서 처리 예정
```

## APK and AAB

- `debug APK`: direct device testing
- `release APK`: manual distribution after signing
- `release AAB`: Play Store submission format

Current scope is the debug APK only.

## Not Included In This Phase

- Play Store release
- release signing
- Android push notifications
- DB-backed permanent alert storage
- KIS realtime worker
- order execution

## Security Notes

Do not commit:

- `.env`
- keystore files
- broker or trading secrets
- `DATABASE_URL`
- KIS credentials
- Kiwoom credentials

Never expose KIS or broker secrets as `NEXT_PUBLIC_*`.
