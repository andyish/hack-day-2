package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public TextureRegion texture;
    public Vector2 position;
    public float direction;
    private boolean isEnabled = true;

    public Bullet(Vector2 position, float angle) {
        this.position = position;
        Texture t = new Texture(Gdx.files.internal("bullet.png"));
        texture = new TextureRegion(t);
        direction = angle;
    }

    public void render(Batch batch, float x, float y) {
        if(isEnabled) {
            batch.begin();
            batch.draw(texture,
                    x , y - 10,
                    0, 26,
                    texture.getRegionWidth() / 16, texture.getRegionHeight() / 16,
                    1f, 1f,
                    direction);
            batch.end();
        }
    }

    public void update() {

    }

    public void disable() {
        isEnabled = false;
    }
}
