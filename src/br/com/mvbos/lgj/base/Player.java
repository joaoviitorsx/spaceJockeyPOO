package br.com.mvbos.lgj.base;

public class Player implements Comparable<Player> {
    private String name;
    private int score;
    private int arrivalOrder;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        this.arrivalOrder = arrivalOrder;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    // Adicione este método
    public void setScore(int score) {
        this.score = score;
    }

    public int getArrivalOrder() {
        return arrivalOrder;
    }

    @Override
    public int compareTo(Player other) {
        int nameComparison = this.name.compareTo(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        } else {
            int scoreComparison = Integer.compare(other.score, this.score);
            if (scoreComparison != 0) {
                return scoreComparison;
            } else {
                return Integer.compare(this.arrivalOrder, other.arrivalOrder);
            }
        }
    }
}

