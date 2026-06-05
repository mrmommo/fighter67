package com.nhom67.platformfighter.map;

public enum MapRegistry {
    MAP_1(
            "map1.tmx",
            "Battlefield",
            "maps/map1background.png"),
    MAP_2(
            "map2.tmx",
            "Loopdrop",
            "maps/map2background.png"),
    MAP_3(
            "map3.tmx",
            "Garden",
            "maps/map3background.png"),
    MAP_4(
            "map4.tmx",
            "Greenzone",
            "maps/map4background.png"),
    MAP_5(
            "map5.tmx",
            "Factory",
            "maps/map5background.png"),
    MAP_6(
            "map6.tmx", 
            "Cakecombat",
            "maps/map6background.png");
            
    private final String tmxFile;
    private final String displayName;
    private final String bgPath;

    MapRegistry(String tmxFile, String displayName, String bgPath) {
        this.tmxFile = tmxFile;
        this.displayName = displayName;
        this.bgPath = bgPath;
    }

    public String getTmxFile() {
        return tmxFile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBgPath() {
        return bgPath;
    }
}