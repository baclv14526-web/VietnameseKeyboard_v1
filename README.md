# ⌨️ Bàn Phím Tiếng Việt (Vietnamese Smart Keyboard)

Bàn phím IME cho Android với đầy đủ tiếng Việt, dấu thanh và emoji dễ thương.

---

## 🚀 Cách mở project trong Android Studio

1. Mở **Android Studio** → **File → Open**
2. Chọn thư mục `VietnameseKeyboard`
3. Đợi Gradle sync xong (~1-2 phút)
4. Nhấn **Run ▶** hoặc Build APK

---

## 📁 Cấu trúc Project

```
VietnameseKeyboard/
├── app/src/main/
│   ├── java/com/viet/keyboard/
│   │   ├── VietnameseIME.java           ← Service chính IME
│   │   ├── VietnameseKeyboardManager.java ← Xây dựng layout bàn phím
│   │   └── SetupActivity.java           ← Màn hình hướng dẫn cài đặt
│   ├── res/
│   │   ├── layout/
│   │   │   ├── keyboard_view.xml        ← Layout bàn phím
│   │   │   └── activity_setup.xml      ← Layout màn hình setup
│   │   ├── xml/method.xml               ← Khai báo IME
│   │   └── drawable/key_bg_rounded.xml  ← Nền phím bo góc
│   └── AndroidManifest.xml
```

---

## ✨ Tính năng

### 🇻🇳 Tiếng Việt đầy đủ
- **29 chữ cái** tiếng Việt đầy đủ
- Chữ cái đặc biệt: **ă, â, đ, ê, ô, ơ, ư** (có trong hàng dấu thanh)
- Phím **Shift** để gõ chữ hoa

### 🎵 Dấu Thanh (6 thanh)
- **´ Sắc** (màu đỏ)
- **` Huyền** (màu xanh ngọc)
- **~ Ngã** (màu vàng)
- **? Hỏi** (màu xanh lá nhạt)
- **. Nặng** (màu hồng)
- **— Ngang** (không dấu)

### 🔢 Dãy Số
- Số 1-9 và 0 ở hàng trên cùng
- Nút **123** để tắt/hiển số

### 😊 Emoji Panel
7 danh mục emoji:
- 😊 Vui vẻ
- 😢 Cảm xúc
- 🌸 Thiên nhiên
- 🍜 Đồ ăn
- ❤️ Trái tim
- 🎵 Hoạt động
- 👍 Chat

### 🌙 Giao diện
- Chủ đề tối (dark mode) hiện đại
- Màu sắc gradient đẹp
- Animation khi nhấn phím
- Phông nền trong suốt

---

## 📲 Cách cài đặt sau khi build APK

1. Cài APK vào điện thoại
2. Mở app → Nhấn **"Mở Cài Đặt"**
3. Bật **"Bàn Phím Việt"** trong danh sách
4. Quay lại app → Nhấn **"Chọn Bàn Phím"**
5. Chọn **"Bàn Phím Việt"** → Xong! ✅

---

## 🛠 Yêu cầu

- **Android Studio** Hedgehog 2023.1.1 trở lên
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Java 8**

---

## 💡 Mở rộng thêm

Bạn có thể mở rộng project này với:
- Gõ kiểu **Telex** hay **VNI** tự động
- **Autocorrect** và gợi ý từ
- **Themes** sáng/tối
- **Haptic feedback** khi gõ
- **Âm thanh** khi nhấn phím
- Thêm **sticker** dễ thương

---

*Made with ❤️ for Vietnam 🇻🇳*
