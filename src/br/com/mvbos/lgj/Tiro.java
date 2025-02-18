package br.com.mvbos.lgj;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import br.com.mvbos.lgj.base.Elemento;

import javax.sound.sampled.*;

public class Tiro extends Elemento {

	private boolean inimigo;
	private int vel;  // Velocidade do tiro

	public Tiro() {
		setLargura(5);
		setAltura(5);
		this.vel = 16; // Velocidade padrão do tiro
	}

	public void setVel(int vel) {
		this.vel = vel;
	}

	public int getVel() {
		return vel;
	}

	@Override
	public void atualiza() {
		if (isAtivo()) {
			incPx(inimigo ? vel : -vel);
		}
	}

	@Override
	public void desenha(Graphics2D g) {
		if (!isAtivo())
			return;


		g.setColor(inimigo ? Color.RED : Color.WHITE);
		g.fillRect(getPx(), getPy(), getLargura(), getAltura());
	}

}

