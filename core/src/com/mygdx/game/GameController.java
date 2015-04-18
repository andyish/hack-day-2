package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.physics.Box2dHelper;
import com.mygdx.game.screens.MyGame;

public class GameController implements Screen {
    private final MyGame game;
    private final ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;
    private OrthoCamController cameraController;
    private SpriteBatch batch;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Matrix4 debugMatrix;

    private Array<Fixture> bulletList;
    private Array<Body> worldDeletionList;

    private Fixture player;
    private Vector2 lookAtPos;
    private boolean thrustActive = false;
    private boolean triggerActive = false;
    private float playerPointX;
    private float playerPointY;
    private float mousePointY;
    private float mousePointX;

    private Map map;


    public interface PlayEventListener {
        public void move(int keyCode);
        public void click(int x, int y);
    }

    public GameController(MyGame game) {
        super();

        shapeRenderer = new ShapeRenderer();

        this.game = game;
        this.lookAtPos = new Vector2(0, 0);
        initGame();

        map = new TmxMapLoader().load("temp.tmx");
        MapBodyBuilder.buildShapes(map, 16, world);
    }


    public void initGame() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
//        camera.setToOrtho(false, (w / h) * 640, 640);
        camera.setToOrtho(true, w, h);
        camera.position.set(0, 0, 0);
        camera.zoom = 150;
        camera.update();

        cameraController = new OrthoCamController(camera);
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(keyListener);

        batch = new SpriteBatch();

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(8, 8);

        Body body = world.createBody(bodyDef);
        player = Box2dHelper.generateCharacter(body, 0, 0);

        buildWorld();

        bulletList = new Array<Fixture>();
        worldDeletionList = new Array<Body>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if(contact.getFixtureA().getUserData() instanceof Bullet && (contact.getFixtureB().getUserData() instanceof Player == false)) {
                    ((Bullet) contact.getFixtureA().getUserData()).disable();
                    bulletList.removeValue(contact.getFixtureA(), true);
                    worldDeletionList.add(contact.getFixtureA().getBody());
                } else if(contact.getFixtureB().getUserData() instanceof Bullet && (contact.getFixtureA().getUserData() instanceof Player == false)) {
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

    }

    private void buildWorld() {
//        Box2dHelper.createWall(world, 0, 30, 45, 1);
//        Box2dHelper.createWall(world, 0, -30, 45, 1);
//        Box2dHelper.createWall(world, 50, 0, 1, 30);
//        Box2dHelper.createWall(world, -50, 0, 1, 30);
//
//        Box2dHelper.createWall(world, 10, 0, 1, 20);
//        Box2dHelper.createWall(world, -10, 0, 1, 20);
//
//        Box2dHelper.createWall(world, 20, 0, 1, 10);
//        Box2dHelper.createWall(world, -20, 0, 1, 10);
//
//        Box2dHelper.createWall(world, 35, 0, 1, 20);
//        Box2dHelper.createWall(world, -35, 0, 1, 20);
    }

    private InputProcessor keyListener = new InputProcessor() {
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(Input.Buttons.LEFT == button)
                triggerActive = true;
            else if (Input.Buttons.RIGHT == button)
                thrustActive = true;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(Input.Buttons.LEFT == button)
                triggerActive = false;
            if (Input.Buttons.RIGHT == button)
                thrustActive = false;

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            lookAtPos.set(screenX, screenY);
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    };

    private void renderDebugBox2d() {
        debugMatrix = new Matrix4(camera.combined);
        debugMatrix.scale(32f, 32f, 1f);
        debugRenderer.render(world, debugMatrix);
    }

    public void update() {

        Vector3 screenPos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        camera.position.set(player.getBody().getPosition().x * 32, player.getBody().getPosition().y * 32, 0);



        playerPointX = player.getBody().getPosition().x * 32;
        playerPointY = player.getBody().getPosition().y * 32;

        mousePointX = screenPos.x;
        mousePointY = screenPos.y;

        Vector2 direction = new Vector2(mousePointX - playerPointX, mousePointY - playerPointY).nor();

        player.getBody().setTransform(player.getBody().getPosition(), MathUtils.atan2(direction.y, direction.x) - (90 * MathUtils.degreesToRadians));

        if(thrustActive) {
            applyThurst(direction);
        }
        if(triggerActive) {
            createBullet(direction);
            triggerActive = false;
        }

        if(worldDeletionList.size > 0) {
            for(Body b : worldDeletionList) {
                world.destroyBody(b);
            }
            worldDeletionList.clear();
        }

        world.step(1 / 60f, 6, 2);
    }

    private void applyThurst(Vector2 direction) {
        float speed = 100;
        player.getBody().applyForceToCenter(direction.scl(speed), true);
    }


    private void createBullet(Vector2 direction) {
        Vector2 worldChar = new Vector2(player.getBody().getPosition().x, player.getBody().getPosition().y);

        Bullet bullet = new Bullet(worldChar, 0);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(worldChar);

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture bulletBody = body.createFixture(fixtureDef);
        bulletBody.getBody().setFixedRotation(true);
        bulletBody.getBody().setBullet(true);
        bulletBody.setUserData(bullet);
        bulletBody.setSensor(true);
        body.setGravityScale(0);

        bulletList.add(bulletBody);

        // calculte the normalized direction from the body to the touch position
        float speed = 10;
        body.setLinearVelocity(direction.scl(speed));


        shape.dispose();
    }

    public static float angleBetween(Vector2 p1, Vector2 p2) {
        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;

        return (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        renderDebugBox2d();

        shapeRenderer.setColor(0, 1, 0, 0);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(mousePointX, mousePointY, 10);
        shapeRenderer.end();

        shapeRenderer.setColor(1, 0, 0, 0);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(playerPointX, playerPointY, 10);
        shapeRenderer.end();

        update();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width/32f;
        camera.viewportHeight = camera.viewportWidth * height/width;
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}