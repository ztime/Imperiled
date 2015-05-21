package com.imperiled.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

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
	private MapObjects eventObjects;
	private MapObjects markers;
	
	private Player player;
	
	private ArrayList<Actor> actors; //actors , not player
	
	private UiWrapper ui;
	
	private Music music;
	
	//this represents if this is the currently running game map
	private boolean isRunning = true; 
	
	public MainGameScreen(Imperiled game){
		this.game = game;
		PropertyHandler.currentGame = game;
		ui = new UiWrapper(this.game);
		
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
		//set it to a fixed resoultion to fix resizing
		camera = new OrthographicCamera();
		cameraWidth = 533.333f;
		cameraHeight = 400.0f;
		camera.setToOrtho(false, cameraWidth, cameraHeight);
		camera.update();
		//Starting position of camera is 0,0 (lower left corner) of map
		cameraLowerBound = camera.position.y;
		cameraLeftBound = camera.position.x;
		//set batch to render the same as camera
		batch.setProjectionMatrix(camera.combined);
		//load map objects
		collisionObjects = map.getLayers().get("collision").getObjects();
		markers = map.getLayers().get("markers").getObjects();
		PropertyHandler.collisionObjects = collisionObjects;
		
		//events
		// Adds the events associated with this map to the PropertyHandler.
		eventObjects = map.getLayers().get("events").getObjects();
		ArrayList<String> listOfEvents = new ArrayList<String>();
		for(MapObject eventObject : eventObjects) {
			listOfEvents.add(eventObject.getName());
		}
		
		//setup actors in the map
		actors = new ArrayList<Actor>();
		
		//Move player to it's starting position
		//if the "global" variable startPos is set we use that one, otherwise
		//we get the default starting position from map
		Integer startX;
		Integer startY;
		if(game.startPos == null){
			startX = Math.round((Float) markers.get("playerStart").getProperties().get("x"));
			startY = Math.round((Float) markers.get("playerStart").getProperties().get("y"));
		} else {
			startX = (int) game.startPos.x;
			startY = (int) game.startPos.y;
			game.startPos = null; //reset the position
		}
		//remove the player starting position from markers
		markers.remove(markers.get("playerStart"));
		
		player = new Player(startX, startY, 100);
		//change direction if needed
		if(game.startDirection != null){
			player.setDirection(game.startDirection);
			game.startDirection = null;
		}
		
		//spawn enemies from what is left in markers
		Iterator<MapObject> iterMarkers = markers.iterator();
		while(iterMarkers.hasNext()){
			MapObject currentMarker = iterMarkers.next();
			String actorType = currentMarker.getProperties().get("type", String.class);
			String actorName = currentMarker.getName();
			Integer startPosX = Math.round((Float) currentMarker.getProperties().get("x"));
			Integer startPosY = Math.round((Float) currentMarker.getProperties().get("y"));
			// 'this.game.map + "-" + actorName' allows actors to
			// have an equal name on another map.
			this.spawnActor(actorType, this.game.map + "-" + actorName, startPosX, startPosY);
		}
		//add to propertyhandler
		PropertyHandler.newActors(actors);
		//we need to add them to the ui skin
		ui.createEnemyHealthBars(actors);
		PropertyHandler.currentActors.put("player", player);
		new FileParser(this.game.map, listOfEvents);
		new FileParser(this.game.map, actors, 0);
		
		if(music != null) {
			
		}
	}

	@Override
	public void render(float delta) {
		
		//if this is not the current screen we dont want to render
		if(!this.isRunning) {
			// Moved the dispose here temporarily to fix crash
			// the occurs upon death.
			this.dispose();
			return;
		}
		
		//check if we need to return to main menu screen
		if(player.getState() == State.INACTIVE && Gdx.input.isKeyPressed(Keys.ANY_KEY)){
			this.game.setScreen(new MainMenuScreen(this.game));
			//this.dispose(); Crash occurs on windows (maybe all OS?) when the dispose is here.
			PropertyHandler.clearProperties();
			return; // make sure we dont draw anyting more
		}
		
		
		//Everything that needs to change position or do something 
		//needs to to that in update(float delta) , not here.
		//we only want to update if the game is running
		if(!this.game.paused) {
			this.update(delta);
			//if we have changed map we dont want to draw anything more
			if(!this.isRunning){
				return;
			}
		} else {
			//NPC stuff
			ui.updateNPCText();
			if(Gdx.input.isKeyJustPressed(Keys.E) && ui.getInterraction() != null) {
				ui.setInterraction(null);
				game.paused = false;
				ui.updateNPCText();
			}
		}
		
		//we also need to adapt the camera to the players position
		setCameraPosition(player.x, player.y);
		
		//This should run before anything else i rendered on screen
		camera.update();
		
		//And render
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		//-------------------------------------- //
		batch.begin();
		//draw all actors
		for(Actor actor : actors){
			if(actor.getState() != State.INACTIVE){
				actor.draw(batch);
			}
		}
		player.draw(batch);
		batch.end();
		
		/**
		 * This is debug rendering and will only happen is debug flag
		 * is set to true in Imperiled class
		 */
		if(game.debug){
			debugDrawing();
		}
		//ui rendering should always happen last
		ui.draw();
		
		handleMusic();
	}

	/**
	 * Updates things that needs to happen , will circle all actors 
	 * and call update on them 
	 * @param delta
	 */
	public void update(float delta){
		
		//NPC STUFF
		if(Gdx.input.isKeyJustPressed(Keys.E)) {
			NPC npc = checkNPC();
			if(npc != null && npc.getNPCText() != null) {
				ui.setInterraction(npc);
				this.game.paused = true;
				npc.currentDirection = player.currentDirection.getOpposite();
			}
		}
		
		
		//circle all actors and call update
		for(Actor actor : actors){
			actor.update(delta);
		}
		player.update(delta);
		/**
		 * Player control is here now, if we have time
		 * move it to another class with more functionality
		 */
		State newState = State.IDLE; //we start in idle
		Direction newDir = player.getDirection(); 
		//new position 
		int x = player.getX();
		int y = player.getY();
		
		if(player.getState() != State.ATTACKING && 
				player.getState() != State.DEAD &&
				player.getState() != State.INACTIVE){
			//get speed
			int moveDist = Math.round(Gdx.graphics.getDeltaTime() * player.getSpeed());
			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ||
					Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)){
				moveDist = Math.round(Gdx.graphics.getDeltaTime() * player.attackingSpeed);
			}
			if(moveDist > player.getRectangle().width) {
				moveDist = Math.round(player.getRectangle().width);
			} else if(moveDist > player.getRectangle().height) {
				moveDist = Math.round(player.getRectangle().height);
			}
			
			if(Gdx.input.isKeyPressed(Keys.W)){
				y += moveDist;
				newState = State.MOVE;
				newDir = Direction.UP;
			}	
			if(Gdx.input.isKeyPressed(Keys.S)){
				y -= moveDist;
				newState = State.MOVE;
				newDir = Direction.DOWN;
			}
			if(Gdx.input.isKeyPressed(Keys.A)){
				x -= moveDist;
				newState = State.MOVE;
				newDir = Direction.LEFT;
			}
			if(Gdx.input.isKeyPressed(Keys.D)){
				x += moveDist;
				newState = State.MOVE;
				newDir = Direction.RIGHT;
			}
			if(Gdx.input.isKeyPressed(Keys.SPACE)){
				newState = State.ATTACKING;
			}
		}
		
		//set the new values
		player.setDirection(newDir);
		player.setState(newState);
		player.setPosition(x, y);
		
		//move the player back if it needs to 
		this.checkPlayerCollision(); 
		
		//circle trough all the actors and if they are inside
		//the cameras view we check their collision
		for(Actor actr : actors) {
			if(Math.abs(actr.x - player.x) < cameraWidth/2 + 20 ||
					Math.abs(actr.y - player.y) < cameraHeight/2 + 20) {
				actr.getAI().act(collisionObjects, player);
			}
			this.checkActorsCollision(actr);
		}
		
		//--- check events ---
		this.checkEventCollision(player);
		//if we have changed map we dont want to draw anything more
		if(!this.isRunning){
			return;
		}
		for(Actor actor : actors){
			this.checkEventCollision(actor);
		}
		
		//check damage
		this.checkDamage();
		
		//update ui
		ui.update(player, camera, actors);
	}
	
	/**
	 * Checks player and all actors for damage rectangels that
	 * intersects and deals out damage accordingly
	 */
	private void checkDamage(){
		//-- check damage that player does to actors 
		DamageRectangle playerDmgRect = player.getDamageRectangle();
		Iterator<Actor> iterActors = actors.iterator();
		while(iterActors.hasNext()){
			Actor currentActor = iterActors.next();
			if(Intersector.overlaps(playerDmgRect.rectangle, currentActor.getRectangle())){
				currentActor.takeDamage(playerDmgRect.dmg);
				currentActor.currentDirection = player.currentDirection.getOpposite();
			}
		}
		//next we check actors vs player
		Rectangle playerHitBox = player.getRectangle();
		iterActors = actors.iterator();
		while(iterActors.hasNext()){
			Actor currentActor = iterActors.next();
			DamageRectangle actorDmgRect = currentActor.getDamageRectangle();
			if(Intersector.overlaps(actorDmgRect.rectangle, playerHitBox)){
				player.takeDamage(actorDmgRect.dmg);
			}
		}
	}
	/**
	 * Adds a new monster to the map! 
	 * 
	 * @param type	String 	with the type of actor
	 * @param name	String	name of the actor
	 * @param x		Int		Starting position x
	 * @param y		Int		Starting position y
	 */
	private void spawnActor(String type, String name, int x, int y){
		// If the actor has been marked as inactive
		// previously in this map, it does not spawn
		// the actor.
		if(PropertyHandler.inactiveActors.contains(name)) {
			return;
		}
		
		//loads music
		if(type.equals("music")){
			String fileName = "music/" + name.substring(game.map.length() + 1) + ".mp3";
			FileHandle loadMusic = Gdx.files.internal(fileName);
			if(!loadMusic.exists()) {
				System.out.println("Music does not exist: " + fileName);
				return;
			}
			music = Gdx.audio.newMusic(loadMusic);
			music.setVolume(0.5f);
		}
		
		//check that the type is valid
		else if(type.equals("bee")){
			Bee newBee = new Bee(x,y);
			newBee.name = name;
			actors.add(newBee);
		} else if(type.equals("ghost")){
			Ghost newGhost = new Ghost(x,y);
			newGhost.name = name;
			actors.add(newGhost);
		} else if (type.equals("worm")){
			Worm newWorm = new Worm(x,y);
			newWorm.name = name;
			actors.add(newWorm);
		} else if (type.equals("soldier")){
			Soldier newSoldier = new Soldier(x,y);
			newSoldier.name = name;
			actors.add(newSoldier);
		} else if (type.equals("soldier_alt")){
			SoldierAlt newSoldierAlt = new SoldierAlt(x,y);
			newSoldierAlt.name = name;
			actors.add(newSoldierAlt);
		} else if (type.equals("princess")){
			Princess newPrincess = new Princess(x,y);
			newPrincess.name = name;
			actors.add(newPrincess);
		} else {
			System.out.println("Invalid actortype: " + type);
		}
	}
	
	/**
	 * Checks if an actor collides with an event where they are the target
	 * if they are we run the action 
	 * @param actor
	 */
	private void checkEventCollision(Actor actor){
		Rectangle actorHitBox = actor.getRectangle();
		Iterator<MapObject> iterEventObj = eventObjects.iterator();
		while(iterEventObj.hasNext()){
			MapObject currentEvent = iterEventObj.next();
			RectangleMapObject currentEventBox = (RectangleMapObject) currentEvent;
			if(Intersector.overlaps(actorHitBox, currentEventBox.getRectangle())){
				String actorsName = actor.getName();
				String eventName = currentEvent.getName();
				//call event
				String eventTarget = PropertyHandler.currentEvents.get(eventName).eventTarget();
				if(actorsName.equals(eventTarget)){
					PropertyHandler.currentEvents.get(eventName).action();
				}
			}
			//if we have changed map we dont want to do anything more
			if(!this.isRunning){
				return;
			}
		}
	}
	
	/**
	 * Circles through all actors and first check if they collide with
	 * players hit box , then checks if they collide with walls and objects
	 */
	private void checkActorsCollision(Actor currentActor){
		//actors collision checking
		Rectangle playerHitBox = player.getRectangle();
		Rectangle currentActorHitBox = currentActor.getRectangle();
		//first check player
		if(Intersector.overlaps(playerHitBox, currentActorHitBox)){
			currentActor.revertToOldPosition();
			return;
		}
		//then other actors
		for(Actor actr : actors) {
			if(actr == currentActor) {
				continue;
			}
			Rectangle actorHitBox = actr.getRectangle();
			if(Intersector.overlaps(actorHitBox, currentActorHitBox)){
				currentActor.revertToOldPosition(); //moves to old position
				return;
			}
		}
		//lastly map objects
		Iterator<MapObject> iterCollision = collisionObjects.iterator();
		while(iterCollision.hasNext()){
			RectangleMapObject collRect = (RectangleMapObject) iterCollision.next();
			if(Intersector.overlaps(currentActorHitBox, collRect.getRectangle())){
				currentActor.revertToOldPosition();
				return;
			}
		}
	}
	
	/**
	 * Check collisions for player, circles through all map
	 * objects and reverts player to old position if they collide 
	 */
	private void checkPlayerCollision(){
		//Collision check for player
		//Start with collision objects
		Rectangle playerHitBox = player.getRectangle();
		
		Iterator<MapObject> iterCollision = collisionObjects.iterator();
		while(iterCollision.hasNext()){
			RectangleMapObject collisionObject = (RectangleMapObject) iterCollision.next();
			if(Intersector.overlaps(playerHitBox, collisionObject.getRectangle())){
				//Code duplication CLEAN UP LATER!!
				int tempX = player.x;
				player.x = player.oldX;
				if(!Intersector.overlaps(player.getRectangle(), collisionObject.getRectangle())) {
					checkPlayerCollision();
					return;
				}
				player.x = tempX;
				player.y = player.oldY;
				if(!Intersector.overlaps(player.getRectangle(), collisionObject.getRectangle())) {
					checkPlayerCollision();
					return;
				}
				player.revertToOldPosition(); //moves to old position
				return;
			}
		}
		//next is all the actors
		Iterator<Actor> iterActors = actors.iterator();
		while(iterActors.hasNext()){
			Rectangle rectangleActor = iterActors.next().getRectangle();
			if(Intersector.overlaps(playerHitBox, rectangleActor)){
				//Code duplication CLEAN UP LATER!!
				int tempX = player.x;
				player.x = player.oldX;
				if(!Intersector.overlaps(player.getRectangle(), rectangleActor)) {
					checkPlayerCollision();
					return;
				}
				player.x = tempX;
				player.y = player.oldY;
				if(!Intersector.overlaps(player.getRectangle(), rectangleActor)) {
					checkPlayerCollision();
					return;
				}
				player.revertToOldPosition(); //moves to old position
				return;
			}
		}
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
	
	/**
	 * Returns an NPC if the player is
	 * directed towards it and close by
	 * it.
	 */
	private NPC checkNPC() {
		NPC retNPC = null;
		Actor dummy = new DummyActor(player.x, player.y);
		Rectangle rect = player.getRectangle();
		Rectangle dummyRect = dummy.getRectangle();
		if(player.currentDirection == Direction.DOWN) {
			dummy.y -= dummyRect.height;
			dummy.x += rect.width/2 - dummyRect.width/2;
		} else if(player.currentDirection == Direction.UP) {
			dummy.y += rect.height;
			dummy.x += rect.width/2 - dummyRect.width/2;
		} else if(player.currentDirection == Direction.LEFT) {
			dummy.x -= dummyRect.width*1.5;
			dummy.y += rect.height/2 - dummyRect.height/2;
		} else {
			dummy.x += rect.width*1.5;
			dummy.y += rect.height/2 - dummyRect.height/2;
		}
		dummyRect = dummy.getRectangle();
		for(Actor actor : actors) {
			if(Intersector.overlaps(actor.getRectangle(), dummyRect)) {
				if(actor instanceof NPC) {
					retNPC = (NPC) actor;
					break;
				}
			}
		}
		dummy.dispose();
		return retNPC;
	}
	
	private void handleMusic() {
		if(music == null) {
			return;
		}
		if(game.paused && ui.getInterraction() == null) {
			music.pause();
			return;
		}
		music.play();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * This gets called if the screen gets resized
	 * only here should we adapt the ui and camera to the new resolution
	 * 
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		ui.updateScreen(width, height);
		ui.draw();
		camera.setToOrtho(false, cameraWidth, cameraHeight);
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
		this.dispose();
		this.isRunning = false;
	}

	/**
	 * Releases memory for assets that are no longer being used
	 */
	@Override
	public void dispose() {
		map.dispose();
		batch.dispose();
		player.dispose();
		mapRenderer.dispose();
		for(Actor actor : actors){
			actor.dispose();
		}
		ui.dispose();
		if(music != null) {
			music.dispose();
		}
	}
	
	/**
	 * Draws debug boxed around objects loaded in the map 
	 * White is playerboxes / collision boxes
	 * Red is damageboxed
	 * Blue is event boxes
	 */
	private void debugDrawing(){
		ShapeRenderer shRend = new ShapeRenderer();
		shRend.setProjectionMatrix(camera.combined);
		shRend.begin(ShapeType.Line);
		//Render player box
		Rectangle playerBox = player.getRectangle();
		shRend.rect(playerBox.x, playerBox.y, playerBox.width, playerBox.height);
		//Render collision objects loaded from map
		Iterator<MapObject> iter = collisionObjects.iterator();
		while(iter.hasNext()){
			RectangleMapObject obj = (RectangleMapObject) iter.next();
			Rectangle rectangle = obj.getRectangle();
			shRend.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
		//Render all actors boxes
		Iterator<Actor> iterActor = actors.iterator();
		while(iterActor.hasNext()){
			Actor next = iterActor.next();
			Rectangle actorRectangle = next.getRectangle();
			if(actorRectangle != null){
				shRend.rect(actorRectangle.x, actorRectangle.y, actorRectangle.width, actorRectangle.height);
			}
			
		}
		//Render damage boxes
		shRend.setColor(1,0,0,1);
		DamageRectangle playerDmg = player.getDamageRectangle();
		shRend.rect(playerDmg.rectangle.x, playerDmg.rectangle.y, playerDmg.rectangle.width,  playerDmg.rectangle.height);
		//actors dmg boxes
		iterActor = actors.iterator();
		while(iterActor.hasNext()){
			Actor next = iterActor.next();
			Rectangle actorDmgRectangle = next.getDamageRectangle().rectangle;
			if(actorDmgRectangle != null){
				shRend.rect(actorDmgRectangle.x, actorDmgRectangle.y, actorDmgRectangle.width, actorDmgRectangle.height);
			}
		}
		//Render eventboxes
		shRend.setColor(0,1,0,1);
		Iterator<MapObject> iterEventObj = eventObjects.iterator();
		while(iterEventObj.hasNext()){
			RectangleMapObject eventObj = (RectangleMapObject) iterEventObj.next();
			Rectangle eventRectangle = eventObj.getRectangle();
			shRend.rect(eventRectangle.x, eventRectangle.y, eventRectangle.width, eventRectangle.height);
		}
		//Debug drawing done
		
		shRend.end();
		shRend.dispose();
	}

}
