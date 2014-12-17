package com.mygdx.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class OrthoCamController extends InputAdapter {
    final OrthographicCamera camera;
    private MyGdxGame.PlayEventListener playerEventListener;

    public OrthoCamController (OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        camera.zoom = camera.zoom + (float) amount;

        if(camera.zoom < 15f)
            camera.zoom = 15f;

        if(camera.zoom > 1000f)
            camera.zoom = 1000f;

        return false;
    }

    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        playerEventListener.click(screenX, screenY);
        return false;
    }

    public boolean keyDown (int keycode) {
        playerEventListener.move(keycode);
        return false;
    }

    public void setPlayerEventListener(MyGdxGame.PlayEventListener playerEventListener) {
        this.playerEventListener = playerEventListener;
    }
}
