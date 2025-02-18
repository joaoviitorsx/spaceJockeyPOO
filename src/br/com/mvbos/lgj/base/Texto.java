package br.com.mvbos.lgj.base;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Texto extends Elemento {

	private Font fonte;

	public Texto() {
		// Carregar a fonte TTF
		try {
			Font fonteTTF = Font.createFont(Font.TRUETYPE_FONT, new File("src\\Fonte\\mk1.ttf"));
			fonte = fonteTTF.deriveFont(Font.PLAIN, 28); // Definir estilo e tamanho da fonte
		} catch (IOException | FontFormatException e) {
			System.err.println("Erro ao carregar a fonte TTF: " + e.getMessage());
			// Em caso de erro, usar a fonte padrão
			fonte = new Font("Tahoma", Font.PLAIN, 16);
		}
	}

	public Texto(Font fonte) {
		this.fonte = fonte;
	}

	public void desenha(Graphics2D g, String texto) {
		desenha(g, texto, getPx(), getPy());
	}

	public void desenha(Graphics2D g, String texto, int px, int py) {
		if (getCor() != null)
			g.setColor(getCor());

		g.setFont(fonte);
		g.drawString(texto, px, py);
	}

	public Font getFonte() {
		return fonte;
	}

	public void setFonte(Font fonte) {
		this.fonte = fonte;
	}
}

