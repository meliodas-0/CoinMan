package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.ArrayList;

public class SuperMarioRunClone extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture manRunning[];
	Texture manIdle[];
	Texture manDizzy[];
	Texture fallingMan, jumpingMan;
	Texture coin, bomb;
	Pixmap pixmap;
	Circle manShape;

	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	ArrayList<Circle> coinShapes = new ArrayList<>();
	ArrayList<Circle> bombShapes = new ArrayList<>();

	int i = 0;
	int coinCount = 0, bombCount = 0;
	private int pause = 0;
	int manY = 0;
	float gravity = 0.8f;
	float velocity = 0;
	int score = 0;
	int gameState = 0;
	BitmapFont scoreBitmapFont;
	int pause2 = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		manRunning = new Texture[4];
		manRunning[0] = new Texture("run1.png");
		manRunning[1] = new Texture("run2.png");
		manRunning[2] = new Texture("run3.png");
		manRunning[3] = new Texture("run4.png");
		manDizzy = new Texture[5];
		manDizzy[0] = new Texture("dizzy1.png");
		manDizzy[1] = new Texture("dizzy2.png");
		manDizzy[2] = new Texture("faint1.png");
		manDizzy[3] = new Texture("faint2.png");
		manDizzy[4] = new Texture("faint3.png");
		manIdle = new Texture[2];
		manIdle[0] = new Texture("idle1.png");
		manIdle[1] = new Texture("idle2.png");
		fallingMan = new Texture("jump_fall.png");
		jumpingMan = new Texture("jump_up.png");
		bomb = new Texture("bomb.png");

		scoreBitmapFont = new BitmapFont();
		scoreBitmapFont.setColor(Color.WHITE);
		scoreBitmapFont.getData().setScale(10);

		Pixmap pixmapNew = new Pixmap(Gdx.files.internal("coin.png"));
		pixmap = new Pixmap(128, 128, pixmapNew.getFormat());
		pixmap.drawPixmap(pixmapNew,0, 0, pixmapNew.getWidth(), pixmapNew.getHeight(), 0, 0, pixmap.getWidth(), pixmap.getHeight());
		coin = new Texture(pixmap);
		pixmapNew.dispose();
		pixmap.dispose();
	}

	public void makeCoin(){

		float height = (float) (Math.random() * Gdx.graphics.getHeight());
		coinY.add((int) height);
		coinX.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = (float) (Math.random() * Gdx.graphics.getHeight());
		bombY.add((int) height);
		bombX.add(Gdx.graphics.getWidth());
	}

	public void whenGameIsLive(){

		int width = Gdx.graphics.getWidth();

		if(coinCount < 100){
			coinCount++;
		}else{
			coinCount = 0;
			makeCoin();
		}
		if(bombCount < 250){
			bombCount++;
		}else{
			bombCount = 0;
			makeBomb();
		}

		coinShapes.clear();
		for(int n = 0; n<coinX.size(); n++){
			batch.draw(coin, coinX.get(n), coinY.get(n));
			coinX.set(n, coinX.get(n) - 4);
			coinShapes.add(new Circle(coinX.get(n), coinY.get(n), coin.getWidth()/2 - 30 ));
		}
		bombShapes.clear();
		for(int n = 0; n<bombX.size(); n++){
			batch.draw(bomb, bombX.get(n), bombY.get(n));
			bombX.set(n, bombX.get(n) - 10);
			bombShapes.add(new Circle(bombX.get(n), bombY.get(n), bomb.getWidth()/2 - 30));

		}

		if(Gdx.input.justTouched()){
			velocity = -30;
		}

		velocity += gravity;
		manY -= velocity;
		if(manY <= 0){
			manY = 0;
			batch.draw(manRunning[i], width / 2 - manRunning[i].getWidth(), manY);
			manShape = new Circle(width/2 - manRunning[i].getWidth(),manY + manRunning[i].getHeight()/2, manRunning[i].getWidth() - 10);
		}else{
			if(velocity>0){
				batch.draw(fallingMan, width/2 - fallingMan.getWidth(), manY);
				manShape = new Circle(width/2 - fallingMan.getWidth(),manY + fallingMan.getHeight()/2, fallingMan.getWidth() - 10);

			}else {
				batch.draw(jumpingMan, width/2 - jumpingMan.getWidth(), manY);
				manShape = new Circle(width/2 - jumpingMan.getWidth(),manY + jumpingMan.getHeight()/2, jumpingMan.getWidth() - 10);

			}
		}

		for(int n = 0; n<coinShapes.size(); n++){
			if(Intersector.overlaps(manShape, coinShapes.get(n))){
				Gdx.app.log("Coin", "touched");
				score++;
				coinShapes.remove(n);
				coinX.remove(n);
				coinY.remove(n);

				break;
			}
		}
		for(int n = 0; n<bombShapes.size(); n++){
			if(Intersector.overlaps(manShape, bombShapes.get(n))){
				Gdx.app.log("bomb", "exploded");
				gameState = 2;
			}
		}

	}

	private void clearAll() {

		i = 0;
		coinCount = 0;
		bombCount = 0;
		pause = 0;
		manY = 0;
		gravity = 0.8f;
		velocity = 0;
		score = 0;
		pause2 = 0;

		coinX.clear();
		coinY.clear();
		bombX.clear();
		bombY.clear();
		coinShapes.clear();
		bombShapes.clear();

	}

	@Override
	public void render () {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		batch.begin();
		batch.draw(background, 0, 0, width, height);

		if(gameState == 0){
			if(pause > 10) {
				pause = 0;
				i = (i + 1) % 2;
			}
			batch.draw(manIdle[i/2], width/2 - manIdle[i].getWidth(), manY, manIdle[i].getWidth(), manIdle[i/2].getHeight());
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}
		else if(gameState == 1){
			whenGameIsLive();
			if(pause > 10) {
				pause = 0;
				i = (i+1)%4;
			}
		}else {

			velocity +=gravity;
			manY-= velocity;
			if (manY > 0) {
				if (pause < 15) {
					i = 2;
				}else if(pause < 30){
					i = 3;
				}else{
					i = 4;
				}

				batch.draw(manDizzy[i], width / 2 - manDizzy[i].getWidth(), manY, manDizzy[i].getWidth(), manDizzy[i].getHeight());
			}else{
				manY = 0;
				if (pause2 > 30) {
					pause2 = 0;
					i = (i+1)%2;
				}
				pause2++;
				batch.draw(manDizzy[i], width / 2 - manDizzy[i].getWidth(), manY, manDizzy[i].getWidth(), manDizzy[i].getHeight());
				BitmapFont bitmapFont = new BitmapFont();
				bitmapFont.setColor(Color.WHITE);
				bitmapFont.getData().setScale(5);
				bitmapFont.draw(batch, "Touch to restart the game.", 150, height/2);
			}

			if(Gdx.input.justTouched()){
				gameState = 1;
				clearAll();
			}
		}

		scoreBitmapFont.draw(batch, String.valueOf(score), 100, 200);


		batch.end();
		pause++;

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
