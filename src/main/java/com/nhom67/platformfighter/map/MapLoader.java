package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.nhom67.platformfighter.entity.EntityType;
import com.almasb.fxgl.texture.Texture;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MapLoader {
    
    // ✅ Inner class để return map dimensions
    public static class MapDimensions {
        public double width;
        public double height;
        
        public MapDimensions(double w, double h) {
            this.width = w;
            this.height = h;
        }
    }

    public static MapDimensions loadMap(MapRegistry map) {
        // Load TMX level
        Level level = getAssetLoader().loadLevel(map.getTmxFile(), new TMXLevelLoader());
        getGameWorld().setLevel(level);

        double mapWidth = level.getWidth();
        double mapHeight = level.getHeight();

        // Parallax Background Loading
        int numLayers = map.getLayerCount();
        String mapPrefix = map.name().replace("MAP_", ""); // Lấy ra số "1", "2"...

        // Dùng nguyên kích thước của map (1920x1080) để ảnh không bị phóng to làm mờ/mất tự nhiên
        double bgWidth = mapWidth;
        double bgHeight = mapHeight;
        
        // Bắt đầu vẽ từ tọa độ 0, 0
        double startX = 0;
        double startY = 0;

        for (int b = 1; b <= numLayers; b++) {
            // b=1 là lớp gần nhất (foreground), b=numLayers là lớp xa nhất (mây/bầu trời).
            // Tỷ lệ chuyển động:
            // Lớp xa nhất (bầu trời) ratioX càng lớn (gần 1.0) -> di chuyển theo camera gần nhất -> dường như ít trôi hơn so với màn hình.
            // Lớp gần nhất (mặt đất) ratioX = 0 -> gắn cố định với map, trôi qua nhanh cùng map.
            double ratioX = (numLayers > 1) ? (double)(b - 1) * (0.9 / (numLayers - 1)) : 0.0;
            // Chiều dọc di chuyển nhẹ
            double ratioY = ratioX * 0.2;
            
            // Lớp mây xa nhất (b = numLayers) sẽ trôi nhè nhẹ
            double autoScrollX = (b == numLayers) ? 10.0 : 0.0;
            
            // zIndex: Lớp b càng nhỏ (gần nhất) thì phải đè lên trên (zIndex cao hơn)
            // Lớp b càng lớn (xa nhất) thì phải nằm dưới (zIndex thấp hơn)
            int zIndex = -100 - b;
            
            String textureName = "maps/" + mapPrefix + b + ".png";
            
            // Dùng 2 ảnh nối tiếp nhau để tạo thành cuộn ngang vô tận (wrap)
            Texture t1 = texture(textureName, bgWidth, bgHeight);
            Texture t2 = texture(textureName, bgWidth, bgHeight);
            t2.setTranslateX(bgWidth); // Ảnh 2 nối vào đuôi ảnh 1
            
            javafx.scene.Group viewGroup = new javafx.scene.Group(t1, t2);
            
            entityBuilder()
                    .at(startX, startY)
                    .type(EntityType.BACKGROUND)
                    .view(viewGroup)
                    .with(new com.nhom67.platformfighter.entity.component.ParallaxComponent(ratioX, ratioY, autoScrollX, bgWidth))
                    .zIndex(zIndex)
                    .buildAndAttach();
        }

        // Không dùng setBounds của FXGL vì CameraController đã tự clamp bounds (tránh conflict gây giật)

        // Sinh kill zones dựa trên kích thước map đúng
        MapFactory.generateKillZones(mapWidth, mapHeight);
        
        // ✅ RETURN map dimensions
        return new MapDimensions(mapWidth, mapHeight);
    }
}