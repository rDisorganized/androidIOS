# Tính Công Việc — Android (Java)

Ứng dụng tính khối lượng công việc và thời gian làm việc. Dữ liệu theo từng tài khoản trên Firebase.

## Vì sao file YML không chạy được

GitHub **chỉ** nhận workflow ở đúng đường dẫn:

```text
<repo-root>/.github/workflows/build-apk.yml
```

Sai (workflow biến mất, tab Actions trống):

```text
WorkerWorkload/.github/workflows/build-apk.yml
zip-goc/WorkerWorkload/.github/workflows/build-apk.yml
```

Cách đẩy đúng: giải nén zip, upload **các file bên trong** (settings.gradle, app/, .github/, gradlew...) làm **root** của repo, không bọc thêm thư mục `WorkerWorkload`.

Nhánh phải là `main` hoặc `master`, hoặc bấm **Run workflow** (workflow_dispatch).

Cũng sửa các lỗi build thường gặp:

- Không gọi lệnh `gradle` toàn cục (runner GitHub không có sẵn)
- Cài SDK `platforms;android-34` + build-tools 34
- Bắt buộc có `gradlew` + `gradle/wrapper/gradle-wrapper.jar`

## Load dữ liệu (đã tối ưu)

- `WorkRepository`: `limitToLast(100)`, `keepSynced`, gỡ listener ở `onStop`
- `ListAdapter` + DiffUtil: không `notifyDataSetChanged` cả list
- RecyclerView riêng (không nhét trong NestedScrollView) nên chỉ bind item đang hiện
- Offline cache: `setPersistenceEnabled` trong `WorkloadApp`
- Rules có `.indexOn: ["timestamp"]`

## Mở trên Android Studio

Open thư mục có `settings.gradle` → Sync (Gradle 8.4, AGP 8.3.2, JDK 17) → Run.

## Firebase

1. Bật Authentication Email/Password
2. Dán rules trong `database.rules.json`
3. Thêm app Android package `com.example.workerworkload`, thay `app/google-services.json`
