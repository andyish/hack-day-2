package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by ishera02 on 17/12/2014.
 */
public class ControlButton extends Actor {
    TextureRegion texture;
    String id;
    Rectangle bounds;

    public ControlButton(String id, int x, int y) {
        this.id = id;
        setPosition(x, y);
        bounds = new Rectangle(x, y, 60, 60);

        texture = new TextureRegion(new Texture(Gdx.files.internal("controls/"+id+".png")));
    }

    @Override
    public void draw(Batch batch, float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.draw(texture, getX(), getY());

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
