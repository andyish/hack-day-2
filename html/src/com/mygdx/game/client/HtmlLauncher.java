package com.mygdx.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.mygdx.game.MyGdxGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        int height = com.google.gwt.user.client.Window.getClientHeight();
        int width = com.google.gwt.user.client.Window.getClientWidth();
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(width, height);
        return cfg;
    }

    @Override
    public ApplicationListener getApplicationListener () {
                return new MyGdxGame();
        }

    @Override
    public void onModuleLoad () {
        super.onModuleLoad();
        com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent ev) {
                Gdx.graphics.setDisplayMode(ev.getWidth(),ev.getHeight(), false);
            }
        });
    }
}