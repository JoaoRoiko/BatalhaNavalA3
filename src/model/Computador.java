package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Participante controlado pelo computador.
 */
public class Computador extends Jogador {
    public static final int FACIL = 1;
    public static final int MEDIO = 2;
    public static final int DIFICIL = 3;

    private final Random aleatorio;
    private int nivel;
    private int[] ultimoAcerto;

    public Computador() {
        super("Computador", "C");
        aleatorio = new Random();
        nivel = MEDIO;
        ultimoAcerto = null;
    }

    public void configurarDificuldade(int nivel) {
        if (nivel < FACIL || nivel > DIFICIL) {
            throw new IllegalArgumentException("Nível de dificuldade inválido.");
        }
        this.nivel = nivel;
        this.ultimoAcerto = null;
    }

    public int getNivel() {
        return nivel;
    }

    /**
     * Guarda o último ponto certeiro para que o computador possa mirar em volta dele.
     */
    public void registrarResultado(Tabuleiro.ResultadoTiro resultado, int linha, int coluna) {
        if (resultado == Tabuleiro.ResultadoTiro.AFUNDADO) {
            ultimoAcerto = null;
        } else if (resultado == Tabuleiro.ResultadoTiro.ACERTO) {
            ultimoAcerto = new int[] { linha, coluna };
        }
    }

    public void posicionarNaviosAutomaticamente() {
        List<int[]> posicionamentos = new ArrayList<int[]>();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                posicionamentos.add(new int[] { linha, coluna, 1 });
                posicionamentos.add(new int[] { linha, coluna, 0 });
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
        if (deveMirarNoEntorno()) {
            int[] vizinho = escolherVizinhoDoAcerto(alvo);
            if (vizinho != null) {
                return vizinho;
            }
        }
        return escolherTiroAleatorio(alvo);
    }

    private boolean deveMirarNoEntorno() {
        if (ultimoAcerto == null) {
            return false;
        }
        // No difícil sempre insiste em volta do acerto; no médio insiste na metade das vezes.
        return nivel == DIFICIL || (nivel == MEDIO && aleatorio.nextBoolean());
    }

    private int[] escolherVizinhoDoAcerto(Tabuleiro alvo) {
        int linha = ultimoAcerto[0];
        int coluna = ultimoAcerto[1];
        int[][] vizinhos = {
                { linha - 1, coluna },
                { linha + 1, coluna },
                { linha, coluna - 1 },
                { linha, coluna + 1 }
        };
        List<int[]> disponiveis = new ArrayList<int[]>();
        for (int[] vizinho : vizinhos) {
            if (alvo.tiroDisponivel(vizinho[0], vizinho[1])) {
                disponiveis.add(vizinho);
            }
        }
        if (disponiveis.isEmpty()) {
            ultimoAcerto = null;
            return null;
        }
        return disponiveis.get(aleatorio.nextInt(disponiveis.size()));
    }

    private int[] escolherTiroAleatorio(Tabuleiro alvo) {
        List<int[]> disponiveis = new ArrayList<int[]>();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                if (alvo.tiroDisponivel(linha, coluna)) {
                    disponiveis.add(new int[] { linha, coluna });
                }
            }
        }
        if (disponiveis.isEmpty()) {
            throw new IllegalStateException("Não há posições disponíveis para disparo.");
        }
        return disponiveis.get(aleatorio.nextInt(disponiveis.size()));
    }
}
