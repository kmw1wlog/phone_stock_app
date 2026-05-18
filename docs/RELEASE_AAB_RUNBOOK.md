# Release AAB Runbook

## Current Target

- app label: `급등주 for you`
- applicationId: `com.kmw1wlog.phonestockapp`
- WebView URL: `https://stock-app-mu-three.vercel.app/`
- stock_app Production commit: `57b584f`
- versionName: `0.1.1`
- versionCode: `2`

## Required Secrets

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

If any of these are missing, `Build Release AAB` should fail. That is expected and safer than building an unsigned artifact by mistake.

## Workflow

1. Push the wrapper changes to `main`.
2. Wait for `Build Release AAB` on `main`, or run it manually with `workflow_dispatch`.
3. Download artifact `phone-stock-app-release-aab`.
4. Extract `android/app/build/outputs/bundle/release/app-release.aab`.
5. Upload it to Google Play Console closed testing.

## Play Console Notes

- Use the same `applicationId`.
- Keep increasing `versionCode` for every new AAB.
- Web UI updates from `stock_app` do not require a new AAB.
- Native wrapper changes do require a new AAB.
- Do not describe the app as guaranteed returns or buy/sell advice.

## Security

Never commit:

- `release-keystore.jks`
- `release-keystore.base64`
- `key.properties`
- broker or KIS secrets
- any `NEXT_PUBLIC_*` secret values
