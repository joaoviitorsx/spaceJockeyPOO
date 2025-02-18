package br.com.mvbos.lgj;

import br.com.mvbos.lgj.base.Elemento;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
public class Invader extends Elemento {

	public static int tamanhoInimigo = 130;
	private static final int tamanhoTiro = 5;
	private static final int velocidadeTiro = 60;
	private static final int velocidadeTiroInimigo = velocidadeTiro;
	private Tipos tipo;
	private boolean aberto;
	private ArrayList<Rectangle> bullets;
	private Random random;
	private boolean jaAtirou;
	private int velocidadeInimigo;
	private BufferedImage imagem1;
	private BufferedImage imagem2;
	private BufferedImage imagemAtual;
	private BufferedImage imagemExplosao1;
	private BufferedImage imagemExplosao2;
	private BufferedImage imagemExplosao3;
	private BufferedImage imagemExplosaoAtual;
	private int contadorExplosao;
	private boolean morrendo;
	private int contadorExplosaoMax = 9; // Duração da animação de explosão
	private Timer timer;

	public boolean isMorrendo() {
		return morrendo;
	}
	public int getVelocidadeInimigo() {
		return velocidadeInimigo;
	}

	public void setVelocidadeInimigo(int velocidadeInimigo) {
		this.velocidadeInimigo = velocidadeInimigo;
	}

	public enum Tipos {
		AVIAO, HELICOPTERO, CANHAO, BALAO, CASA, ARVORE, DRONE
	}

	public Invader(Tipos tipo) {
		this.tipo = tipo;
		this.bullets = new ArrayList<>();
		this.random = new Random();
		this.jaAtirou = false;
		this.velocidadeInimigo = 7;

		setLargura(tamanhoInimigo);
		setAltura(tamanhoInimigo);
		setAtivo(true);

		// Carregar as imagens correspondentes ao tipo
		carregarImagens(tipo);

		// Alternar entre as imagens a cada meio segundo
		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alternarImagem();
			}
		});
		timer.start();
	}

	private void carregarImagens(Tipos tipo) {
		String caminhoImagem1 = "";
		String caminhoImagem2 = "";

		switch (tipo) {
			case AVIAO:
				caminhoImagem1 = "src\\Sprite\\AVIAO1.png";
				caminhoImagem2 = "src\\Sprite\\AVIAO2.png";
				break;
			case HELICOPTERO:
				caminhoImagem1 = "src\\Sprite\\HELICOPTERO1.png";
				caminhoImagem2 = "src\\Sprite\\HELICOPTERO2.png";
				break;
			case CANHAO:
				caminhoImagem1 = "src\\Sprite\\CANHAO1.png";
				caminhoImagem2 = "src\\Sprite\\CANHAO2.png";
				break;
			case DRONE:
				caminhoImagem1 = "src\\Sprite\\DRONE.png";
				caminhoImagem2 = "src\\Sprite\\DRONE.png";
				break;
			case BALAO:
				caminhoImagem1 = "src\\Sprite\\BALAO.png";
				caminhoImagem2 = "src\\Sprite\\BALAO.png";
				break;
			case CASA:
				caminhoImagem1 = "src\\Sprite\\CASA.png";
				caminhoImagem2 = "src\\Sprite\\CASA.png";
				break;
			case ARVORE:
				caminhoImagem1 = "src\\Sprite\\ARVORE.png";
				caminhoImagem2 = "src\\Sprite\\ARVORE.png";
				break;
		}

		String caminhoImagemExplosao1 = "src\\Sprite\\EXPLOSAO1.png";
		String caminhoImagemExplosao2 = "src\\Sprite\\EXPLOSAO2.png";
		String caminhoImagemExplosao3 = "src\\Sprite\\EXPLOSAO3.png";

		imagem1 = carregarImagem(caminhoImagem1);
		imagem2 = carregarImagem(caminhoImagem2);
		imagemExplosao1 = carregarImagem(caminhoImagemExplosao1);
		imagemExplosao2 = carregarImagem(caminhoImagemExplosao2);
		imagemExplosao3 = carregarImagem(caminhoImagemExplosao3);

		imagemAtual = imagem1;
	}

	public BufferedImage carregarImagem(String caminhoImagem) {
		try {
			return ImageIO.read(new File(caminhoImagem));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void atingido() {
		if (!morrendo) {
			morrendo = true;
			contadorExplosao = 0;
		}
	}

	private void alternarImagem() {
		if (imagemAtual == imagem1) {
			imagemAtual = imagem2;
		} else {
			imagemAtual = imagem1;
		}
	}

	@Override
	public void atualiza() {
		aberto = !aberto;
		setPx(getPx() - 8); // Move para a esquerda

		if (morrendo) {
			contadorExplosao++;
			if (contadorExplosao >= contadorExplosaoMax) {
				setAtivo(false);
			}
		}

		if (tipo == Tipos.AVIAO || tipo == Tipos.HELICOPTERO || tipo == Tipos.CANHAO || tipo == Tipos.DRONE) {
			// Tiro do inimigo
			if (!jaAtirou) {
				shoot();
				jaAtirou = true;
			}

			// Remove tiros fora da tela
			bullets.removeIf(bullet -> bullet.x < 0);

			// Movimento das balas dos inimigos
			for (Rectangle bullet : bullets) {
				bullet.x -= velocidadeTiroInimigo;
			}
		}
	}

	@Override
	public void desenha(Graphics2D g) {
		if (morrendo) {
			g.drawImage(imagemExplosaoAtual, getPx(), getPy(), getLargura(), getAltura(), null);
			contadorExplosao++;
			if (contadorExplosao <= 4) {
				imagemExplosaoAtual = imagemExplosao1;
			} else if (contadorExplosao <= 8) {
				imagemExplosaoAtual = imagemExplosao2;
			} else if (contadorExplosao <= 12) {
				imagemExplosaoAtual = imagemExplosao3;
			}
			if (contadorExplosao >= 12) {
				setAtivo(false);
				morrendo = false; // Reset the dying state
				contadorExplosao = 0; // Reset the explosion counter
				imagemAtual = imagem1; // Reset the image to the first UFO image
			}
			return;
		}

		if (!isAtivo()) {
			return;
		}

		g.drawImage(imagemAtual, getPx(), getPy(), getLargura(), getAltura(), null);

		if (tipo == Tipos.AVIAO || tipo == Tipos.HELICOPTERO || tipo == Tipos.CANHAO || tipo == Tipos.DRONE) {
			g.setColor(Color.WHITE);
			for (Rectangle bullet : bullets) {
				g.fillRect(bullet.x, bullet.y, tamanhoTiro, tamanhoTiro);
			}
		}
	}

	public int getPremio() {
		switch (tipo) {
			case AVIAO:
				return 150;
			case HELICOPTERO:
				return 150;
			case CANHAO:
				return 150;
			case DRONE:
				return 100;
			case BALAO:
				return 50;
			case CASA:
				return 25;
			case ARVORE:
				return 20;
			default:
				return 0;
		}
	}

	private void shoot() {
		int bulletX = getPx();
		int bulletY = getPy() + tamanhoInimigo / 2 - tamanhoTiro / 2;
		bullets.add(new Rectangle(bulletX, bulletY, tamanhoTiro, tamanhoTiro));
	}

	public ArrayList<Rectangle> getBullets() {
		return bullets;
	}

	public Tipos getTipo() {
		return tipo;
	}

	public static int getChanceTiro() {
		return tamanhoInimigo;
	}

	public static void setChanceTiro(int chanceTiro) {
		Invader.tamanhoInimigo = chanceTiro;
	}

	public boolean isJaAtirou() {
		return jaAtirou;
	}

	public void setJaAtirou(boolean jaAtirou) {
		this.jaAtirou = jaAtirou;
	}
}