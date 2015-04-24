package ch.fhnw.bomberman.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by P on 12.04.2015.
 */
public class BombExplosions {
    BombExplosions(TiledMapTileLayer unbreakable,TiledMapTileLayer breakable){
        explosionSprite = new Texture("bombexplosions.png");
        this.unbreakable = unbreakable;
        this.breakable = breakable;
    }
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private final TiledMapTileLayer unbreakable;
    private final TiledMapTileLayer breakable;

    private final int offset = 1;
    private final int size = 16;
    private final int nFrames = 5;

    Texture explosionSprite;

    private final int xCenter = 6-1;
    private final int xLane = 5-1;
    private final int xEnd = 3-1;

    private final float laneUpDownRotation = 90;
    private final float endRightRotation = 0;
    private final float endLeftRotation = 180f;
    private final float endDownRotation = 90f;
    private final float endUpRotation = 90;


    public void render(Batch batch){

        ArrayList<Explosion> toRemove = new ArrayList<Explosion>();
        for(Explosion e :explosions){
            e.explosionAnimationDuration -= TimeUtils.millis() - e.lastFrame;
            if(e.explosionAnimationDuration<=0){
                toRemove.add(e);
                continue;
            }

            int currentFrame =(int) e.explosionAnimationDuration / (e.totalExplosionTime / nFrames);
            currentFrame++; // has to be between 1 and 5

            float xOrigin = size * (float)Math.floor(e.center.x/size);
            float yOrigin = size * (float)Math.floor(e.center.y/size);

            batch.draw(explosionSprite,xOrigin,yOrigin,size,size,xCenter*size+xCenter*offset,currentFrame*size+currentFrame*offset,size,size,false,false);

            for (int i = (int)yOrigin/size+1; i <e.upEnd ; i++) {
                batch.draw(explosionSprite,xOrigin+size-2,i*size,1,1,size,size,1,1,
                        laneUpDownRotation,xLane*size+xLane*offset,currentFrame*size+currentFrame*offset,size,size,false,false);
            }
            batch.draw(explosionSprite,xOrigin+size-2,e.upEnd*size,1,1,size,size,1,1,
                    endUpRotation,xEnd*size+xEnd*offset,currentFrame*size+currentFrame*offset,size,size,false,false);


           e.lastFrame = TimeUtils.millis();
        }
        explosions.removeAll(toRemove);


    }
    public void add(Bomb bomb){
        Explosion tmp = new Explosion(bomb.getPosition(),bomb.getPowerOfExplosionInMotherFuckingGoogolTons(),unbreakable,breakable);
        explosions.add(tmp);
    }


    class Explosion{
        public Explosion(Vector2 center, int power,TiledMapTileLayer unbreakable,TiledMapTileLayer breakable) {
            this.center = center;
            this.power = power;
            findLengths(unbreakable,breakable);
            lastFrame = TimeUtils.millis();
        }

        private final int totalExplosionTime = 3000;
        private final int nDestroyableBreakableRocks = 2;
        long explosionAnimationDuration = totalExplosionTime;
        long lastFrame;
        int power;
        Vector2 center;

        int upEnd;
        int rightEnd;
        int downEnd;
        int leftEnd;

        private void findLengths(TiledMapTileLayer unbreakable,TiledMapTileLayer breakable) {
            int xOrigin =(int) (center.x / unbreakable.getTileWidth());
            int yOrigin =(int)(center.y / unbreakable.getTileWidth());
            int xPos=xOrigin;
            int yPos=yOrigin;
            int nDestroyed = 0;

            while((xPos-xOrigin)!=power && unbreakable.getCell(xPos,yOrigin)==null && nDestroyed < nDestroyableBreakableRocks) {
                xPos++;
                if(breakable.getCell(xPos,yOrigin)==null){
                    nDestroyed++;
                }
            }
            rightEnd = xPos;

            xPos=xOrigin;
            nDestroyed = 0;
            while((xOrigin-xPos)!=power && unbreakable.getCell(xPos,yOrigin)==null && nDestroyed < nDestroyableBreakableRocks) {
                xPos--;
                if(breakable.getCell(xPos,yOrigin)==null){
                    nDestroyed++;
                }
            }
            leftEnd = xPos;

            yPos=yOrigin;
            nDestroyed = 0;
            while((yPos-yOrigin)!=power && unbreakable.getCell(xOrigin,yPos)==null && nDestroyed < nDestroyableBreakableRocks) {
                yPos++;
                if(breakable.getCell(xOrigin,yPos)==null){
                    nDestroyed++;
                }
            }
            upEnd = yPos;


            yPos=yOrigin;
            nDestroyed = 0;
            while((yOrigin-yPos)!=power && unbreakable.getCell(xOrigin,yPos)==null && nDestroyed < nDestroyableBreakableRocks) {
                xPos--;
                if(breakable.getCell(xOrigin,yPos)==null){
                    nDestroyed++;
                }
            }
            downEnd = yPos;




        }


    }
}
