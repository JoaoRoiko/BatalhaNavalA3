package model;

import java.util.Arrays;

/**
 * Representa uma unidade posicionável no tabuleiro.
 */
public abstract class Unidade {
    private final String id;
    private final int tamanho;
    private int[] posicoes;
    private int[] posicoesAtingidas;
    private int partesAtingidas;
    private boolean afundado;

    protected Unidade(String id, int tamanho) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O identificador da unidade não pode ser vazio.");
        }
        if (tamanho <= 0) {
            throw new IllegalArgumentException("O tamanho da unidade deve ser positivo.");
        }
        this.id = id;
        this.tamanho = tamanho;
        this.posicoes = new int[0];
        this.posicoesAtingidas = new int[0];
    }

    public abstract boolean posicionar(Tabuleiro tabuleiro, int linha, int coluna, boolean horizontal);

    public abstract boolean atingir(int posicao);

    public String getId() {
        return id;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int[] getPosicoes() {
        return posicoes.clone();
    }

    public int[] getPosicoesAtingidas() {
        return posicoesAtingidas.clone();
    }

    public int getPartesAtingidas() {
        return partesAtingidas;
    }

    public boolean isAfundado() {
        return afundado;
    }

    void definirPosicoes(int[] novasPosicoes) {
        if (novasPosicoes == null || novasPosicoes.length != tamanho || posicoes.length != 0) {
            throw new IllegalArgumentException("Posições inválidas para a unidade " + id + ".");
        }
        posicoes = novasPosicoes.clone();
    }

    protected boolean contemPosicao(int posicao) {
        for (int ocupada : posicoes) {
            if (ocupada == posicao) {
                return true;
            }
        }
        return false;
    }

    protected boolean jaFoiAtingida(int posicao) {
        for (int atingida : posicoesAtingidas) {
            if (atingida == posicao) {
                return true;
            }
        }
        return false;
    }

    protected void registrarAcerto(int posicao) {
        int[] novasPosicoesAtingidas = Arrays.copyOf(posicoesAtingidas, posicoesAtingidas.length + 1);
        novasPosicoesAtingidas[novasPosicoesAtingidas.length - 1] = posicao;
        posicoesAtingidas = novasPosicoesAtingidas;
        partesAtingidas++;
        afundado = partesAtingidas == tamanho;
    }

    /**
     * Restaura os dados lidos do arquivo de save depois de validar sua consistência.
     */
    public void restaurarEstado(int tamanhoSalvo, int[] posicoesSalvas,
                                int[] acertosSalvos, int partesSalvas, boolean afundadoSalvo) {
        if (tamanhoSalvo != tamanho || posicoesSalvas == null || posicoesSalvas.length != tamanho
                || acertosSalvos == null || partesSalvas != acertosSalvos.length
                || partesSalvas < 0 || partesSalvas > tamanho
                || afundadoSalvo != (partesSalvas == tamanho)) {
            throw new IllegalArgumentException("Estado salvo inválido para a unidade " + id + ".");
        }
        for (int posicao : posicoesSalvas) {
            if (posicao < 0 || posicao >= Tabuleiro.LINHAS * Tabuleiro.COLUNAS) {
                throw new IllegalArgumentException("Posição inválida para a unidade " + id + ".");
            }
        }
        for (int acerto : acertosSalvos) {
            if (!contem(posicoesSalvas, acerto)) {
                throw new IllegalArgumentException("Acerto não pertence à unidade " + id + ".");
            }
        }
        if (contemDuplicados(posicoesSalvas) || contemDuplicados(acertosSalvos)) {
            throw new IllegalArgumentException("O save contém posições repetidas na unidade " + id + ".");
        }
        if (!saoPosicoesLineares(posicoesSalvas)) {
            throw new IllegalArgumentException("As posições salvas não formam uma linha para a unidade " + id + ".");
        }

        posicoes = posicoesSalvas.clone();
        posicoesAtingidas = acertosSalvos.clone();
        partesAtingidas = partesSalvas;
        afundado = afundadoSalvo;
    }

    private boolean contem(int[] valores, int procurado) {
        for (int valor : valores) {
            if (valor == procurado) {
                return true;
            }
        }
        return false;
    }

    private boolean contemDuplicados(int[] valores) {
        for (int i = 0; i < valores.length; i++) {
            for (int j = i + 1; j < valores.length; j++) {
                if (valores[i] == valores[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean saoPosicoesLineares(int[] valores) {
        if (valores.length < 2) {
            return true;
        }
        int passo = valores[1] - valores[0];
        if (passo != 1 && passo != Tabuleiro.COLUNAS) {
            return false;
        }
        if (passo == 1 && valores[0] / Tabuleiro.COLUNAS != valores[valores.length - 1] / Tabuleiro.COLUNAS) {
            return false;
        }
        for (int i = 1; i < valores.length; i++) {
            if (valores[i] != valores[0] + passo * i) {
                return false;
            }
        }
        return true;
    }
}
