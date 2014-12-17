package com.mygdx.game.MapBase;

/**
 * Created by ishera02 on 16/12/2014.
 */
public class TileValue {
    public static final int ROCK = 0;
    public static final int TUNNEL = 1;
    public static final int NOTHING = 2;

    public int value;
    public String prettyImage;

    public TileValue(int value) {
        this.value = value;
    }

    public String getValue() {
        if(value == ROCK) {
            return "ROCK";
        } else if(value == TUNNEL) {
            return "TUNNEL";
        } else {
            return "NONE";
        }
    }

    public String getPrettyImage() {
        return (prettyImage == null ? getValue() : prettyImage);
    }
}
