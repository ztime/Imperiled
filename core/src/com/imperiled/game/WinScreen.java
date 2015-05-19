package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
 * A winner screen! Should be displayed when the player has 
 * finished the game
 *
 */
public class WinScreen implements Screen{
	final Imperiled game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private float elapsedTime;
	
	/**
	 * Creates a new win screen!
	 * @param game
	 */
	public WinScreen(final Imperiled game){
		this.game = game;
		
		//New camera with the same resolution as our window
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		
		font = new BitmapFont();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
	}

	/**
	 * For now, this just renders a few lines of text and if you
	 * press any key it starts a new game
	 */
	@Override
	public void render(float delta) {
		elapsedTime += delta;
		camera.update();
		//Clear screen
		Gdx.gl.glClearColor(0,0,0,1); //Black
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// draw to screen
		int middleOfScreenY = Gdx.graphics.getHeight() / 2;
		int middleOfScreenX = Gdx.graphics.getWidth() / 2;
		batch.begin();
		font.draw(batch, "Congratulations!", middleOfScreenX - 20, middleOfScreenY);
		font.draw(batch, "You finished the game!", middleOfScreenX -15, middleOfScreenY + 20);
		font.draw(batch, "Press any key to play again...", middleOfScreenX - 40, middleOfScreenY - 50);
		batch.end();
		
		//check if we should switch screens 
		if((Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.ANY_KEY)) && elapsedTime > 2.0f){
			//start default map
			this.game.map = this.game.startMap;
			this.game.playerHealth = 100;
			game.setScreen(new MainGameScreen(game));
			dispose();
			return; //just to make sure we don't render anything more
		}
	}

	/**
	 * If the screen is resized we need to set the camera 
	 * to the new screen and with
	 */
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false);
		
	}
	
	/**
	 * Clears up memory from font and camera
	 */
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		
	}
	
	@Override
	public void show() {
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
}