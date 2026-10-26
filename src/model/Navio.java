package model;

/**
 * Navio padrão do jogo, com três partes.
 */
public class Navio extends Unidade {
    public static final int TAMANHO_PADRAO = 3;

    public Navio(String id) {
        super(id, TAMANHO_PADRAO);
    }

    @Override
    public boolean posicionar(Tabuleiro tabuleiro, int linha, int coluna, boolean horizontal) {
        return tabuleiro.posicionarUnidade(this, linha, coluna, horizontal);
    }

    @Override
    public boolean atingir(int posicao) {
        if (!contemPosicao(posicao) || jaFoiAtingida(posicao)) {
            return false;
        }
        registrarAcerto(posicao);
        return true;
    }
}
