# Ứng dụng Điểm danh Khuôn mặt

Ứng dụng Android được xây dựng bằng Kotlin và Google ML Kit để điểm danh bằng nhận diện khuôn mặt với 2 vai trò: Admin và User.

## Tính năng chính

### 🔐 Xác thực người dùng
- Đăng nhập với email và mật khẩu
- Phân quyền Admin và User
- Quản lý phiên đăng nhập

### 📸 Nhận diện khuôn mặt
- Sử dụng Google ML Kit Face Detection và Face Recognition
- Camera real-time để chụp ảnh khuôn mặt
- Đánh giá chất lượng khuôn mặt trước khi xử lý
- So sánh khuôn mặt với dữ liệu đã lưu trữ

### ⏰ Điểm danh tự động
- Check-in/Check-out tự động dựa trên nhận diện khuôn mặt
- Theo dõi thời gian làm việc
- Phân loại trạng thái: Đúng giờ, Muộn, Vắng mặt
- Lưu trữ lịch sử điểm danh

### 📊 Quản lý dữ liệu
- Lưu trữ thông tin người dùng và khuôn mặt
- Theo dõi lịch sử điểm danh
- Thống kê điểm danh theo ngày

## Công nghệ sử dụng

- **Kotlin** - Ngôn ngữ lập trình chính
- **Jetpack Compose** - UI framework
- **Google ML Kit** - Face Detection và Face Recognition
- **CameraX** - Camera API
- **Room Database** - Local database
- **Hilt** - Dependency injection
- **Navigation Compose** - Navigation
- **Coroutines & Flow** - Asynchronous programming

## Cấu trúc dự án

```
app/src/main/java/com/example/nhandienkhuanmat/
├── data/
│   ├── local/           # Room database và DAOs
│   ├── model/           # Data models
│   └── repository/      # Repository classes
├── di/                  # Hilt dependency injection
├── domain/
│   ├── service/         # Business services
│   └── usecase/         # Use cases
├── presentation/
│   ├── components/      # Reusable UI components
│   ├── screens/         # UI screens
│   └── viewmodel/       # ViewModels
└── ui/theme/            # UI theme
```

## Cài đặt và chạy

### Yêu cầu hệ thống
- Android Studio Arctic Fox trở lên
- Android SDK 24+ (API level 24)
- Kotlin 1.8+
- Google Play Services

### Bước 1: Clone dự án
```bash
git clone <repository-url>
cd NhanDienKhuanMat
```

### Bước 2: Cấu hình
1. Mở project trong Android Studio
2. Đồng bộ Gradle files
3. Đảm bảo có Google Play Services trên thiết bị/emulator

### Bước 3: Chạy ứng dụng
1. Kết nối thiết bị Android hoặc khởi động emulator
2. Nhấn "Run" trong Android Studio
3. Cấp quyền camera khi được yêu cầu

## Tài khoản demo

Ứng dụng được khởi tạo với 2 tài khoản demo:

### Admin
- **Email:** admin@example.com
- **Mật khẩu:** password
- **Quyền:** Quản lý toàn bộ hệ thống

### User
- **Email:** user@example.com
- **Mật khẩu:** password
- **Quyền:** Điểm danh và xem thống kê cá nhân

## Hướng dẫn sử dụng

### Đăng nhập
1. Mở ứng dụng
2. Nhập email và mật khẩu
3. Nhấn "Đăng nhập"

### Điểm danh
1. Chọn tab "Điểm danh"
2. Đặt khuôn mặt vào khung hình camera
3. Nhấn nút "Chụp"
4. Hệ thống sẽ tự động nhận diện và điểm danh

### Lưu ý khi sử dụng
- Đảm bảo ánh sáng đủ sáng
- Khuôn mặt phải rõ ràng và không bị che khuất
- Giữ khoảng cách phù hợp với camera
- Đợi hệ thống xử lý hoàn tất trước khi chụp tiếp

## Tính năng nâng cao

### Admin Dashboard (Đang phát triển)
- Quản lý danh sách người dùng
- Xem thống kê điểm danh tổng hợp
- Xuất báo cáo điểm danh
- Cài đặt hệ thống

### User Dashboard (Đang phát triển)
- Xem lịch sử điểm danh cá nhân
- Thống kê thời gian làm việc
- Cập nhật thông tin cá nhân

## Xử lý lỗi

### Lỗi thường gặp

1. **"Không phát hiện khuôn mặt"**
   - Kiểm tra ánh sáng
   - Đảm bảo khuôn mặt trong khung hình
   - Thử lại với góc khác

2. **"Chất lượng khuôn mặt không tốt"**
   - Cải thiện ánh sáng
   - Giữ khuôn mặt ổn định
   - Tránh chuyển động nhanh

3. **"Khuôn mặt không được nhận diện"**
   - Cần đăng ký khuôn mặt trước
   - Liên hệ admin để được hỗ trợ

## Đóng góp

Để đóng góp vào dự án:

1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## Giấy phép

Dự án này được phát hành dưới giấy phép MIT.

## Liên hệ

Nếu có câu hỏi hoặc góp ý, vui lòng tạo issue trên GitHub repository. 