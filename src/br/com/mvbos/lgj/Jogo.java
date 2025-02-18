package br.com.mvbos.lgj;

import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.base.Player;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Util;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.*;


public class Jogo extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int FPS = 1000 / 40;
    public static final int janelaAltura = 600;
    public static final int janelaLargura = 800;
    private static final int espacoScore = 60;
    static final int chao = 100;

    //
    private static final int posicaoMediaY = (janelaAltura - chao - espacoScore) / 2;
    private static final int posicaoCimaY = posicaoMediaY - 100;
    private static final int posicaoBaixoY = posicaoMediaY + 100;
    private static final int posicaoTerrestreY = janelaAltura - chao;

    //
    private JPanel tela;
    private Graphics2D g2d;
    private BufferedImage buffer;
    private boolean[] teclas = new boolean[3];
    private Random aleatorio = new Random();
    private boolean gameOver;
    private boolean gameStarted;
    private UFO nave;
    private Elemento tiroUFO;
    private List<Invader> inimigos = new ArrayList<>();
    private List<Elemento> tirosInimigos = new ArrayList<>();
    private int score;
    private boolean inimigoTerrestreAtivo = false;
    private int chanceInimigoTerrestre = 50;
    private Ranking ranking = new Ranking();
    private boolean playerAddedToRanking = false;
    private boolean respawning = false;
    private static boolean inimigosParados = false;
    private static boolean criacaoInimigosParada = false;

    public static void setCriacaoInimigosParada(boolean criacaoInimigosParada) {
        Jogo.criacaoInimigosParada = criacaoInimigosParada;
    }

    public static boolean getCriacaoInimigosParada() {
        return criacaoInimigosParada;
    }

    public static void setInimigosParados(boolean inimigosParados) {
        Jogo.inimigosParados = inimigosParados;
    }

    public static boolean getInimigosParados() {
        return inimigosParados;
    }

    public Jogo() {
        setTitle("Space Jockey");
        setSize(janelaLargura, janelaAltura);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buffer = new BufferedImage(janelaLargura, janelaAltura, BufferedImage.TYPE_INT_RGB);
        g2d = buffer.createGraphics();

        tela = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(buffer, 0, 0, null);
                if (gameOver && playerAddedToRanking) {
                    ranking.exibirRanking(g2d, tela);
                }
            }
        };

        getContentPane().add(tela);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    teclas[0] = true;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    teclas[1] = true;
                } else if (keyCode == KeyEvent.VK_SPACE) {
                    teclas[2] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    teclas[0] = false;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    teclas[1] = false;
                } else if (keyCode == KeyEvent.VK_SPACE) {
                    teclas[2] = false;
                }
            }
        });

        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void loadGame() {
        System.out.println("Carregando jogo...");
        nave = new UFO();
        nave.setVel(10);
        nave.setAtivo(true);
        nave.setPx(100);
        nave.configurarPosicaoInicial();
        nave.setVidas(3);
        gameStarted = false;
        gameOver = false;

        tiroUFO = new Tiro();

        tiroUFO.setVel(15);

        score = 0;

        ranking.carregarRanking();

        inimigos.clear();
        tirosInimigos.clear();

        gameOver = false;

        System.out.println("Jogo carregado.");
    }

    private void criarInimigo() {
        if (respawning) {
            return;
        }
        Invader.Tipos tipo = Invader.Tipos.values()[aleatorio.nextInt(Invader.Tipos.values().length)];

        if (tipo == Invader.Tipos.CASA || tipo == Invader.Tipos.CANHAO || tipo == Invader.Tipos.ARVORE) {
            criarInimigoTerrestre(tipo);
        } else {
            criarInimigoAereo(tipo);
        }
    }

    private void criarInimigoTerrestre(Invader.Tipos tipo) {
        if (inimigoTerrestreAtivo) {
            return;
        }

        if (aleatorio.nextInt(100) >= chanceInimigoTerrestre) {
            return;
        }

        Invader inimigo = new Invader(tipo);
        inimigo.setAtivo(true);

        int posicaoX = janelaLargura + aleatorio.nextInt(200);
        int posicaoY = 415;
        inimigo.setPx(posicaoX);
        inimigo.setPy(posicaoY);
        inimigoTerrestreAtivo = true;

        boolean colisao = true;
        while (colisao) {
            colisao = false;
            for (Invader outroInimigo : inimigos) {
                if (Util.colide(inimigo, outroInimigo)) {
                    inimigo.setPx(inimigo.getPx() + inimigo.getLargura() + 10);
                    colisao = true;
                    break;
                }
            }
        }

        inimigos.add(inimigo);
        System.out.println("Inimigo terrestre criado: " + inimigo.getTipo());
    }

    private void criarInimigoAereo(Invader.Tipos tipo) {
        Invader inimigo = new Invader(tipo);
        inimigo.setAtivo(true);

        int[] posicoesFixasY = {posicaoCimaY, posicaoMediaY, posicaoBaixoY};
        Set<Integer> posicoesOcupadas = new HashSet<>();

        for (Invader inim : inimigos) {
            posicoesOcupadas.add(inim.getPy());
        }

        List<Integer> posicoesDisponiveis = new ArrayList<>();
        for (int posY : posicoesFixasY) {
            if (!posicoesOcupadas.contains(posY)) {
                posicoesDisponiveis.add(posY);
            }
        }

        if (posicoesDisponiveis.isEmpty()) {
            return;
        }

        int posicaoY = posicoesDisponiveis.get(aleatorio.nextInt(posicoesDisponiveis.size()));
        int espacoX = 50;
        int posicaoX = janelaLargura + aleatorio.nextInt(200);

        inimigo.setPx(posicaoX);
        inimigo.setPy(posicaoY);

        boolean colisao = true;

        while (colisao) {
            colisao = false;
            for (Invader outroInimigo : inimigos) {
                if (Util.colide(inimigo, outroInimigo)) {
                    inimigo.setPx(inimigo.getPx() + inimigo.getLargura() + espacoX);
                    colisao = true;
                    break;
                }
            }
        }
        inimigos.add(inimigo);
        System.out.println("Inimigo aereo criado: " + inimigo.getTipo());
    }


    private void perdeuVida() {
        nave.atingido(); // Inicia a animação de morte
        inimigosParados = true; // Para os inimigos
        nave.setVidas(nave.getVidas() - 1);
        try {
            File audioFile = new File("src\\Sounds\\explosion.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Erro ao reproduzir o som de explosão do UFO: " + e.getMessage());
        }

        if (nave.getVidas() <= 0) {
            gameOver = true;
        } else {
            respawning = true;
            nave.setAtivo(false);
        }
    }

    private void checarColisaoBala(Elemento tiro) {
        for (Invader inimigo : inimigos) {
            if (Util.colide(inimigo, tiro)) {
                try {
                    File audioFile = new File("src\\Sounds\\explosion.wav");
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    System.err.println("Erro ao reproduzir o som de explosão: " + e.getMessage());
                }
                tiro.setAtivo(false);
                inimigo.setAtivo(false);
                score += ((Invader) inimigo).getPremio();
                if (inimigo.getTipo() == Invader.Tipos.CASA || inimigo.getTipo() == Invader.Tipos.CANHAO || inimigo.getTipo() == Invader.Tipos.ARVORE) {
                    inimigoTerrestreAtivo = false;
                }
                break;
            }
        }
    }

    public void iniciarJogo() {
        gameStarted = true;
        long nextUpdate = 0;
        long tempoUltimoInimigo = 0;

        while (!gameOver) {
            if (System.currentTimeMillis() >= nextUpdate) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, janelaLargura, janelaAltura);

                g2d.setColor(new Color(16, 75, 154));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(148, 79, 31));
                g2d.fillRect(0, janelaAltura - chao, janelaLargura, chao);

                nave.atualiza(inimigos);

                if (nave.isAtivo()) {
                    if (teclas[0] && nave.getPy() > espacoScore) {
                        nave.incPy(-nave.getVel());
                    } else if (teclas[1] && nave.getPy() < janelaAltura - nave.getAltura() - 80) {
                        nave.incPy(nave.getVel());
                    }

                    if (nave.getPy() < espacoScore) {
                        nave.setPy(espacoScore);
                    } else if (nave.getPy() > janelaAltura - nave.getAltura() - 80) {
                        nave.setPy(janelaAltura - nave.getAltura() - 80);
                    }

                    if (teclas[2] && !tiroUFO.isAtivo()) {
                        tiroUFO.setPx(nave.getPx() + nave.getLargura());
                        tiroUFO.setPy(nave.getPy() + nave.getAltura() / 2 - tiroUFO.getAltura() / 2);
                        tiroUFO.setAtivo(true);
                        try {
                            File audioFile = new File("src\\Sounds\\shoot.wav");
                            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioStream);
                            clip.start();
                        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException o) {
                            System.err.println("Erro ao reproduzir o som de explosão do UFO: " + o.getMessage());
                        }
                    }
                }

                if (respawning && inimigos.isEmpty()) {
                    respawning = false;
                    nave.setAtivo(true);
                    nave.atingido();
                }

                inimigos.removeIf(inimigo -> !inimigo.isAtivo());
                tirosInimigos.removeIf(tiro -> !tiro.isAtivo());

                if (tiroUFO.isAtivo()) {
                    tiroUFO.incPx(tiroUFO.getVel());
                    if (tiroUFO.getPx() > janelaLargura) {
                        tiroUFO.setAtivo(false);
                    }
                    tiroUFO.desenha(g2d);
                }

                List<Invader> inimigosTemp = new ArrayList<>(inimigos);
                for (Invader inimigo : inimigosTemp) {
                    if (!Jogo.getInimigosParados()) {
                        inimigo.incPx(- inimigo.getVelocidadeInimigo());
                        if (inimigo.getPx() < -inimigo.getLargura()) {
                            inimigo.setAtivo(false);
                            if (inimigo.getTipo() == Invader.Tipos.CASA || inimigo.getTipo() == Invader.Tipos.CANHAO || inimigo.getTipo() == Invader.Tipos.ARVORE) {
                                inimigoTerrestreAtivo = false;
                            }
                        }

                        int velocidade = respawning ? inimigo.getVel() + 5 : inimigo.getVel();
                        inimigo.setVel(velocidade);

                        //tiro para inimigos
                        if ((inimigo.getTipo() == Invader.Tipos.HELICOPTERO || inimigo.getTipo() == Invader.Tipos.AVIAO || inimigo.getTipo() == Invader.Tipos.CANHAO || inimigo.getTipo() == Invader.Tipos.DRONE)
                                && !inimigo.isJaAtirou() && nave.getPy() == inimigo.getPy()) { // Checar se UFO está na mesma linha horizontal
                            if (aleatorio.nextInt(100) < Invader.getChanceTiro()) {
                                Elemento tiroInimigo = new Tiro();
                                tiroInimigo.setPx(inimigo.getPx());
                                tiroInimigo.setPy(inimigo.getPy() + inimigo.getAltura() / 2 - tiroInimigo.getAltura() / 2);
                                tiroInimigo.setVel(-tiroInimigo.getVel());
                                tiroInimigo.setAtivo(true);
                                tirosInimigos.add(tiroInimigo);
                                inimigo.setJaAtirou(true); // Marcar que o inimigo já atirou
                                try {
                                    File audioFile = new File("src\\Sounds\\mixkit-short-laser-gun-shot-1670.wav");
                                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                                    Clip clip = AudioSystem.getClip();
                                    clip.open(audioStream);
                                    clip.start();
                                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException o) {
                                    System.err.println("Erro ao reproduzir o som de explosão do UFO: " + o.getMessage());
                                }
                            }
                        }

                        if (Util.colide(inimigo, nave)) {
                            perdeuVida();
                            nave.atingido();
                            inimigo.setAtivo(false);
                        }

                        if (Util.colide(inimigo, tiroUFO)) {
                            tiroUFO.setAtivo(false);
                            inimigo.atingido();
                            score += ((Invader) inimigo).getPremio();
                            if (inimigo.getTipo() == Invader.Tipos.CASA || inimigo.getTipo() == Invader.Tipos.CANHAO || inimigo.getTipo() == Invader.Tipos.ARVORE) {
                                inimigoTerrestreAtivo = false;
                            }
                        }
                    }
                }

                List<Elemento> tirosInimigosTemp = new ArrayList<>(tirosInimigos);
                for (Elemento tiroInimigo : tirosInimigosTemp) {
                    if (tiroInimigo.isAtivo()) {
                        tiroInimigo.incPx(tiroInimigo.getVel());
                        if (tiroInimigo.getPx() < 0) {
                            tiroInimigo.setAtivo(false);
                        }
                        if (Util.colide(tiroInimigo, nave)) {
                            perdeuVida();
                            nave.configurarPosicaoInicial();
                            tiroInimigo.setAtivo(false); // Adicionar para desativar o tiro inimigo após colidir
                        }
                        tiroInimigo.desenha(g2d);
                    }
                }

                tirosInimigos.removeIf(tiro -> !tiro.isAtivo());
                inimigos.removeIf(inimigo -> !inimigo.isAtivo());

                for (Invader inimigo : inimigos) {
                    inimigo.desenha(g2d);
                }

                // Verificar se todos os inimigos estão fora da tela
                boolean todosInimigosForaDaTela = true;
                for (Invader inimigo : inimigos) {
                    if (inimigo.getPx() + inimigo.getLargura() > 0) {
                        todosInimigosForaDaTela = false;
                        break;
                    }
                }
                if (todosInimigosForaDaTela && !nave.isAtivo() && nave.isExplosaoFinalizada()) {
                    nave.setAtivo(true); // Reactivate the UFO
                    nave.configurarPosicaoInicial(); // Faz o UFO renascer
                }

                if (!respawning) {
                    nave.desenha(g2d);
                }

                Texto texto = new Texto();
                Font fonteDoTexto = texto.getFonte().deriveFont(40f);
                g2d.setColor(Color.GREEN);
                g2d.setFont(fonteDoTexto);
                g2d.drawString("" + score, janelaLargura / 2 - 30, 50);

                tela.repaint();
                nextUpdate = System.currentTimeMillis() + FPS;

                checarColisaoBala(tiroUFO);

                if (System.currentTimeMillis() - tempoUltimoInimigo > 50 && !Jogo.getCriacaoInimigosParada()) {
                    criarInimigo();
                    tempoUltimoInimigo = System.currentTimeMillis();
                }
            }
        }

        if (nave.getVidas() <= 0) {
            gameOver = true;
            if (!playerAddedToRanking) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, janelaLargura, janelaAltura);
                tela.repaint();

                String playerName = JOptionPane.showInputDialog("GAMER OVER!! Digite seu nome:");
                addPlayer(playerName, score);
                if (ranking.size() > 10) {
                    ranking.removePlayer(10);
                }
                playerAddedToRanking = true;
            }
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, janelaLargura, janelaAltura);
            tela.repaint();
        }
    }
    public void addPlayer(String playerName, int score) {
        Player player = new Player(playerName, score);
        ranking.addPlayer(player);
        ranking.setJogadorAtual(playerName);
    }


    public static void main(String[] args) {
        Jogo jogo = new Jogo();
        jogo.loadGame();
        jogo.iniciarJogo();
    }
}

