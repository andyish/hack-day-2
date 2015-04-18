package com.mygdx.game.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GameController;

public class MainMenuScreen implements Screen, ApplicationListener {
    private final MyGame game;
    private final Stage stage;
    private final Table rootTable;
    private final Table bottom;
    private final Table top;
    private final Table mid;

    OrthographicCamera camera;

    public MainMenuScreen(final MyGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());

        rootTable = new Table();

        rootTable.setWidth(Gdx.graphics.getWidth());
        rootTable.setHeight(Gdx.graphics.getHeight());
        rootTable.setFillParent(true);

        top = new Table();
        mid = new Table();
        bottom = new Table();

        rootTable.add(top).width(rootTable.getWidth()).height(rootTable.getHeight() / 3).top();
        rootTable.row();
        rootTable.add(mid).width(rootTable.getWidth()).height(rootTable.getHeight() / 3).center();
        rootTable.row();
        rootTable.add(bottom).width(rootTable.getWidth()).height(rootTable.getHeight() / 3).bottom();
        top.setTouchable(Touchable.enabled);
        mid.setTouchable(Touchable.enabled);
        bottom.setTouchable(Touchable.enabled);
        stage.addActor(rootTable);

        createWeaponButton(1, top, "gun.png");

        Gdx.input.setInputProcessor(stage);
    }

    private void createWeaponButton(final int levelNumber, Table table, final String weapon) {
        SpriteDrawable button;
        button = new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal(weapon))));

        ImageButton btn = new ImageButton(button);

        table.add(btn);

        btn.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(levelNumber != 0) {
                    game.setScreen(gameWithGun(weapon));
                } else {
                    System.out.println("Not yet implemented :(");
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        stage.act();
        stage.draw();
    }

    private GameController gameWithGun(String weapon) {
        return new GameController(game);
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
