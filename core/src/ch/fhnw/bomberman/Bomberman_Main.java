package ch.fhnw.bomberman;

import ch.fhnw.bomberman.Entities.Bomb;
import ch.fhnw.bomberman.Entities.EntityManager;
import ch.fhnw.bomberman.Entities.Player;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

public class Bomberman_Main extends ApplicationAdapter {
    SpriteBatch batch;
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    private final float UNIT_SCALE =1/16f;
    OrthographicCamera cam;
    EntityManager entityManager;
    long uptime;
    Texture playerIMG;
    Texture bombIMG;
    Texture koIMG;
    TiledMapTileLayer breakableMapLayer;

    @Override
	public void create () {
        batch  = new SpriteBatch();
        map = new TmxMapLoader().load("flugzeugbaumhaus.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        uptime = TimeUtils.millis();

        MapLayer test = map.getLayers().get("Breakable");
        if(test == null){
            System.err.println("The required map layer \"Breakable\" is not available. Please check the map-file");
            throw new IllegalStateException();
        }
        breakableMapLayer = (TiledMapTileLayer)map.getLayers().get("Breakable");



        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();


        cam = new OrthographicCamera(breakableMapLayer.getWidth(),breakableMapLayer.getHeight());
        cam.setToOrtho(false,w,h);
        //cam.position.set(w/2f,h/2f,0);
        cam.update();
        entityManager = new EntityManager(map);
        initKeyBoardInputHandler();
        playerIMG = new Texture("all_pikachus.bmp");
        bombIMG = new Texture("bomb.jpg");
        koIMG = new Texture("ko.jpg");
	}

    private void initKeyBoardInputHandler(){
        Gdx.input.setInputProcessor(new InputAdapter(){
            public boolean keyDown (int key) {
                Player player_local = entityManager.getPlayer_local();
                switch (key){
                    case Keys.UP:
                        player_local.setUpMove(true);
                        break;
                    case Keys.DOWN:
                        player_local.setDownMove(true);
                        break;
                    case Keys.LEFT:
                        player_local.setLeftMove(true);
                        break;
                    case Keys.RIGHT:
                        player_local.setRightMove(true);
                        break;
                    case Keys.SPACE:
                        entityManager.spawnBomb(player_local.getPosition().cpy().add(player_local.getWIDTH()/2,player_local.getHEIGHT()/2),
                                player_local.getCurrentBombPower());
                }
                return true;
            }
            public boolean keyUp (int key) {
                Player player_local = entityManager.getPlayer_local();
                switch (key){
                    case Keys.UP:
                        player_local.setUpMove(false);
                        break;
                    case Keys.DOWN:
                        player_local.setDownMove(false);
                        break;
                    case Keys.LEFT:
                        player_local.setLeftMove(false);
                        break;
                    case Keys.RIGHT:
                        player_local.setRightMove(false);
                        break;
                }
                return true;
            }
        });
    }

	@Override
	public void render () {
        entityManager.update();

        cam.position.set(entityManager.getPlayer_local().getPosition().x + entityManager.getPlayer_local().getWIDTH()/2,
               entityManager.getPlayer_local().getPosition().y + entityManager.getPlayer_local().getHEIGHT()/2,
                0);
        cam.update();
        cam.update(true);
        handleBreakableTiles();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(cam);
        mapRenderer.render();
        batch.setProjectionMatrix(cam.combined);
        mapRenderer.getBatch().begin();

        for(Bomb b : entityManager.getBombs()){
            mapRenderer.getBatch().draw(bombIMG,b.getPosition().x,b.getPosition().y);
        }
        for(Player p : entityManager.getPlayers()){
            if(p.isDead() == false)
                mapRenderer.getBatch().draw(playerIMG,p.getPosition().x,p.getPosition().y, 7, 2, 18, 25); // no animations yet....
            else
                mapRenderer.getBatch().draw(koIMG,p.getPosition().x,p.getPosition().y);
        }
        entityManager.render(mapRenderer.getBatch());

        switch(Gdx.app.getType()){
            case Android:
            if(TimeUtils.millis()-uptime >= 7000){
                Texture fuu = new Texture("fuuu.png");
                mapRenderer.getBatch().draw(fuu,0,0);
            } break;

        }


        mapRenderer.getBatch().end();



	}

    private void handleBreakableTiles(){
                /*
        iterate over every Tile in the tilemap
        The bottom left tile of a map would thus be located at (0,0),
        the top right tile at (tileLayer.getWidth()-1, tileLayer.getHeight()-1).

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("Breakable");
        for (int i = 0; i < layer.getHeight(); i++) {
            for (int j = 0; j < layer.getWidth(); j++) {
                // setting a tile (cell) to null means it points to no image and therefore is not drawn
                layer.setCell(j,i,null);
            }
        }*/
        Vector2 tilePosition = entityManager.worldPositionToTileIndex(entityManager.getPlayer_local().getPosition());

        if(breakableMapLayer.getCell((int)tilePosition.x,(int)tilePosition.y) != null){
            breakableMapLayer.setCell((int)tilePosition.x,(int)tilePosition.y,null);
        }
    }


}
