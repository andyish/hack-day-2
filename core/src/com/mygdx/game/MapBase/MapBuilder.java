package com.mygdx.game.MapBase;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.ArrayMap;
import com.mygdx.game.WorldConfig;

/**
 * Created by ishera02 on 16/12/2014.
 */
public class MapBuilder {

    public static TiledMap GetBaseMap(TiledMap baseMap, TileValue[][] mapPattern, ArrayMap<String, TextureRegion> tiles) {
        MapLayers layers = baseMap.getLayers();

        layers.add(generateSubtrainainLayer(mapPattern, tiles));

        TiledMapTileLayer layer = new TiledMapTileLayer(mapPattern.length, mapPattern[0].length, WorldConfig.tileSize, WorldConfig.tileSize);
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                if(mapPattern[x][y].value != TileValue.NOTHING) {
                    cell.setTile(new StaticTiledMapTile(tiles.get(mapPattern[x][y].getPrettyImage())));
                    cell.getTile().getProperties().put("content", mapPattern[x][y].getValue());
                }

                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);

        layers.add(generateGrassLayer(mapPattern, tiles));

        return baseMap;
    }

    private static MapLayer generateSubtrainainLayer(TileValue[][] mapPattern, ArrayMap<String, TextureRegion> tiles) {
        TiledMapTileLayer layer = new TiledMapTileLayer(mapPattern.length, mapPattern[0].length, WorldConfig.tileSize, WorldConfig.tileSize);

        for(int x = 0; x < mapPattern.length; x++) {
            for(int y = 0; y < mapPattern[0].length; y++) {
                if(mapPattern[x][y].value != TileValue.NOTHING) {

                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                    if(mapPattern[x][y].value != TileValue.NOTHING) {
                        cell.setTile(new StaticTiledMapTile(tiles.get("TUNNEL")));
                    }

                    layer.setCell(x, y, cell);
                }
            }
        }

        return layer;
    }

    private static MapLayer generateGrassLayer(TileValue[][] mapPattern, ArrayMap<String, TextureRegion> tiles) {
        TiledMapTileLayer layer = new TiledMapTileLayer(mapPattern.length, mapPattern[0].length, WorldConfig.tileSize, WorldConfig.tileSize);

        for(int x = 0; x < mapPattern.length; x++) {
            for(int y = 0; y < mapPattern[0].length; y++) {
                if(mapPattern[x][y].value != TileValue.NOTHING) {

                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                    //ROCK
                    if(mapPattern[x][y].value == TileValue.ROCK) {
                        //Add grass tile
                        if(mapPattern[x][y+1].value == TileValue.NOTHING || mapPattern[x][y+1].value == TileValue.TUNNEL) {
                            cell.setTile(new StaticTiledMapTile(tiles.get("grass_top")));
                        }
                        //Add grass topping
                        if(mapPattern[x][y+1].value == TileValue.NOTHING) {
                            TiledMapTileLayer.Cell grassCell = new TiledMapTileLayer.Cell();
                            grassCell.setTile(new StaticTiledMapTile(tiles.get("grass")));
                            layer.setCell(x, y+1, grassCell);
                        }
                    }

                    layer.setCell(x, y, cell);
                }
            }
        }

        return layer;
    }

//    public static TiledMap MakeTunnelsFor(TiledMap map, Node home, TextureRegion tunnleTexture) {
//
//        for(int layers = 0; layers < map.getLayers().getCount(); layers++) {
//            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layers);
//            for(int x = 0; x < layer.getWidth();x++){
//                for(int y = 0; y < layer.getHeight();y++){
//                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
//                    if(cell != null) {
//                        Rectangle cellRectangle = new Rectangle(x, y, 1, 1);
//
//                        for(Node connections : home.vertexes) {
//                            Line2D line = new Line2D.Float(home.tilePosition.x, home.tilePosition.y, connections.tilePosition.x, connections.tilePosition.y);
//                            if(cellRectangle.intersectsLine(line)) {
//                                cell.getTile().setTextureRegion(tunnleTexture);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return map;
//    }
}
