# 🚀 Hướng dẫn đẩy lên GitHub & Build APK tự động

## Bước 1 — Tải gradle-wrapper.jar

File `gradle/wrapper/gradle-wrapper.jar` phải có trong repo.
Cách nhanh nhất: tạo project mới trong Android Studio, nó sẽ tự sinh file này.

**Hoặc chạy lệnh sau để tải về:**
```bash
mkdir -p gradle/wrapper
curl -Lo gradle/wrapper/gradle-wrapper.jar \
  "https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar"
```

---

## Bước 2 — Tạo Keystore để ký APK (chỉ làm 1 lần)

```bash
keytool -genkey -v \
  -keystore my-release-key.jks \
  -alias banphimviet \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Điền thông tin khi được hỏi. Ghi nhớ:
- `keystore password` (mật khẩu keystore)
- `key alias` = `banphimviet`
- `key password` (mật khẩu key)

---

## Bước 3 — Chuyển Keystore sang Base64

```bash
# macOS / Linux
base64 -i my-release-key.jks | pbcopy   # macOS (copy vào clipboard)
base64 -w 0 my-release-key.jks           # Linux (in ra terminal)
```

---

## Bước 4 — Thêm Secrets vào GitHub

Vào repo GitHub → **Settings → Secrets and variables → Actions → New repository secret**

Thêm 4 secrets sau:

| Secret Name        | Giá trị                          |
|--------------------|----------------------------------|
| `KEYSTORE_BASE64`  | Chuỗi base64 từ bước 3           |
| `KEYSTORE_PASSWORD`| Mật khẩu keystore                |
| `KEY_ALIAS`        | `banphimviet`                    |
| `KEY_PASSWORD`     | Mật khẩu key                     |

---

## Bước 5 — Push lên GitHub

```bash
cd VietnameseKeyboard

# Khởi tạo git
git init
git add .
git commit -m "feat: Vietnamese keyboard IME with emoji support"

# Tạo repo trên GitHub rồi push
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/BanPhimViet.git
git push -u origin main
```

→ GitHub Actions sẽ **tự động chạy**, build xong ~3-5 phút.

---

## Bước 6 — Tải APK về

1. Vào repo GitHub → tab **Actions**
2. Click vào workflow run mới nhất
3. Cuộn xuống phần **Artifacts**
4. Tải `BanPhimViet-debug-xxx` hoặc `BanPhimViet-release-xxx`

---

## Tạo Release chính thức (có file APK đính kèm)

```bash
git tag v1.0.0
git push origin v1.0.0
```

→ GitHub tự tạo Release với APK đã ký đính kèm tại tab **Releases**.

---

## Cấu trúc Workflow

```
push/PR → main
    │
    ├── assembleDebug   → APK debug (không cần ký)
    │       └── Upload artifact: BanPhimViet-debug-{run_number}.apk
    │
    └── assembleRelease → APK release
            ├── Ký bằng Keystore (từ Secrets)
            └── Upload artifact: BanPhimViet-release-{run_number}.apk

push tag v*.*.*
    └── Tạo GitHub Release với APK đã ký
```
