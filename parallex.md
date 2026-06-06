# Parallax Background Implementation Plan

Mục tiêu: Thay thế background tĩnh hiện tại bằng một hệ thống Parallax Background (Nhiều lớp) để tạo hiệu ứng chiều sâu 3D (các lớp chuyển động với tốc độ khác nhau dựa trên chuyển động của Camera). Do dự án của bạn có một `CameraController` tuỳ chỉnh với tính năng Dynamic Zoom, việc thiết kế Parallax cũng cần tính toán tỉ lệ scale phù hợp để không bị lỗi đồ hoạ khi zoom in/out.

## User Review Required

> [!IMPORTANT]  
> Vui lòng xem các câu hỏi dưới đây trước khi duyệt kế hoạch. Các quyết định này sẽ ảnh hưởng trực tiếp đến code.

## Open Questions

1. **Thứ tự xa - gần của các layer**: Trong các file như `14.png`, `13.png`, `12.png`, `11.png`, file nào là lớp xa nhất (ví dụ: bầu trời, di chuyển chậm nhất) và file nào là lớp gần nhất (di chuyển nhanh nhất)? (Ví dụ: 14 là lớp xa nhất, 11 là lớp gần nhất?)
2. **Kích thước ảnh layer**: Các file ảnh layer hiện tại có kích thước bằng nhau không? Chúng có kích thước nhỏ hơn, bằng hay lớn hơn kích thước của Map (Tiled map dimensions)?
3. **Trục Parallax**: Bạn muốn hiệu ứng di chuyển Parallax diễn ra trên cả trục X (ngang) và Y (dọc), hay chỉ trục X? (Thường thì Parallax chủ yếu di chuyển theo chiều ngang, chiều dọc chỉ di chuyển rất nhẹ hoặc giữ nguyên).
4. **Cách tiếp cận**: FXGL có một class `ParallaxBackgroundView` có sẵn, tuy nhiên do bạn có dùng Dynamic Zoom trong `CameraController`, hệ thống mặc định của FXGL có thể bị lỗi vỡ hình khi zoom. Tôi đề xuất chúng ta tự viết một **`ParallaxComponent`** tuỳ chỉnh gắn vào các Entity layer để kiểm soát chính xác vị trí và scale của chúng theo Camera của bạn. Bạn đồng ý với hướng này chứ?

## Hướng làm (Approach)

1. **Quản lý danh sách Background**: Thay đổi Enum `MapRegistry` thay vì lưu một đường dẫn duy nhất `bgPath`, ta sẽ lưu tiền tố của map (ví dụ: `1` cho map 1, `2` cho map 2) và số lượng layer (ví dụ: 4).
2. **Tạo Component tuỳ chỉnh `ParallaxComponent`**:
   - Component này sẽ nhận vào 2 tham số: tọa độ gốc của background, và `parallaxFactor` (tỉ lệ tốc độ di chuyển, ví dụ bầu trời = 0.0 tức là không di chuyển so với màn hình, núi xa = 0.3, rừng gần = 0.8).
   - Trong hàm `onUpdate(tpf)`, Component sẽ đọc viewport (`getGameScene().getViewport().getX()` và `Y()`) và tính toán lại toạ độ X, Y của Entity để tạo độ lệch so với camera chính.
3. **Sửa đổi `MapLoader.loadMap`**:
   - Xoá tạo Entity `bgPath` cũ.
   - Thêm một vòng lặp duyệt qua các số từ 1 đến `layerCount`.
   - Với mỗi layer, load texture với định dạng `maps/{mapPrefix}{layerIndex}.png` (ví dụ `maps/14.png`).
   - Tạo Entity gắn `ParallaxComponent` với mức `parallaxFactor` tính toán tự động (hoặc chỉ định rõ), và sắp xếp `zIndex` (từ âm sâu hơn đến gần hơn).

## Proposed Changes

---

### Map Registry (Data)

Sẽ cập nhật cấu trúc để hỗ trợ nhiều layer thay vì một file duy nhất.

#### [MODIFY] [MapRegistry.java](file:///d:/gamemake/fighter67/src/main/java/com/nhom67/platformfighter/map/MapRegistry.java)
- Cập nhật enum để cấu hình `mapPrefix` và `layerCount` cho MAP_1 và MAP_2 thay cho `bgPath`.

---

### Core Mechanics (Component)

Tạo logic xử lý hiệu ứng Parallax tương thích với Camera Controller.

#### [NEW] [ParallaxComponent.java](file:///d:/gamemake/fighter67/src/main/java/com/nhom67/platformfighter/entity/component/ParallaxComponent.java)
- Component gắn vào Entity `EntityType.BACKGROUND` để tự động offset vị trí theo tọa độ Camera và `parallaxFactor`.

---

### Map Initialization (Loader)

Khởi tạo các thực thể nền tại thời điểm load map.

#### [MODIFY] [MapLoader.java](file:///d:/gamemake/fighter67/src/main/java/com/nhom67/platformfighter/map/MapLoader.java)
- Khởi tạo các lớp background thành các entity khác nhau trong vòng lặp và gắn `ParallaxComponent`.

## Verification Plan

### Manual Verification
1. Chạy game và load MAP_1.
2. Di chuyển các nhân vật lại gần/ra xa nhau để trigger `CameraController` zoom in/out và pan camera.
3. Quan sát các lớp nền phía sau: lớp ở xa phải di chuyển chậm hơn lớp ở gần màn hình, đồng thời không bị cắt viền hoặc lộ background đen.
