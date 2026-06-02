package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MapFactory {

    public static void generateKillZones(double mapWidth, double mapHeight) {
        // BOTTOM kill zone (dưới đáy map, thêm buffer 50px)
        spawn("KillZone", new SpawnData(0, mapHeight + 50)
                .put("width", mapWidth)
                .put("height", 100.0));
        // LEFT kill zone (bên trái, cao hơn map)
        spawn("KillZone", new SpawnData(-150, -500)
                .put("width", 100.0)
                .put("height", mapHeight + 1500));
        // RIGHT kill zone (bên phải, cao hơn map)
        spawn("KillZone", new SpawnData(mapWidth + 50, -500)
                .put("width", 100.0)
                .put("height", mapHeight + 1500));
    }
}