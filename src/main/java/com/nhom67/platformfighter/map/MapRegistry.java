package com.nhom67.platformfighter.map;

public enum MapRegistry {
     MAP_1(
            "map1.tmx",
            "Battlefield",
            4,
            "menu_track_01.mp3",
            "#a8e6cf"),   // Mint green — forest/battlefield
  /*   MAP_2(
            "map2.tmx",
            "Loopdrop",
            4,
            "menu_track_02.mp3",
            "#a8e6cf"),  */
    MAP_3(
            "map3.tmx",
            "City of Lights",
            6,
            "menu_track_03.mp3",
            "#74b9ff"),  // Sky blue — đô thị ban đêm
    MAP_4(
            "map4.tmx",
            "Greenzone",
            5,
            "menu_track_04.mp3",
            "#55efc4"),  // Emerald green — rừng xanh
    MAP_5(
            "map5.tmx",
            "Factory",
            9,
            "menu_track_05.mp3",
            "#fdcb6e"),  // Amber — nhà máy công nghiệp
    MAP_6(
            "map6.tmx", 
            "Sweetday",
            4,
            "menu_track_06.mp3",
            "#fd79a8"),  // Pink — ngọt ngào
    MAP_7(
            "map7.tmx", 
            "Volcano Valley",
            4,
            "menu_track_07.mp3",
            "#ff7675");  // Red-orange — núi lửa
            
    private final String tmxFile;
    private final String displayName;
    private final int layerCount;
    private final String musicTrack;
    private final String accentColor;

    MapRegistry(String tmxFile, String displayName, int layerCount, String musicTrack, String accentColor) {
        this.tmxFile = tmxFile;
        this.displayName = displayName;
        this.layerCount = layerCount;
        this.musicTrack = musicTrack;
        this.accentColor = accentColor;
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

    public String getAccentColor() {
        return accentColor;
    }

    public String getPreviewImagePath() {
        return "maps/map" + name().replace("MAP_", "") + "background.png";
    }
}