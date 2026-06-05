package com.nhom67.platformfighter.map;

import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MapFactory {

        private static final double KZ = 1; // độ dày kill zone (buffer ra ngoài)

        public static void generateKillZones(double mapWidth, double mapHeight) {
                // LEFT — hoàn toàn ngoài map, mép trong (right edge) khít x=0
                spawn("KillZone", new SpawnData(-KZ, -KZ)
                                .put("width", KZ)
                                .put("height", mapHeight + KZ * 2));

                // RIGHT — hoàn toàn ngoài map, mép trong (left edge) khít x=mapWidth
                spawn("KillZone", new SpawnData(mapWidth, -KZ)
                                .put("width", KZ)
                                .put("height", mapHeight + KZ * 2));

                // TOP — hoàn toàn ngoài map, mép trong (bottom edge) khít y=0
                spawn("KillZone", new SpawnData(0, -KZ)
                                .put("width", mapWidth)
                                .put("height", KZ));

                // BOTTOM — hoàn toàn ngoài map, mép trong (top edge) khít y=mapHeight
                spawn("KillZone", new SpawnData(0, mapHeight)
                                .put("width", mapWidth)
                                .put("height", KZ));
        }
}