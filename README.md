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

## Play Store Closed Testing용 Release AAB 빌드

### 현재 앱 ID

```text
com.kmw1wlog.phonestockapp
```

이 값은 Play Console에 업로드한 뒤 바꾸지 않습니다.

### 현재 WebView URL

```text
https://stock-app-mu-three.vercel.app/
```

### 필요한 GitHub Secrets

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

### AAB 빌드 방법

1. GitHub Repository `Settings`로 이동합니다.
2. `Secrets and variables` → `Actions`로 이동합니다.
3. 위 4개 secrets를 등록합니다.
4. `Actions` → `Build Release AAB` workflow를 실행합니다.
5. artifact `phone-stock-app-release-aab`를 다운로드합니다.
6. 압축 해제 후 `app-release.aab`를 Play Console closed testing에 업로드합니다.

### versionCode 규칙

AAB를 새로 업로드할 때마다 `versionCode`를 반드시 올립니다.

예시:

- `0.1.0` / `versionCode 1`: 최초 closed testing
- `0.1.1` / `versionCode 2`: 피드 UI 수정
- `0.1.2` / `versionCode 3`: 알림 UX 수정
- `0.2.0` / `versionCode 10`: worker/data 연동

### Windows PowerShell release 빌드 예시

```powershell
npm install
npx cap sync android
cd "android"
.\gradlew bundleRelease
```

### Keystore 생성과 Secrets 등록

keystore 파일은 레포에 커밋하지 않습니다.

PowerShell 기준 예시:

```powershell
keytool -genkeypair -v -keystore "release-keystore.jks" -storetype JKS -keyalg RSA -keysize 2048 -validity 10000 -alias "phone-stock-app"
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Content "release-keystore.base64"
```

GitHub Secrets:

- `ANDROID_KEYSTORE_BASE64`: `release-keystore.base64` 파일 내용
- `ANDROID_KEYSTORE_PASSWORD`: keystore 생성 시 입력한 store password
- `ANDROID_KEY_ALIAS`: `phone-stock-app`
- `ANDROID_KEY_PASSWORD`: keystore 생성 시 입력한 key password

보안 주의:

- `release-keystore.jks`
- `release-keystore.base64`
- keystore 비밀번호

이 값들은 절대 GitHub repo에 커밋하지 말고, 로컬 PC와 별도 백업 저장소에 보관합니다.

## Not Included In This Phase

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
