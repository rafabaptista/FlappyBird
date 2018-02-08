package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.PortableInterceptor.Interceptor;

import java.awt.Color;
import java.util.Random;

import javax.print.attribute.ResolutionSyntax;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; //criar texturas para esse cara executar
	private Texture[] passaros;
	private  Texture fundo;
	private Texture canoBaixo;
	private  Texture canoTopo;
	private Texture gameOver;

	//atributos de config
	private int movimento = 0;

	private float larguraDispositivo;
	private float alturaDispositivo;

	private int statusJog = 0;
	private int pontuacao = 0;




	private Random numRandomico;
	private BitmapFont fonte;
	private  BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	private ShapeRenderer shape;

	private float variacao = 0;

	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private  float alturaEntreCanosRandomica;
	private ResolutionFileResolver.Resolution resolution;
	private boolean marcouPonto = false;

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

		//Camera cam =
		batch = new SpriteBatch();
		numRandomico = new Random();

		passaroCirculo = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoTopo = new Rectangle();
		shape = new ShapeRenderer();

		fonte = new BitmapFont();
		mensagem = new BitmapFont();
		fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		fonte.getData().setScale(6);

		mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		mensagem.getData().setScale(3);

		//passaro = new Texture("passaro1.png");

		passaros = new  Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		gameOver = new Texture("game_over.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");

		//config camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

		viewport = new StretchViewport( VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera );
		//viewport = new FillViewport( VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera ); //mantem proporcao de tela

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;

		posicaoInicialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;

	}

	//é chamado várias vezes
	@Override
	public void render () {

	    camera.update();

	    //limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 6; //calcula a diferença entre um render e outro. É usado para suavizar o movimento

		variacao = (variacao > 2) ? 0 : variacao;

		if(statusJog == 0){

			if(Gdx.input.justTouched()){
				statusJog = 1;
			}

		} else {

			velocidadeQueda++; //queda do passaro automatica
			if(posicaoInicialVertical > 0 || velocidadeQueda < 0) //para não sumir da tela
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda; //queda do passaro

			if(statusJog == 1){
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				if(Gdx.input.justTouched()){
					//Gdx.app.log("Toque", "Toque na Tela");

					velocidadeQueda = -17; // subir em vez de cair
				}

				//verifica se o cano saiu da tela
				if(posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numRandomico.nextInt(400) - 200;

					marcouPonto = false;
				}

				//verifica pontuação
				if(posicaoMovimentoCanoHorizontal < 120){

					if(!marcouPonto){
						pontuacao++;
						marcouPonto = true;
					}

				}

			} else { //game over

				if(Gdx.input.justTouched()){
					statusJog = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo / 2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					marcouPonto = false;
				}

			}

		}

		//configurar dados de projeção da câmera
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);

		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

		batch.draw(passaros[(int)variacao], 120, posicaoInicialVertical); //desenha a textura com posição x/y

		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

		if(statusJog == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);

			mensagem.draw(batch, "Toque para Reiniciar", larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth() / 2,//,mesmo do passaro
				posicaoInicialVertical + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2 // o indicado eh usar o maior...nesse caso eh o width
		);

		retanguloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(),
				canoBaixo.getHeight()
		);

		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(),
				canoTopo.getHeight()
		);

//		//desenhar formas
//		shape.begin(ShapeRenderer.ShapeType.Filled);
//		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius );
//		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
//		shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//		shape.setColor(com.badlogic.gdx.graphics.Color.RED);
//		shape.end();


		//teste de colisão
		if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
				|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ) {
			//Gdx.app.log("Colisão", "Houve colisão");
			statusJog = 2;
		}



	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

}
