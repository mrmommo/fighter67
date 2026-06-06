package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MapFactory {

        private static final double KZ = 20; // độ dày kill zone (buffer ra ngoài)

        public static void generateKillZones(double mapWidth, double mapHeight) {
                // BOTTOM — hoàn toàn ngoài map, mép trong (top edge) khít y=mapHeight
                spawn("KillZone", new SpawnData(-(mapWidth * 5), mapHeight)
                                .put("width", mapWidth * 10)
                                .put("height", KZ));
        }
}