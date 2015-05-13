package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	private Skin skin;
	
	/**
	 * Creates a new ui based on preferences set in class
	 */
	public UiWrapper(final Imperiled game){
		//creates a new stage that covers all of the current view
		this.stage = new Stage(new ScreenViewport());
		this.skin = new Skin();
		
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
		ProgressBar.ProgressBarStyle progStyle = new ProgressBar.ProgressBarStyle();
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
	}
	
	public void updateScreen(int width, int height){
		stage.getViewport().update(width, height, true);
	}
	
	/**
	 * Sets the healthbar with the new player health
	 * @param playerHealth
	 */
	public void update(Actor player){
		skin.get("healthBar", ProgressBar.class).setValue((float) player.getHealth());
	}
	
	/**
	 * Draws the ui on screen
	 */
	public void draw(){
		stage.draw();
	}
}
