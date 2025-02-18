package br.com.mvbos.lgj.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Elemento  {

	private int px;
	private int py;
	private int largura;
	private int altura;
	private int vel;
	private boolean ativo;
	private Color cor;
	private BufferedImage imagem;

	public Elemento() {
	}

	public Elemento(int px, int py, int largura, int altura) {
		this.px = px;
		this.py = py;
		this.largura = largura;
		this.altura = altura;
	}

	public void atualiza() {
	}


	public void desenha(Graphics2D g) {
		if (imagem != null) {
			g.drawImage(imagem, px, py, largura, altura, null);
		} else {
			g.drawRect(px, py, largura, altura);
		}
	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getPx() {
		return px;
	}

	public void setPx(int px) {
		this.px = px;
	}

	public int getPy() {
		return py;
	}

	public void setPy(int py) {
		this.py = py;
	}

	public int getVel() {
		return vel;
	}

	public void setVel(int vel) {
		this.vel = vel;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}

	public void incPx(int x) {
		px = px + x;
	}

	public void incPy(int y) {
		py = py + y;
	}

	public BufferedImage carregarImagem(String caminho) {
		try {
			this.imagem = ImageIO.read(new File(caminho));
			// Ajustar largura e altura para as dimensões da imagem
			this.largura = imagem.getWidth();
			this.altura = imagem.getHeight();
			// Centralizar a imagem na posição original
			this.px -= largura / 2;
			this.py -= altura / 2;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Novo método para obter os limites do elemento
	public Rectangle getBounds() {
		return new Rectangle(px, py, largura/2, altura/2);
	}
}