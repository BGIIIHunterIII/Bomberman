package ch.fhnw.bomberman.Entities;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by P on 11.04.2015.
 */
public class EntityManager {

    private ArrayList<Player> players= new ArrayList<Player>();
    private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
    private BombExplosions explosionAnimations;

    private Player player_local;
    private final TiledMap map;
    private final float tileWidth;
    private final float tileHeight;
    private TiledMapTileLayer nonWalkableTileLayer;
    private TiledMapTileLayer breakAbleTileLayer;
    private MapLayer cBoxes;
    private long uptime;

    public EntityManager(TiledMap map) {
        this.map = map;
        tileWidth = ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth();
        tileHeight = ((TiledMapTileLayer) map.getLayers().get(0)).getTileHeight();
        init();
    }

    public void init(){

        // load information from the mapfile
        nonWalkableTileLayer = (TiledMapTileLayer) map.getLayers().get("Unbreakable");
        if(nonWalkableTileLayer == null){
            Gdx.app.error("Map","couldn't find \"Unbreakable\" tile layer. Please check the map file");
            throw new IllegalStateException();
        }
        cBoxes = map.getLayers().get("CollissionBoxes");
        if(cBoxes == null){
            Gdx.app.error("Map","couldn't find \"CollissionBoxes\" tile layer. Please check the map file");
            throw new IllegalStateException();
        }

        breakAbleTileLayer = (TiledMapTileLayer) map.getLayers().get("Breakable");
        if(breakAbleTileLayer == null){
            Gdx.app.error("Map","couldn't find \"Breakable\" tile layer. Please check the map file");
            throw new IllegalStateException();
        }

        spawnPlayers();
        player_local = players.get(0);
        explosionAnimations= new BombExplosions(breakAbleTileLayer,nonWalkableTileLayer);
        uptime = TimeUtils.millis();
    }

    public void update(){
        for(Player p : players){
            p.update();
        }
        handleMapCollissions();

        ArrayList<Bomb> toRemove = new ArrayList<Bomb>();
        for(Bomb b : bombs){
            b.update();
            if(b.isExploded()){
                explosionAnimations.add(b);
                toRemove.add(b);
            }
        }
        bombs.removeAll(toRemove);

        switch(Gdx.app.getType()){
            case Android:
            bombs.add(new Bomb(player_local.getPosition(),3));
                break;
        }

    }

    public void render(Batch b){
        explosionAnimations.render(b);
    }




    private void handleMapCollissions(){
        //prevent players from stepping onto non-walkable tiles

        for(Player p : players){
            /*
            calculate where the player would be the next frame (when its velocity is added to its position)
            if it would be on a non-walkable-tile then set its velocity to 0
             */
            Rectangle futurePosition = p.getCollissionBox();
            if(tiledMapLayerHasTilesAt(futurePosition,nonWalkableTileLayer)){ // then this is a non-walkable tile
                p.getPosition().set(p.getPreviousPosition()); //reset to old position
            }

            if(tiledMapLayerHasTilesAt(futurePosition,breakAbleTileLayer)){
                p.getPosition().set(p.getPreviousPosition());
            }

            Iterator<MapObject> i = cBoxes.getObjects().iterator();
            while (i.hasNext()){
                Rectangle c = ((RectangleMapObject) i.next()).getRectangle();

                if(c.contains(futurePosition)){
                    p.getPosition().set(p.getPreviousPosition());
                    break;
                }
            }
        }
    }
    /*
    spawns a player at each spawnlocation
    players may spawn at the center of any unoccupied spawnposition (randomly decided)
     */
    private void spawnPlayers(){
        ArrayList<Rectangle> spawnZones = new ArrayList<Rectangle>();

        MapLayer s = map.getLayers().get("spawnzones");
        if(s == null){
           Gdx.app.error("Map","couldn't find \"spawnzones\" tile layer. Please check the map file");
            throw new IllegalStateException();
        }
        s.setVisible(true);

        Iterator<MapObject> i =  s.getObjects().iterator();
        while (i.hasNext()){
            spawnZones.add(((RectangleMapObject)i.next()).getRectangle());
        }
        int nSpawnLocations = spawnZones.size();

        for (int j = 0; j <nSpawnLocations; j++) {
            int index =(int) ( Math.random() * spawnZones.size());

            Rectangle r = spawnZones.get(index);
            spawnZones.remove(index);
            Vector2 spawnPos = new Vector2();

            Rectangle tmp;
            do{
                spawnPos.x = (float) (Math.random() * r.width + r.x);
                spawnPos.y = (float) (Math.random() * r.height + r.y);
                tmp = new Rectangle(spawnPos.x,spawnPos.y,Player.WIDTH,Player.HEIGHT);
            } while (tiledMapLayerHasTilesAt(tmp,nonWalkableTileLayer)==true || tiledMapLayerHasTilesAt(tmp,breakAbleTileLayer)==true);

            players.add(new Player(spawnPos));
        }
    }

    public Vector2 worldPositionToTileIndex(Vector2 pos){


        Vector2 tileLoc = new Vector2();
        tileLoc.x = pos.x / tileWidth;
        tileLoc.y = pos.y / tileHeight;

        return tileLoc;
    }
    public TiledMapTileLayer.Cell getTileAtPosition(Vector2 pos,TiledMapTileLayer layer){
        Vector2 tileLoc = worldPositionToTileIndex(pos);
        return layer.getCell((int)tileLoc.x,(int)tileLoc.y);

    }


    /*lazy check:
    checks whether any of the edges of the given Rectangle are at a position
    where there is also a tile (cell!=null) in the tilemap */
    public boolean tiledMapLayerHasTilesAt(Rectangle r,TiledMapTileLayer layer){
        Vector2 rPos = new Vector2();
        rPos = r.getPosition(rPos);
        Vector2 xy = rPos.cpy();
        Vector2 xwy = rPos.cpy();
        Vector2 xyh = rPos.cpy();
        Vector2 xwyh = rPos.cpy();


        xwy.add(r.width, 0);
        xyh.add(0,r.height);
        xwyh.add(r.width,r.height);
        if(getTileAtPosition(xy,layer)!=null)
            return true;
        else if (getTileAtPosition(xwy,layer)!= null)
            return true;
        else if (getTileAtPosition(xyh,layer)!= null)
            return true;
        else if (getTileAtPosition(xwyh,layer)!=null)
            return true;

        return false;
    }

    public void spawnBomb(Vector2 position,int strength){
        bombs.add(new Bomb(position,strength));

    }

    public ArrayList<Player> getPlayers(){
        return players;
    }
    public Player getPlayer_local() {
        return player_local;
    }

    public ArrayList<Bomb> getBombs() {
        return bombs;
    }
}
