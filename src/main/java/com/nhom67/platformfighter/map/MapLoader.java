package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.nhom67.platformfighter.entity.EntityType;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MapLoader {

    public static void loadMap(MapRegistry map) {
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

        // Giới hạn camera theo kích thước map thực
        getGameScene().getViewport().setBounds(0, 0, (int) mapWidth, (int) mapHeight);

        // Sinh kill zones dựa trên kích thước map đúng
        MapFactory.generateKillZones(mapWidth, mapHeight);
    }
}