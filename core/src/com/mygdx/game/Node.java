package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node {

    public Array<Node> vertexes;

    public Vector2 tilePosition;
    public Vector2 worldPosition;

    public Node(int x, int y) {
        tilePosition = new Vector2(x, y);
        worldPosition = new Vector2(x * WorldConfig.tileSize, y * WorldConfig.tileSize);
        vertexes = new Array<Node>();
    }

    public void addConnection(Node node) {
        if(!vertexes.contains(node, true)) {
            vertexes.add(node);
            node.addConnection(this);
        }
    }
}
