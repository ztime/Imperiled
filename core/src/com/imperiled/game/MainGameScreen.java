package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MainGameScreen implements Screen{
	final Imperiled game;
	
	private SpriteBatch batch;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	
	private Integer mapWidth, mapHeight; 
	private float cameraWidth, cameraHeight;
	private Float cameraLowerBound, cameraLeftBound;
	
	private MapObjects collisionObjects;
	
	//Settings
	private float SCALE_WIDTH = 1.2f;
	
	public MainGameScreen(Imperiled game){
		this.game = game;
		
		batch = new SpriteBatch();
		//setup map
		map = new TmxMapLoader().load("map/" + this.game.map + ".tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
		//set map size
		mapHeight = map.getProperties().get("tilewidth", Integer.class) 
				* map.getProperties().get("height", Integer.class);
		mapWidth = map.getProperties().get("tilewidth", Integer.class) 
				* map.getProperties().get("width", Integer.class);
		//set camera viewport to smaller than resolution of window
		//sort of like a zoom 
		cameraWidth = Gdx.graphics.getWidth() / SCALE_WIDTH;
		cameraHeight = Gdx.graphics.getHeight() * (cameraWidth / Gdx.graphics.getWidth());
		camera = new OrthographicCamera();
		camera.setToOrtho(false, cameraWidth, cameraHeight);
		camera.update();
		//Starting position of camera is 0,0 (lower left corner) of map
		cameraLowerBound = camera.position.y;
		cameraLeftBound = camera.position.x;
		//set batch to render the same as camera
		batch.setProjectionMatrix(camera.combined);
		
		//load map objects
		collisionObjects = map.getLayers().get("collision").getObjects();
	}

	@Override
	public void render(float delta) {
		//This should run before anything else i rendered on screen
		//First adjust camera position
		//setCameraPosition(newX,newY);
		camera.update();
		//And render
		mapRenderer.setView(camera);
		mapRenderer.render();
		//-------------------------------------- //
	}

	/**
	 * Sets cameras new position in the map, checks so it's not out
	 * of bounds. And if it is, it moves it
	 * @param x
	 * @param y
	 */
	private void setCameraPosition(float x, float y){
		if(x < cameraLeftBound){
			x = cameraLeftBound;
		} else if( x + cameraWidth > cameraLeftBound + mapWidth){
			x = cameraLeftBound + mapWidth - cameraWidth;
		}
		if(y < cameraLowerBound){
			y = cameraLowerBound;
		} else if(y + cameraHeight > cameraLowerBound + mapHeight){
			y = cameraLowerBound + mapHeight - cameraHeight;
		}
		camera.position.set(x,y,0);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		map.dispose();
		batch.dispose();
	}

}
