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
2. Android Studio → **Open** → chọn thư mục `WorkerWorkload` (thư mục có `settings.gradle`)
3. Đợi Gradle Sync (Gradle **8.4**, AGP **8.3.2**, JDK **17**)
4. Run app

## Build APK trên GitHub (khuyến nghị)
Đẩy **nội dung thư mục `WorkerWorkload`** lên repo (file `settings.gradle` phải nằm ở root repo, hoặc trong 1 thư mục con — workflow sẽ tự dò).

Workflow: `.github/workflows/build-apk.yml`

- Nhánh: `main` / `master` (push, pull request) hoặc chạy tay **workflow_dispatch**
- Lệnh: `./gradlew assembleDebug`
- Tải APK tại tab **Actions** → artifact `jobfinder-debug-apk`

## Cấu hình Firebase
1. Firebase Console → project **project-doidoi**
2. Authentication → Sign-in method → bật **Email/Password**
3. Realtime Database → Rules (file `database.rules.json`):

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

4. Thêm app Android, package: `com.example.workerworkload`
5. Thay `app/google-services.json` bằng file tải từ Console

## Stack build
| Thành phần | Phiên bản |
|---|---|
| JDK | 17 |
| Gradle | 8.4 |
| Android Gradle Plugin | 8.3.2 |
| compileSdk / targetSdk | 34 |
| minSdk | 24 |
| Package | `com.example.workerworkload` |
