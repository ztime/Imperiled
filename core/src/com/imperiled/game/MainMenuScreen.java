package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
 * This is the main menu screen. 
 * 
 * @author Jonas Wedin
 *
 */
public class MainMenuScreen implements Screen {
	final Imperiled game;
	private Texture background;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	
	public MainMenuScreen(final Imperiled game){
		//save the super-game object so we can pass it on
		this.game = game;
		//New camera with the same resolution as our window
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		
		font = new BitmapFont();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		
		background = new Texture(Gdx.files.internal("logo.png"));
	}


	@Override
	public void render(float delta) {
		// This apparently fixes scaling
		// Don't ask me why.
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		//Clear screen
		Gdx.gl.glClearColor(48/51f,48/51f,48/51f,1); //#f0f0f0
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// calculate scaling for image 
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float scaleX = background.getWidth() / screenWidth;
		float imgHeight = background.getHeight() / scaleX;
		
		//draw background in top left corner
		batch.begin();
		batch.draw(background, 0, screenHeight - imgHeight, screenWidth, imgHeight);
		font.draw(batch, "Press any key to play . . .", 100, 100);
		batch.end();
		
		//check if we should switch screens 
		if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.ANY_KEY)){
			//start default map
			this.game.map = this.game.startMap;
			//with full health
			this.game.playerHealth = 100;
			game.setScreen(new MainGameScreen(game));
			dispose();
			return; //just to make sure we don't render anything more
		}
		
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
		batch.dispose();
		font.dispose();
		background.dispose();
	}
}
