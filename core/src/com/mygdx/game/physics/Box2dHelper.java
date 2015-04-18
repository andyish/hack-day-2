package com.mygdx.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Player;

/**
 * Created by Andrew on 17/02/2015.
 */
public class Box2dHelper {

    public static Fixture generateCharacter(Body body, float x, float y) {

        PolygonShape shape = new PolygonShape();

        Vector2 top = new Vector2(0f, 2f);
        Vector2 left = new Vector2(-1f, -1f);
        Vector2 right = new Vector2(1f, -1f);
        Vector2[] vertexes = {top, left, right};

        shape.set(vertexes);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.75f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        Fixture player = body.createFixture(fixtureDef);
        player.getBody().setLinearDamping(2);
        player.setUserData(new Player());

        body.setAngularDamping(2);

        shape.dispose();

        return player;
    }

    public static void createWall(World world, float x, float y) {
        createWall(world, x, y, 1, 1);
    }
    public static void createWall(World world, float x, float y, float hw, float hh) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw, hh);

        Fixture block = body.createFixture(shape, 0);

        body.setAngularDamping(2);

        shape.dispose();
    }
}
