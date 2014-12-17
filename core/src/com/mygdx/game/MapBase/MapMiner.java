package com.mygdx.game.MapBase;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.WorldConfig;

import java.util.Random;

public class MapMiner {

    private static final int MAX_MINERS = WorldConfig.maxMiners;
    private static Array<Miner> miners;
    private static int minerCount = 0;

    public static TileValue[][] getMapModel() {
        TileValue[][] map = buildBaseMap();
        miners = new Array<Miner>();
        miners.add(new Miner(new Vector2(WorldConfig.worldTileWidth / 2, WorldConfig.groundHeight)));

        int currentMiner = 0;
        while(minerCount < MAX_MINERS) {
            if(miners.size == 0) {
                spawnMiner(new Vector2(WorldConfig.worldTileWidth / 2, WorldConfig.groundHeight));
            }

            Miner miner = miners.get(currentMiner);
            if(!mine(miner, map)) {
                move(miner, map);
            }

            if(miner.tilesLeftToMine <= 0) {
                miners.removeIndex(currentMiner);
            }

            currentMiner++;
            if(currentMiner > miners.size-1) {
                currentMiner = 0;
            }
        }

        postProduction(map);

        return map;
    }

    private static TileValue[][] buildBaseMap() {
        TileValue[][] map = new TileValue[WorldConfig.worldTileWidth][WorldConfig.worldTileHeight];

        for(int x = 0; x < WorldConfig.worldTileWidth; x++) {
            for(int y = 0; y < WorldConfig.worldTileWidth; y++) {
                map[x][y] = new TileValue(TileValue.NOTHING);
            }
        }

        for(int x = 0; x < WorldConfig.worldTileWidth; x++) {
            for(int y = 0; y < WorldConfig.groundHeight; y++) {
                map[x][y] = new TileValue(TileValue.ROCK);
            }
        }
        return map;
    }

    private static void move(Miner miner, TileValue[][] map) {
        int direction = new Random().nextInt(4);

        if(miner.position.y + 1< map[0].length-1 && direction == Miner.UP) miner.position.add(0, 1);
        if(miner.position.y - 1 < 0 && direction == Miner.DOWN) miner.position.add(0, -1);
        if(miner.position.x + 1 < map.length-1 && direction == Miner.RIGHT) miner.position.add(1, 0);
        if(miner.position.x - 1 < 0 && direction == Miner.LEFT) miner.position.add(-1, 0);

    }

    private static boolean mine(Miner miner, TileValue[][] map) {

        Array<Vector2> tileMiningOptions = new Array<Vector2>();
        int x = (int) miner.position.x;
        int y = (int) miner.position.y;

        if(x < 1 || y < 1 || x > map.length-2 ||  y > map[0].length-2) {
            miner.tilesLeftToMine = 0;
            return false;
        }

        if(map[x+1][y].value == TileValue.ROCK)
            tileMiningOptions.add(new Vector2(x+1, y));

        if(map[x-1][y].value == TileValue.ROCK)
            tileMiningOptions.add(new Vector2(x-1, y));

        if(map[x][y+1].value == TileValue.ROCK)
            tileMiningOptions.add(new Vector2(x, y+1));

        if(map[x][y-1].value == TileValue.ROCK)
            tileMiningOptions.add(new Vector2(x, y-1));

        if(tileMiningOptions.size == 0) {
            miner.tilesLeftToMine = 0;
            return false;
        }
        else {
            Vector2 v = tileMiningOptions.random();
            map[(int)v.x][(int)v.y].value = TileValue.TUNNEL;
            miner.position.set(v.x, v.y);

            trySpawnMiner(miner.position);

            return true;
        }
    }

    private static void trySpawnMiner(Vector2 position) {
        if(miners.size < 3) {
            float rand = new Random().nextFloat();
            if(rand < 0.6f) {
                spawnMiner(position);
            }
        }
    }

    private static void spawnMiner(Vector2 position) {
        Miner newMiner = new Miner(new Vector2(position.x, position.y));
        miners.add(newMiner);
        minerCount++;
    }

    private static TileValue[][] postProduction(TileValue[][] baseMap) {
        //Clean up tiles with 2 adjacent walls
        for(int x = 1; x < baseMap.length-1; x++) {
            for(int y = 1; y < baseMap[0].length-1; y++) {
                if(baseMap[x+1][y].value == TileValue.ROCK && baseMap[x-1][y].value == TileValue.ROCK &&
                         baseMap[x][y+1].value == TileValue.TUNNEL && baseMap[x][y-1].value == TileValue.TUNNEL) {
                    baseMap[x][y].value = TileValue.TUNNEL;
                }
                if(baseMap[x][y+1].value == TileValue.ROCK && baseMap[x][y-1].value == TileValue.ROCK &&
                         baseMap[x+1][y].value == TileValue.TUNNEL && baseMap[x-1][y].value == TileValue.TUNNEL) {
                    baseMap[x][y].value = TileValue.TUNNEL;
                }
            }
        }

        //Clean up tiles with no Adjacent walls
        for(int x = 1; x < baseMap.length-1; x++) {
            for(int y = 1; y < baseMap[0].length-1; y++) {
                if(baseMap[x+1][y].value == TileValue.TUNNEL && baseMap[x-1][y].value == TileValue.TUNNEL &&
                        baseMap[x][y+1].value == TileValue.TUNNEL && baseMap[x][y-1].value == TileValue.TUNNEL) {
                    baseMap[x][y].value = TileValue.TUNNEL;
                }
            }
        }

        beautifyTiles(baseMap);

        return baseMap;
    }

    private static void beautifyTiles(TileValue[][] baseMap) {
        for(int x = 0; x < baseMap.length-1; x++) {
            for(int y = 1; y < baseMap[0].length-1; y++) {

                //ROCK
                if(baseMap[x][y].value == TileValue.ROCK) {
                    //Add rock left
                    if(x > 1 && baseMap[x-1][y].value == TileValue.TUNNEL) {
                        baseMap[x][y].prettyImage = "rock_left";
                    }

                    //Add rock right
                    if(x > 1 && baseMap[x+1][y].value == TileValue.TUNNEL) {
                        baseMap[x][y].prettyImage = "rock_right";
                    }

//                  Add rock left, right
                    if(x > 1 && x < baseMap.length+1 &&
                            baseMap[x-1][y].value == TileValue.TUNNEL && baseMap[x+1][y].value == TileValue.TUNNEL) {
                        baseMap[x][y].prettyImage = "rock_left_right";
                    }
                }
            }
        }
    }

}
