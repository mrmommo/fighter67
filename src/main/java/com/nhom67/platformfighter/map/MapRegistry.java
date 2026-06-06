package com.nhom67.platformfighter.map;

public enum MapRegistry {
    MAP_1(
            "map1.tmx",
            "Battlefield",
            4,
            "menu_track_01.mp3"),
    MAP_2(
            "map2.tmx",
            "Loopdrop",
            4,
            "menu_track_02.mp3"),
    MAP_3(
            "map3.tmx",
            "Garden",
            6,
            "menu_track_03.mp3"),
    MAP_4(
            "map4.tmx",
            "Greenzone",
            5,
            "menu_track_05.mp3"), // Using track 05 for map 4 for now
    MAP_5(
            "map5.tmx",
            "Factory",
            9,
            "menu_track_05.mp3"),
    MAP_6(
            "map6.tmx",
            "Cakecombat",
            4,
            "menu_track_01.mp3"), // Using track 01 for map 6
    MAP_7(
            "map7.tmx",
            "Volcano",
            4,
            "menu_track_01.mp3");

    private final String tmxFile;
    private final String displayName;
    private final int layerCount;
    private final String musicTrack;

    MapRegistry(String tmxFile, String displayName, int layerCount, String musicTrack) {
        this.tmxFile = tmxFile;
        this.displayName = displayName;
        this.layerCount = layerCount;
        this.musicTrack = musicTrack;
    }

    public String getTmxFile() {
        return tmxFile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public String getMusicTrack() {
        return musicTrack;
    }

    public String getPreviewImagePath() {
        return "maps/map" + name().replace("MAP_", "") + "background.png";
    }
}