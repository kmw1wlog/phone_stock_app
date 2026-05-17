# Play Store Closed Testing

`phone_stock_app`은 `stock_app`의 Vercel 배포 URL을 여는 Android WebView wrapper입니다. 이번 문서는 Google Play Console closed testing을 시작하기 위한 최소 release AAB 절차를 정리합니다.

## 현재 기준

- applicationId: `com.kmw1wlog.phonestockapp`
- WebView URL: `https://stock-app-mu-three.vercel.app/`
- 현재 release AAB는 closed testing 시작용 최소 버전
- 실제 push notification은 아직 포함하지 않음
- DB 기반 영구 알림 저장은 아직 포함하지 않음
- KIS 실시간 worker는 별도 서버에서 처리함

## Debug APK와 Release AAB 차이

- `debug APK`: 폰에 직접 설치해 빠르게 테스트하는 용도
- `release AAB`: Google Play Console에 업로드하는 용도
- release AAB는 signing이 필수이며, unsigned 산출물로 closed testing을 진행하지 않음

## 왜 Closed Testing을 먼저 하는가

- 앱 심사 전 실제 설치 흐름을 빠르게 점검할 수 있음
- versionCode를 올리며 WebView wrapper와 모바일 UX를 반복 개선할 수 있음
- worker/KIS/DB 연동은 후속 versionCode 업데이트로 분리 가능함

## AAB 생성 절차

1. GitHub repository secrets에 signing 정보를 등록합니다.
2. `Build Release AAB` workflow를 수동 실행합니다.
3. artifact `phone-stock-app-release-aab`를 다운로드합니다.
4. 압축 해제 후 `app-release.aab`를 Play Console closed testing에 업로드합니다.

필요한 GitHub Secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

## Keystore 생성과 Secrets 등록

PowerShell 기준 예시:

```powershell
keytool -genkeypair -v -keystore "release-keystore.jks" -storetype JKS -keyalg RSA -keysize 2048 -validity 10000 -alias "phone-stock-app"
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Content "release-keystore.base64"
```

GitHub Secrets 등록:

- `ANDROID_KEYSTORE_BASE64`: `release-keystore.base64` 파일 내용
- `ANDROID_KEYSTORE_PASSWORD`: keystore 생성 시 입력한 store password
- `ANDROID_KEY_ALIAS`: `phone-stock-app`
- `ANDROID_KEY_PASSWORD`: keystore 생성 시 입력한 key password

절대 커밋하지 않는 파일:

- `release-keystore.jks`
- `release-keystore.base64`
- `key.properties`
- `.env`

## Play Console 업로드 절차

1. Google Play Console에서 앱을 생성합니다.
2. 앱 ID는 `com.kmw1wlog.phonestockapp`으로 유지합니다.
3. `Closed testing` 트랙을 엽니다.
4. release AAB를 업로드합니다.
5. 테스터 그룹과 테스터 메일을 등록합니다.
6. 출시 노트를 입력하고 검토를 진행합니다.

## versionCode 증가 규칙

Play Console에 새 AAB를 올릴 때마다 `versionCode`는 반드시 이전보다 커야 합니다.

현재 기준:

- `versionCode 1`
- `versionName 0.1.0`

운영 규칙:

- `0.1.0` / `1`: 최초 closed testing
- `0.1.1` / `2`: 피드 UI 수정
- `0.1.2` / `3`: 알림 UX 수정
- `0.2.0` / `10`: worker/data 연동

## 앱 ID를 바꾸면 안 되는 이유

- Play Console에 등록된 앱 식별자는 applicationId를 기준으로 유지됩니다.
- 한 번 등록한 뒤 applicationId를 바꾸면 별도 앱으로 취급될 수 있습니다.
- 브랜드명 변경과 applicationId 변경은 같은 문제가 아닙니다. 이름이 바뀌어도 `com.kmw1wlog.phonestockapp`은 유지합니다.

## Secrets를 앱이나 NEXT_PUBLIC에 넣으면 안 되는 이유

- `KIS_APP_KEY`, `KIS_APP_SECRET`, broker secret은 앱에 포함되면 추출될 수 있습니다.
- `NEXT_PUBLIC_*` 값은 클라이언트에 노출됩니다.
- `phone_stock_app`은 wrapper일 뿐이며, 민감한 연동은 별도 서버에서 처리해야 합니다.

## 비공개 테스트 중 업데이트 원칙

- 피드 UI와 wrapper UX 개선은 `versionCode`를 올려 반복 배포합니다.
- worker/KIS 연동도 별도 서버 구성이 끝난 뒤 새 `versionCode`로 반영합니다.
- 앱은 결과 조회와 WebView wrapper 역할에 집중합니다.

## Play Console 업로드 전 체크리스트

```text
[앱 기술]
- applicationId: com.kmw1wlog.phonestockapp
- app-release.aab 생성 완료
- versionCode가 이전보다 큼
- release signing 적용됨
- WebView URL이 https://stock-app-mu-three.vercel.app/로 열림
- Android 폰에서 debug APK 검수 완료

[스토어 기본]
- 앱 이름 임시/최종 결정
- 앱 아이콘 준비
- 스크린샷 준비
- 짧은 설명 준비
- 긴 설명 준비
- 개인정보처리방침 URL 준비
- 투자 유의사항 페이지 준비

[정책]
- 수익 보장 표현 없음
- 매수/매도 강제 추천 표현 없음
- 투자 판단 책임 고지 있음
- 데이터 지연/오류 가능성 고지 있음
- KIS/브로커 secret이 앱에 포함되지 않음
```
