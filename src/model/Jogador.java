package model;

/**
 * Participante humano e seu tabuleiro com três navios.
 */
public class Jogador {
    public static final int QUANTIDADE_NAVIOS = 3;

    private final String nome;
    private final Tabuleiro tabuleiro;
    private final Navio[] navios;

    public Jogador(String nome, String prefixoIds) {
        this.nome = nome;
        this.tabuleiro = new Tabuleiro();
        this.navios = new Navio[QUANTIDADE_NAVIOS];
        for (int i = 0; i < navios.length; i++) {
            navios[i] = new Navio(prefixoIds + (i + 1));
        }
    }

    public String getNome() {
        return nome;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Navio[] getNavios() {
        return navios.clone();
    }

    public int quantidadeNaviosAfundados() {
        return tabuleiro.quantidadeNaviosAfundados();
    }

    public boolean perdeu() {
        return quantidadeNaviosAfundados() == QUANTIDADE_NAVIOS;
    }
}
