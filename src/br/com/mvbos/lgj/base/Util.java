package br.com.mvbos.lgj.base;

import br.com.mvbos.lgj.Invader;
import br.com.mvbos.lgj.UFO;
import java.awt.Rectangle;
import java.awt.*;

public class Util {
	public Util() {
	}

	public static boolean colide(Elemento a, Elemento b) {
		return a.isAtivo() && b.isAtivo() ? a.getBounds().intersects(b.getBounds()) : false;
	}

	public static boolean colideX(Elemento a, Elemento b) {
		if (a.isAtivo() && b.isAtivo()) {
			Rectangle boundsA = a.getBounds();
			Rectangle boundsB = b.getBounds();
			return boundsA.x + boundsA.width >= boundsB.x && boundsA.x <= boundsB.x + boundsB.width;
		} else {
			return false;
		}
	}

	public static boolean colideY(Elemento a, Elemento b) {
		if (a.isAtivo() && b.isAtivo()) {
			Rectangle boundsA = a.getBounds();
			Rectangle boundsB = b.getBounds();
			return boundsA.y + boundsA.height >= boundsB.y && boundsA.y <= boundsB.y + boundsB.height;
		} else {
			return false;
		}
	}

	public static boolean colide(Rectangle bullet, UFO nave) {
		return nave.isAtivo() && bullet.intersects(nave.getBounds());
	}

	public static boolean colide(Elemento tiroUFO, Invader inimigo) {
		if (tiroUFO.isAtivo() && inimigo.isAtivo()) {
			Rectangle boundsInimigo = inimigo.getBounds();
			boundsInimigo.setSize(Invader.tamanhoInimigo, Invader.tamanhoInimigo);
			return tiroUFO.getBounds().intersects(boundsInimigo);
		} else {
			return false;
		}
	}
}