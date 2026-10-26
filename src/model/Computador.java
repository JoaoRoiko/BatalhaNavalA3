package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Participante controlado pelo computador.
 */
public class Computador extends Jogador {
    private final Random aleatorio;

    public Computador() {
        super("Computador", "C");
        aleatorio = new Random();
    }

    public void posicionarNaviosAutomaticamente() {
        List<int[]> posicionamentos = new ArrayList<int[]>();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                posicionamentos.add(new int[]{linha, coluna, 1});
                posicionamentos.add(new int[]{linha, coluna, 0});
            }
        }

        for (Navio navio : getNavios()) {
            List<int[]> embaralhados = new ArrayList<int[]>(posicionamentos);
            Collections.shuffle(embaralhados, aleatorio);
            boolean posicionado = false;
            for (int[] posicao : embaralhados) {
                if (navio.posicionar(getTabuleiro(), posicao[0], posicao[1], posicao[2] == 1)) {
                    posicionado = true;
                    break;
                }
            }
            if (!posicionado) {
                throw new IllegalStateException("Não foi possível posicionar os navios do computador.");
            }
        }
    }

    public int[] escolherTiro(Tabuleiro alvo) {
        List<int[]> disponiveis = new ArrayList<int[]>();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                if (alvo.tiroDisponivel(linha, coluna)) {
                    disponiveis.add(new int[]{linha, coluna});
                }
            }
        }
        if (disponiveis.isEmpty()) {
            throw new IllegalStateException("Não há posições disponíveis para disparo.");
        }
        return disponiveis.get(aleatorio.nextInt(disponiveis.size()));
    }
}
