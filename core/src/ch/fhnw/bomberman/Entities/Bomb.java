package ch.fhnw.bomberman.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by P on 12.04.2015.
 */
public class Bomb {
    private static final long fuseLenght = 5000;
    private static final float HEIGHT = 19;
    private static final float WIDTH = 14;

    private long timeUntilExplosion;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private int powerOfExplosionInMotherFuckingGoogolTons;
    private boolean exploded = false;

    private long lastUpdateTime;
    private long remaingBombExplosionAnimationTime;
    private final long explosionAnimationTime = 3000;

    public int getPowerOfExplosionInMotherFuckingGoogolTons() {
        return powerOfExplosionInMotherFuckingGoogolTons;
    }

    public boolean isExploded() {

        return exploded;
    }

    public Vector2 getPosition() {

        return position;
    }

    public Bomb(Vector2 position, int powerOfExplosionInMotherFuckingGoogolTons) {
        this.position = position.add(-WIDTH/2,-HEIGHT/2);

        this.powerOfExplosionInMotherFuckingGoogolTons = powerOfExplosionInMotherFuckingGoogolTons;
        timeUntilExplosion = fuseLenght;
        lastUpdateTime = TimeUtils.millis();
    }

    private void explode(){
        exploded = true;

    }

    public void update(){
        timeUntilExplosion -= (TimeUtils.millis() - lastUpdateTime);
        if(timeUntilExplosion<=0){
            explode();
        }

    }
}
