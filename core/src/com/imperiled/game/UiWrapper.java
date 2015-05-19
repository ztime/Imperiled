package com.imperiled.game;

import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A wrapper around a Ui interface
 * 
 * @author jonas wedin
 * @verision 2015-05-12
 */
public class UiWrapper {

	private Stage stage;
	private Stage enemyStage;
	private Skin skin;
	private Skin enemySkin;
	
	private ProgressBar.ProgressBarStyle progStyle;

	private int counter;
	private String bubbleText;
	private Label bubbleLabel;
	private NPC currentInterraction;
	
	/**
	 * Creates a new ui based on preferences set in class
	 */
	public UiWrapper(final Imperiled game){
		//creates a new stage that covers all of the current view
		this.stage = new Stage(new ScreenViewport());
		this.enemyStage = new Stage(new ScreenViewport());
		this.skin = new Skin();
		this.enemySkin = new Skin();
		
		//we need to recive input for buttons
		Gdx.input.setInputProcessor(stage);
		
		//create a few textures to work with
		Pixmap pixmap = new Pixmap(1,1,Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		pixmap = new Pixmap(10,10,Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("whiteBig", new Texture(pixmap));
		
		//Store a default font
		skin.add("default", new BitmapFont());
		
		//Store a default label style
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = skin.getFont("default");
		labelStyle.fontColor = Color.WHITE;
		skin.add("default", labelStyle);
		
		//Store a default progressbar style
		//we make the knob the same color as the background, that way it looks
		//like the progressbar goes all the way down 
		progStyle = new ProgressBar.ProgressBarStyle();
		progStyle.background = skin.newDrawable("whiteBig", Color.BLACK);
	    progStyle.knobBefore = skin.newDrawable("whiteBig", Color.GREEN);
	    progStyle.knob = skin.newDrawable("white", Color.GREEN);
	    skin.add("default-horizontal", progStyle);
	    
	    //default button style
	    TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		
	    //create a new table that fills the screen
	    Table table = new Table();
	    //this fills the viewport with the table
	    table.setFillParent(true);
	    table.setDebug(game.debug);
	    
	    stage.addActor(table);
	    
	    //creates the enteties 
	    table.add(new Label("Health:", skin));
	    ProgressBar healthBar = new ProgressBar(0,100,1,false,progStyle);
	    healthBar.setSize(100, 10);
	    healthBar.setValue(100);
	    skin.add("healthBar", healthBar);
	    table.add(healthBar);
	    final TextButton pauseButton = new TextButton("Pause", skin);
	    table.add(pauseButton).align(Align.right).expandX();
	    //position the table on screen
	    table.left().top().pad(10);
	    
	    //we create a listener to the button
	    pauseButton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event,
					com.badlogic.gdx.scenes.scene2d.Actor actor) {
				if(game.paused){
					pauseButton.setText("Pause");
					game.paused = false;
				} else {
					pauseButton.setText("Paused");
					game.paused = true;
				}
			}
	    });
	    
	    
	    //NPC TEXT
	    Table textBubble = new Table();
	    //this fills the viewport with the table
	    textBubble.setFillParent(true);
	    textBubble.setDebug(game.debug);
	    counter = 0;
	    stage.addActor(textBubble);
	    //adds the label that contains the text
	    bubbleLabel = new Label("", skin);
	    textBubble.add(bubbleLabel);
	    textBubble.center().bottom().pad(10);
	    //NPC TEXT
	}
	
	/**
	 * This updates all the enemy healthbars.
	 * 
	 * If the enemy is within the cameras view, it checks if it's still alive
	 * and if it is the healthBar is set to visible and updates health & position
	 */
	private void updateEnemyScreen(Camera currentCamera, ArrayList<Actor> currentActors){
		Iterator<Actor> actors = currentActors.iterator();
		
		while(actors.hasNext()){
			Actor currentActor = actors.next();
			//only draw a health bar if its not the player/npc and it's still active
			if(currentActor.isActive() && (currentActor instanceof Enemy)){
				int xPos = (int) currentActor.getRectangle().x;
				int yPos = (int) currentActor.getRectangle().y + (int) currentActor.getRectangle().height + 15; //10 is for padding
				Vector3 screenCoords = new Vector3(xPos, yPos, 0);
				if(currentCamera.frustum.pointInFrustum(screenCoords)){
					//transform to coordinates for stage
					Vector3 stageCoords = currentCamera.project(screenCoords);
					ProgressBar currentBar = enemySkin.get(currentActor.name, ProgressBar.class);
					currentBar.setVisible(true);
					currentBar.setPosition(stageCoords.x, stageCoords.y);
					currentBar.setValue(currentActor.health);
				} else {
					//ok so the enemy is not on the screen but they are still alive, se simply dont 
					//draw the healthBar
					enemySkin.get(currentActor.name, ProgressBar.class).setVisible(false);
				}
			} else if((currentActor instanceof Enemy)){
				//so the actor is not active BUT is still an enemy, so the health bar 
				//should never show again
				enemySkin.get(currentActor.name, ProgressBar.class).setVisible(false);
			}
		}
	}
	
	/**
	 * This initilazies all the enemyHealthBars for all enemies in the map
	 * and sets values according to max hp and such. 
	 * 
	 * 
	 */
	public void createEnemyHealthBars(ArrayList<Actor> currentActors){
		Iterator<Actor> actors = currentActors.iterator();
		while(actors.hasNext()){
			Actor currentActor = actors.next();
			if(currentActor instanceof Enemy){
				ProgressBar enemyHealthBar = new ProgressBar(0, currentActor.maxHP, 1, false, progStyle);
				float width = currentActor.getRectangle().width + 10;
				enemyHealthBar.setSize(width, 10);
				enemyHealthBar.setValue((float) currentActor.health);
				//These settings dont matter, we will update them if we need to
				enemyHealthBar.setPosition(0,0);
				enemyHealthBar.setVisible(false);
				
				enemyStage.addActor(enemyHealthBar);
				enemySkin.add(currentActor.name, enemyHealthBar);
			}
		}
	}
	
	/**
	 * Adjusts the ui viewport to the new screen size
	 * @param width
	 * @param height
	 */
	public void updateScreen(int width, int height){
		stage.getViewport().update(width, height, true);
		enemyStage.getViewport().update(width, height, true);
	}
	
	/**
	 * Sets the healthbar with the new player health
	 * @param playerHealth
	 */
	public void update(Actor player, Camera camera, ArrayList<Actor> actors){
		skin.get("healthBar", ProgressBar.class).setValue((float) player.getHealth());
		this.updateEnemyScreen(camera, actors);
		
		// NPC STUFF
		//
		if(currentInterraction == null) {
			bubbleLabel.setText("");
			counter = 0;
			return;
		}
		bubbleText = currentInterraction.getNPCText().get("default");
		if(PropertyHandler.currentGame.paused) {
			if(counter < bubbleText.length()*2) {
				counter++;
			}
			bubbleText = bubbleText.substring(0, counter/2);
			//if text is too long this will split it
			//up into several lines
			if(counter > 80) {
				charloop:
				for(int i = 40; i < bubbleText.length(); i += 40) {
					//if line does not end with a whitespace,
					//try to go back a little bit and one
					//and do a linebreak there instead
					while(bubbleText.charAt(i) != ' ') {
						if(i < 2) {
							continue charloop;
						}
						i--;
					}
					bubbleText = bubbleText.substring(0, i) + "\n" + bubbleText.substring(i+1, bubbleText.length());
				}
			}
			bubbleLabel.setText(bubbleText);
		}
		//
		// NPC STUFF
	}
	
	public void setInterraction(NPC npc) {
		this.currentInterraction = npc;
	}
	
	/**
	 * Draws the ui on screen
	 */
	public void draw(){
		stage.draw();
		enemyStage.draw();
	}
	
	/**
	 * clears the current skins / stages from memory
	 */
	public void dispose(){
		stage.dispose();
		skin.dispose();
		enemyStage.dispose();
		enemySkin.dispose();
	}
}
