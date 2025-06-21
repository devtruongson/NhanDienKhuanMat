# Hướng dẫn cài đặt và khắc phục lỗi build

## Vấn đề hiện tại
Lỗi build xảy ra do thiếu Java Development Kit (JDK). Đây là các bước để khắc phục:

## Bước 1: Cài đặt Java Development Kit (JDK)

### Tùy chọn 1: Cài đặt JDK 17 (Khuyến nghị)
1. Tải JDK 17 từ Oracle hoặc OpenJDK:
   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17
   - OpenJDK: https://adoptium.net/temurin/releases/?version=17

2. Cài đặt JDK 17
3. Thiết lập biến môi trường:
   - Mở "System Properties" → "Environment Variables"
   - Thêm biến mới `JAVA_HOME` với giá trị đường dẫn JDK (ví dụ: `C:\Program Files\Java\jdk-17`)
   - Thêm `%JAVA_HOME%\bin` vào biến `PATH`

### Tùy chọn 2: Sử dụng Android Studio's embedded JDK
Nếu bạn đã cài đặt Android Studio, nó có sẵn JDK:
1. Mở Android Studio
2. Vào File → Project Structure
3. Trong SDK Location, copy đường dẫn JDK
4. Thiết lập `JAVA_HOME` với đường dẫn này

## Bước 2: Kiểm tra cài đặt

Mở Command Prompt hoặc PowerShell và chạy:
```bash
java -version
javac -version
echo %JAVA_HOME%
```

## Bước 3: Build project

Sau khi cài đặt Java, chạy:
```bash
./gradlew clean
./gradlew build
```

## Bước 4: Chạy ứng dụng

```bash
./gradlew installDebug
```

## Các lỗi thường gặp và cách khắc phục

### Lỗi 1: JAVA_HOME is not set
**Nguyên nhân:** Chưa thiết lập biến môi trường JAVA_HOME
**Giải pháp:** Làm theo Bước 1

### Lỗi 2: Plugin conflict với kotlin-kapt
**Nguyên nhân:** Xung đột phiên bản plugin
**Giải pháp:** Đã được sửa bằng cách chuyển sang KSP

### Lỗi 3: Camera permissions
**Nguyên nhân:** Thiết bị không có camera hoặc chưa cấp quyền
**Giải pháp:** 
- Đảm bảo thiết bị có camera
- Cấp quyền camera khi được yêu cầu

### Lỗi 4: ML Kit dependencies
**Nguyên nhân:** Thiếu Google Play Services
**Giải pháp:** 
- Đảm bảo thiết bị có Google Play Services
- Hoặc sử dụng emulator với Google Play Services

## Cấu hình Android Studio

1. Mở project trong Android Studio
2. Đồng bộ Gradle files
3. Đảm bảo SDK và build tools được cài đặt
4. Chọn thiết bị hoặc emulator
5. Chạy ứng dụng

## Tài khoản demo

Sau khi chạy thành công, sử dụng các tài khoản demo:

- **Admin:** admin@example.com / password
- **User:** user@example.com / password

## Hỗ trợ

Nếu gặp vấn đề khác, hãy kiểm tra:
1. Logs trong Android Studio
2. Gradle build output
3. Thiết bị logs 