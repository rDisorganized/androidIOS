# Tính Công Việc — Android (Java)

Ứng dụng tính **khối lượng công việc** và **thời gian làm việc** của công nhân.
Dữ liệu lưu trên Firebase Realtime Database theo từng tài khoản.

## Tính năng
- Đăng ký / đăng nhập (Firebase Auth – Email/Password)
- Nhập tên công nhân, loại việc, khối lượng, đơn vị, số giờ
- Tự tính năng suất = khối lượng / thời gian
- Lưu lịch sử vào `users/{uid}/works`
- Xem lịch sử, nhấn giữ để xóa
- Đăng xuất

## Mở project trên Android Studio
1. Giải nén file zip
2. Mở Android Studio → **Open** → chọn thư mục `WorkerWorkload`
3. Đợi Gradle Sync
4. Nếu chưa có `gradlew`, Android Studio sẽ tự tạo Gradle Wrapper

## Cấu hình Firebase (bắt buộc để chạy thật)
1. Vào [Firebase Console](https://console.firebase.google.com/) → project **project-doidoi**
2. **Authentication** → Sign-in method → bật **Email/Password**
3. **Realtime Database** → Rules, dán:

```json
{
  "rules": {
    "users": {
      "$user_id": {
        ".read": "$user_id === auth.uid",
        ".write": "$user_id === auth.uid"
      }
    }
  }
}
```

4. Thêm ứng dụng **Android** với package: `com.example.workerworkload`
5. Tải `google-services.json` mới và **thay** file `app/google-services.json`

> File `google-services.json` trong zip dùng cấu hình hiện có (project `project-doidoi`). Nên đăng ký app Android trong Console để tránh lỗi App ID.

## Build APK trên GitHub
1. Tạo repo GitHub, đẩy toàn bộ source
2. Vào **Actions** → workflow **Build APK**
3. Tải artifact `app-release`

Hoặc trên máy: Android Studio → **Build → Build Bundle(s) / APK(s) → Build APK(s)**

## Package
`com.example.workerworkload`
