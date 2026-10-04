M4X STORE Android Loader

1) CLOUDFLARE
- Repo M4X-key của bạn phải có D1 binding tên DB.
- Copy file:
    cloudflare/functions/verify.js
  vào repo Cloudflare thành:
    functions/verify.js
- Commit và chờ deployment xanh.
- Endpoint loader dùng:
    https://m4x-key.pages.dev/verify

2) ANDROID
- Mở thư mục project bằng Android Studio.
- Sync Gradle.
- Build > Build APK(s).
- APK debug nằm trong app/build/outputs/apk/debug/.

3) HOẠT ĐỘNG
- App lấy ANDROID_ID.
- Gửi key + device_id tới /verify.
- Key lần đầu được bind với thiết bị.
- Key hết hạn, bị thu hồi hoặc dùng trên thiết bị khác sẽ bị từ chối.

LƯU Ý
- Đây là loader độc lập. Nó không tiêm/sửa PUBG hoặc PAK.
- ADMIN_PASSWORD/ADMIN_USERNAME không nằm trong APK.
