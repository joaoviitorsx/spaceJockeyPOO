package br.com.mvbos.lgj;

import br.com.mvbos.lgj.base.Player;
import br.com.mvbos.lgj.base.Texto;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Ranking extends Texto {
    private static final String RANKING_FILE = "src/ranking.txt";
    private List<Player> players = new ArrayList<>();
    private boolean rankingExibido = false;
    private int contador = 0;
    private String jogadorAtual;

    public void setJogadorAtual(String jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }

    public String getJogadorAtual() {
        return jogadorAtual;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public void addPlayer(Player player) {
        System.out.println("Adicionando jogador: " + player.getName() + ", Score: " + player.getScore());

        boolean playerExists = false;
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                playerExists = true;
                if (player.getScore() > p.getScore()) {
                    p.setScore(player.getScore());
                }
                break;
            }
        }

        if (!playerExists) {
            if (players.size() < 10 || player.getScore() > players.get(players.size() - 1).getScore()) {
                players.add(player);
            }
        }

        sort();
        ensureTopTen();
        salvarRanking();
    }

    private void ensureTopTen() {
        if (players.size() > 10) {
            players = new ArrayList<>(players.subList(0, 10)); // Manter apenas os top 10 jogadores
        }
    }

    public int size() {
        return players.size();
    }

    public void removePlayer(int index) {
        if (index >= 0 && index < players.size()) {
            players.remove(index);
        }
    }

    public void sort() {
        Collections.sort(players, Comparator.comparing(Player::getScore).reversed());
    }

    public void exibirJogador(Graphics2D g2d, JPanel tela, int index) {
        if (index < players.size()) {
            Player player = players.get(index);
            System.out.println("Jogador " + index + ": " + player.getName() + ", Score: " + player.getScore());

            // Verificar se o jogador é o jogador atual
            if (player.getName().equals(jogadorAtual)) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }

            g2d.setFont(getFonte());
            String playerInfo = String.format("%d. %s - %d", (index + 1), player.getName(), player.getScore());
            g2d.drawString(playerInfo, Jogo.janelaLargura / 2 - 115, 100 + index * 50);
        }
    }

    public void exibirRanking(Graphics2D g2d, JPanel tela) {
        if (rankingExibido) {
            return;
        }

        System.out.println("Exibindo ranking...");
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Jogo.janelaLargura, Jogo.janelaAltura);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFonte()); // Usar a fonte definida na classe Texto
        FontMetrics fm = g2d.getFontMetrics();
        String title = "Ranking";
        int titleWidth = fm.stringWidth(title);
        int titlePositionX = (Jogo.janelaLargura - titleWidth) / 2;
        g2d.drawString(title, titlePositionX, 40);

        for (int i = 0; i <= contador; i++) {
            exibirJogador(g2d, tela, i);
        }

        if (contador < players.size()) {
            contador++;
        } else {
            rankingExibido = true;
        }

        tela.repaint();
    }

    public void carregarRanking() {
        File file = new File(RANKING_FILE);
        if (!file.exists()) {
            return;
        }
        players.clear();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                players.add(new Player(name, score));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sort();
        ensureTopTen();
    }

    private void salvarRanking() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RANKING_FILE))) {
            for (Player player : players) {
                writer.println(player.getName() + "," + player.getScore());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

