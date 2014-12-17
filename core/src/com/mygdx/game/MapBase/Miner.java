package com.mygdx.game.MapBase;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Miner {
    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    public int tilesLeftToMine;
    public Vector2 position;

    public Miner(Vector2 position) {
        this.position = position;
        tilesLeftToMine = MathUtils.clamp(new Random().nextInt(17), 5, 15);
    }

}
