package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MapBase.MapBuilder;
import com.mygdx.game.MapBase.MapMiner;
import com.mygdx.game.MapBase.TileValue;

public class MyGdxGame extends ApplicationAdapter {
    private TiledMap map;
    private TiledMapRenderer renderer;
    private OrthographicCamera camera;
    private OrthoCamController cameraController;
    private BitmapFont font;
    private SpriteBatch batch;
    private TextureRegion background;

    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;
    private Texture walkSheet;
    private TextureRegion[] walkRightFrames;
    private TextureRegion[] walkLeftFrames;

    private SpriteBatch spriteBatch;
    private TextureRegion currentFrame;
    private float stateTime;

    World world;
    Box2DDebugRenderer debugRenderer;
    private Fixture character;
    private TextureRegion stationaryFrame;
    private Matrix4 debugMatrix;

    private Array<Fixture> bulletList;
    private float direction;
    private Array<Body> worldDeletionList;

    private float startZoom = 100f;
    private float endZoom = 15f;
    private boolean zoomIntro = true;

    private Stage stage;

    public interface PlayEventListener {
        public void move(int keyCode);
        public void click(int x, int y);
    }


    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (w / h) * 640, 640);
        camera.translate(0, 0);
        camera.zoom = startZoom;
        camera.update();

        background = new TextureRegion(new Texture(Gdx.files.internal("sky/sky_background.png")));
        cameraController = new OrthoCamController(camera);
        Gdx.input.setInputProcessor(cameraController);

        font = new BitmapFont();
        batch = new SpriteBatch();

        final TileValue[][] mapPattern = MapMiner.getMapModel();

        map = new TiledMap();
        map = MapBuilder.GetBaseMap(map, mapPattern, getTiles());

        renderer = new OrthogonalTiledMapRenderer(map);

        world = new World(new Vector2(0, WorldConfig.gravity), true);
        debugRenderer = new Box2DDebugRenderer();

        generateStaticBodies(mapPattern);
        generateCharacter();

        cameraController.setPlayerEventListener(new PlayEventListener() {
            @Override
            public void move(int keyCode) {
            }

            @Override
            public void click(int clickX, int clickY) {
                Vector3 v = camera.unproject(new Vector3(clickX, clickY, 0));
            }
        });

        walkSheet = new Texture(Gdx.files.internal("ninja_m.png"));
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 36);

        walkRightFrames = new TextureRegion[3];
        walkRightFrames[0] = tmp[1][0];
        walkRightFrames[1] = tmp[1][1];
        walkRightFrames[2] = tmp[1][2];

        walkLeftFrames = new TextureRegion[3];
        walkLeftFrames[0] = tmp[3][0];
        walkLeftFrames[1] = tmp[3][1];
        walkLeftFrames[2] = tmp[3][2];

        stationaryFrame = tmp[2][1];

        walkRightAnimation = new Animation(0.15f, walkRightFrames);
        walkLeftAnimation = new Animation(0.15f, walkLeftFrames);
        spriteBatch = new SpriteBatch();
        stateTime = 0f;

        currentFrame = stationaryFrame;

        bulletList = new Array<Fixture>();
        worldDeletionList = new Array<Body>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if(contact.getFixtureA().getUserData() instanceof Bullet) {
                    ((Bullet) contact.getFixtureA().getUserData()).disable();
                    bulletList.removeValue(contact.getFixtureA(), true);
                    worldDeletionList.add(contact.getFixtureA().getBody());
                } else if(contact.getFixtureB().getUserData() instanceof Bullet) {
                    ((Bullet) contact.getFixtureB().getUserData()).disable();
                    bulletList.removeValue(contact.getFixtureB(), true);
                    worldDeletionList.add(contact.getFixtureB().getBody());
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        if(WorldConfig.isTouchDevice) {
//            stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            stage = new Stage(new ScreenViewport());
            final ControlButton up = new ControlButton("up", 100, 105);
            final ControlButton down = new ControlButton("down", 100, 20);
            final ControlButton left = new ControlButton("left", 40, 70);
            final ControlButton right = new ControlButton("right", 150, 70);
            final ControlButton fire = new ControlButton("fire", Gdx.graphics.getWidth() - 150, 60);
            stage.addActor(up);
            stage.addActor(down);
            stage.addActor(left);
            stage.addActor(right);
            stage.addActor(fire);
            Gdx.input.setInputProcessor(stage);

            stage.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    if(up.bounds.contains(x, y)) {
                        character.getBody().applyLinearImpulse(new Vector2(0, WorldConfig.jumpVel), character.getBody().getWorldCenter(), true);
                    }
                    if(left.bounds.contains(x, y)) {
                        character.getBody().applyLinearImpulse(-WorldConfig.runVel/4, 0, character.getBody().getPosition().x, 0, true);
                        direction = -1;
                    }
                    if(right.bounds.contains(x, y)) {
                        character.getBody().applyLinearImpulse(WorldConfig.runVel/4, 0, character.getBody().getPosition().x, 0, true);
                        direction = 1;
                    }
                    if(down.bounds.contains(x, y)) {
                        character.getBody().applyLinearImpulse(new Vector2(0, -1f), character.getBody().getWorldCenter(), true);
                    }
                    if(fire.bounds.contains(x, y)) {
                        if(direction != 0) {
                            createBullet();
                        }
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        camera.update();

        renderBackground(batch);
        renderMap(batch);

        renderCharacter(batch);
        renderBullet(batch);
        renderFPS(batch);

        if(WorldConfig.isTouchDevice)
            stage.draw();

//        renderDebugBox2d();

        update();
    }

    private void renderDebugBox2d() {
        debugMatrix=new Matrix4(camera.combined);
        debugMatrix.scale(32f, 32f, 1f);
        debugRenderer.render(world, debugMatrix);
    }

    public void update() {
        if(zoomIntro) {
            camera.zoom = camera.zoom - 1f;
            if(camera.zoom < endZoom) {
                zoomIntro = false;
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && character.getBody().getLinearVelocity().x > -WorldConfig.runVel) {
            character.getBody().applyLinearImpulse(-WorldConfig.runVel/4, 0, character.getBody().getPosition().x, 0, true);
            direction = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && character.getBody().getLinearVelocity().x < WorldConfig.runVel) {
            character.getBody().applyLinearImpulse(WorldConfig.runVel/4, 0, character.getBody().getPosition().x, 0, true);
            direction = 1;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && character.getBody().getLinearVelocity().y <0.1f && character.getBody().getLinearVelocity().y > -0.1f) {
            character.getBody().applyLinearImpulse(new Vector2(0, WorldConfig.jumpVel), character.getBody().getWorldCenter(), true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            character.getBody().applyLinearImpulse(new Vector2(0, -1f), character.getBody().getWorldCenter(), true);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(direction != 0) {
                createBullet();
            }
        }

        camera.position.set(character.getBody().getPosition().x * 32, character.getBody().getPosition().y * 32, 0);

        if(worldDeletionList.size > 0) {
            for(Body b : worldDeletionList) {
                world.destroyBody(b);
            }
            worldDeletionList.clear();
        }

        world.step(1/45f, 4, 8);

        if(WorldConfig.isTouchDevice)
            stage.act();
    }

    private void renderBullet(Batch batch) {
        for(Fixture bulletFixutre:bulletList) {
            Bullet b = (Bullet) bulletFixutre.getUserData();
            b.render(batch, bulletFixutre.getBody().getPosition().x * 32, bulletFixutre.getBody().getPosition().y * 32 - 12);
        }
    }

    private void renderBackground(Batch batch) {
        batch.begin();
        batch.draw(background,
                0, 0,
                WorldConfig.worldTileWidth * WorldConfig.tileSize, WorldConfig.worldTileHeight * WorldConfig.tileSize);
        batch.end();
    }

    private void renderMap(Batch batch) {
        renderer.render();
    }

    private void renderFPS(Batch batch) {
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        batch.end();
    }

    private void renderCharacter(Batch batch) {
        spriteBatch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();

        spriteBatch.begin();
        if(character.getBody().getLinearVelocity().x > 1) {
            currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
        }
        if(character.getBody().getLinearVelocity().x < -1) {
            currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
        }
        spriteBatch.draw(currentFrame, character.getBody().getPosition().x * WorldConfig.tileSize - 16, character.getBody().getPosition().y * WorldConfig.tileSize - 8, 20, 24);
        spriteBatch.end();
    }

    public ArrayMap<String, TextureRegion> getTiles() {
        ArrayMap<String, TextureRegion> map = new ArrayMap<String, TextureRegion>();

        map.put("ROCK", new TextureRegion(new Texture(Gdx.files.internal("dirt/rock_centre.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("TUNNEL", new TextureRegion(new Texture(Gdx.files.internal("dirt/tile_11.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("grass_top", new TextureRegion(new Texture(Gdx.files.internal("dirt/grass_top.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("rock_left", new TextureRegion(new Texture(Gdx.files.internal("dirt/rock_left.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("rock_right", new TextureRegion(new Texture(Gdx.files.internal("dirt/rock_right.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("rock_left_right", new TextureRegion(new Texture(Gdx.files.internal("dirt/rock_left_right.png")), WorldConfig.tileSize, WorldConfig.tileSize));
        map.put("grass", new TextureRegion(new Texture(Gdx.files.internal("dirt/grass.png")), WorldConfig.tileSize, WorldConfig.tileSize));

        return map;
    }

    private void createGroundTileAt(int x, int y) {
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(x + 0.5f, y + 0.5f));

        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(0.5f, 0.5f);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void generateStaticBodies(TileValue[][] baseMap) {
        for(int x = 0; x < baseMap.length; x++) {
            for(int y = 0; y < baseMap[0].length; y++) {
                if(baseMap[x][y].value == TileValue.ROCK) {
                    createGroundTileAt(x, y);
                }
            }
        }
    }

    private void generateCharacter() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WorldConfig.worldTileWidth / 2, WorldConfig.groundHeight);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        Vector2 bottomLeft = new Vector2(-0.3f, -0.2f);
        Vector2 bottomBaseLeft = new Vector2(-0.2f, -0.25f);
        Vector2 bottomBaseRight = new Vector2(-0.025f, -0.25f);
        Vector2 bottomRight = new Vector2(0.075f, -0.2f);
        Vector2 topLeft = new Vector2(-0.3f, 0.5f);
        Vector2 topLidLeft = new Vector2(-0.2f, 0.55f);
        Vector2 topLidRight = new Vector2(-0.025f, 0.55f);
        Vector2 topRight = new Vector2(0.075f, 0.5f);
        Vector2[] vertexes = {bottomLeft, topLeft, topLidLeft, topLidRight, topRight, bottomRight, bottomBaseRight, bottomBaseLeft};

        shape.set(vertexes);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.75f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        character = body.createFixture(fixtureDef);
        character.getBody().setLinearDamping(2);
        character.getBody().setFixedRotation(true);
        MassData mData = new MassData();
        mData.mass = 50f;
        character.getBody().setMassData(mData);
        character.getBody().resetMassData();

        body.applyForce(10, 10, bodyDef.position.x - 10, bodyDef.position.y - 10, true);

        shape.dispose();
    }

    private void createBullet() {

        Vector2 worldChar = new Vector2(character.getBody().getPosition().x, character.getBody().getPosition().y);

        Bullet bullet = new Bullet(worldChar, (direction  > 0 ? 270 : 90));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(character.getBody().getPosition().x + (0.5f * direction), character.getBody().getPosition().y));

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.025f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture bulletBody = body.createFixture(fixtureDef);
        bulletBody.getBody().setFixedRotation(true);
        bulletBody.getBody().setBullet(true);
        bulletBody.setUserData(bullet);
        body.setGravityScale(0);

        bulletList.add(bulletBody);

        body.applyForceToCenter(500 * direction, 0, true);


        shape.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width/32f;
        camera.viewportHeight = camera.viewportWidth * height/width;
        camera.update();
    }
}