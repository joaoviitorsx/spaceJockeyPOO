package br.com.mvbos.lgj;

import br.com.mvbos.lgj.base.Elemento;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.List;
import javax.swing.Timer;

public class UFO extends Elemento {

	private final int tamanhoUFO = 100;
	private int vidas;
	private BufferedImage imagem1;
	private BufferedImage imagem2;
	private BufferedImage imagemAtual;
	private BufferedImage imagemVida;
	private Timer timer;
	private boolean usarImagem1;
	private boolean morrendo;

	private BufferedImage imagemExplosao1;
	private BufferedImage imagemExplosao2;
	private BufferedImage imagemExplosao3;
	private BufferedImage imagemExplosaoAtual;
	private int contadorExplosao;
	private int contadorExplosaoMax = 32; // Duração da animação de explosão
	private boolean explosaoFinalizada = false;

	public boolean isExplosaoFinalizada() {
		return explosaoFinalizada;
	}
	public UFO() {
		this.vidas = 3;
		setLargura(tamanhoUFO);
		setAltura(tamanhoUFO);

		// Carregar as imagens
		carregarImagens();

		imagemAtual = imagem1;
		usarImagem1 = true;

		// Configurar o timer para alternar as imagens a cada meio segundo (500 milissegundos)
		timer = new Timer(150, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				usarImagem1 = !usarImagem1;
				imagemAtual = usarImagem1 ? imagem1 : imagem2;
			}
		});
		timer.start();
	}
	public int getVidas() {
		return this.vidas;
	}

	private void carregarImagens() {
		imagem1 = carregarImagem("src\\Sprite\\UFO1.png");
		imagem2 = carregarImagem("src\\Sprite\\UFO2.png");
		imagemVida = carregarImagem("src\\Sprite\\VIDA.png");

		imagemExplosao1 = carregarImagem("src\\Sprite\\EXPLOSAOUFO1.png");
		imagemExplosao2 = carregarImagem("src\\Sprite\\EXPLOSAOUFO2.png");
		imagemExplosao3 = carregarImagem("src\\Sprite\\EXPLOSAOUFO3.png");

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
			imagemExplosaoAtual = imagemExplosao1; // Inicia a animação de explosão
			Jogo.setCriacaoInimigosParada(true); // Para os inimigos
		}
	}

	public void configurarPosicaoInicial() {
		int alturaUFO = getAltura();
		setPy(Jogo.janelaAltura - Jogo.chao - alturaUFO - 20);
	}


	public void atualiza(List<Invader> inimigos) {
		// Atualização de limites de movimentação
		if (getPx() < 0) {
			setPx(0);
		} else if (getPx() + getLargura() > Jogo.janelaLargura) {
			setPx(Jogo.janelaLargura - getLargura());
		}

		if (getPy() < 0) {
			setPy(0);
		} else if (getPy() + getAltura() > Jogo.janelaAltura) {
			setPy(Jogo.janelaAltura - getAltura());
		}

		if (morrendo) {
			contadorExplosao++;
			if (contadorExplosao == 1) {
				Jogo.setInimigosParados(true); // Para os inimigos quando a animação de explosão começa
				Jogo.setCriacaoInimigosParada(true); // Para a criação de inimigos
			}
			if (contadorExplosao >= contadorExplosaoMax) {
				Jogo.setInimigosParados(false); // Faz os inimigos voltarem a se mover quando a animação de explosão termina
				Jogo.setCriacaoInimigosParada(true); // Mantém a criação de inimigos parada
				setAtivo(false); // Mantém o UFO inativo
			}
			// Verificar se todos os inimigos estão fora da tela
			boolean todosInimigosForaDaTela = true;
			for (Invader inimigo : inimigos) {
				if (inimigo.getPx() + inimigo.getLargura() > 0) {
					todosInimigosForaDaTela = false;
					break;
				}
			}
			if (todosInimigosForaDaTela) {
				setAtivo(true);
				morrendo = false;
				contadorExplosao = 0;
				imagemAtual = imagem1;
				explosaoFinalizada = true;
				Jogo.setCriacaoInimigosParada(false);
				configurarPosicaoInicial();
			}
		}
	}

	@Override
	public void desenha(Graphics2D g) {
		int vidas = getVidas();
		int larguraVida = 80;
		int alturaVida = 80;
		int espacoEntreVidas = 10;

		int larguraTotalVidas = (vidas * larguraVida) + ((vidas - 1) * espacoEntreVidas);
		int xInicial = (Jogo.janelaLargura - larguraTotalVidas) / 2;
		int yInicial = Jogo.janelaAltura - alturaVida - 30; // 30 pixels acima da borda inferior

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
			if (contadorExplosao >= 32) {
				setAtivo(true);
				morrendo = false;
				contadorExplosao = 0;
				imagemAtual = imagem1;
				explosaoFinalizada = true;
				configurarPosicaoInicial();
			}
			return;
		}

		if (!isAtivo()) {
			return;
		}


		// Desenhar a imagem atual do UFO
		int x = getPx();
		int y = getPy();
		g.drawImage(imagemAtual, x, y, getLargura(), getAltura(), null);

		// Desenhar vidas
		for (int i = 0; i < vidas; i++) {
			g.drawImage(imagemVida, xInicial + i * (larguraVida + espacoEntreVidas), yInicial, larguraVida, alturaVida, null);
		}
	}

	public void setVidas(int i) {
		vidas = i;
	}
}
