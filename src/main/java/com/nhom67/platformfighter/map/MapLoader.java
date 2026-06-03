package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.nhom67.platformfighter.entity.EntityType;
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

        // Background entity
        entityBuilder()
                .type(EntityType.BACKGROUND)
                .view(map.getBgPath())
                .zIndex(-100)
                .buildAndAttach();

        // Không dùng setBounds của FXGL vì CameraController đã tự clamp bounds (tránh conflict gây giật)

        // Sinh kill zones dựa trên kích thước map đúng
        MapFactory.generateKillZones(mapWidth, mapHeight);
        
        // ✅ RETURN map dimensions
        return new MapDimensions(mapWidth, mapHeight);
    }
}