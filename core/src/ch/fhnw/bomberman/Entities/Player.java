package ch.fhnw.bomberman.Entities;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by P on 02.04.2015.
 */
public class Player {
    private boolean rightMove;
    private boolean leftMove;
    private boolean upMove;
    private boolean downMove;

    private int currentBombPower = 3;
    private final int maxBombPower = 9;

    public int getCurrentBombPower() {
        return currentBombPower;
    }

    private boolean dead = false;

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {

        return dead;
    }

    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private Vector2 previousPosition = new Vector2();
    private final float PLAYER_SPEED = 2;
    public static final float WIDTH = 18;
    public static final float HEIGHT =25;


    public void setDownMove(boolean input) {
        if(upMove && input)
            upMove = false;
        downMove = input;
    }

    public void setUpMove(boolean input){
        if(downMove && input)
            downMove = false;
        upMove = input;
    }

    public void setRightMove(boolean input){
        if(leftMove && input)
            leftMove = false;
        rightMove = input;
    }

    public void setLeftMove(boolean input){
        if(rightMove && input)
            rightMove = false;
        leftMove = input;

    }

    public void update(){
        previousPosition = position.cpy();

       if(upMove)
           velocity.y += PLAYER_SPEED;
       if(downMove)
            velocity.y -= PLAYER_SPEED;
       if(rightMove)
            velocity.x += PLAYER_SPEED;
       if(leftMove)
            velocity.x -= PLAYER_SPEED;

        position.add(velocity);
        velocity.setZero();
    }

    public void render(){

    }
    public Rectangle getCollissionBox(){
        Rectangle r = new Rectangle();
        r.setPosition(position);
        r.width = getWIDTH();
        r.height = getHEIGHT();
        return r;
    }
    public Player(Vector2 position) {
        this.position = position;
    }
    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public float getHEIGHT() {
        return HEIGHT;
    }

    public float getWIDTH() {
        return WIDTH;
    }
}
