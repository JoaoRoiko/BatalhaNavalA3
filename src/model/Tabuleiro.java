package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Matriz e regras de ocupação e disparo do tabuleiro.
 */
public class Tabuleiro {
    public static final int LINHAS = 10;
    public static final int COLUNAS = 10;
    public static final char AGUA = '~';
    public static final char NAVIO = 'N';
    public static final char ACERTO = 'X';
    public static final char ERRO = 'o';

    public enum ResultadoTiro {
        INVALIDO, AGUA, ACERTO, AFUNDADO
    }

    private char[][] grade;
    private final List<Unidade> unidades;

    public Tabuleiro() {
        grade = criarGradeVazia();
        unidades = new ArrayList<Unidade>();
    }

    public boolean posicionarUnidade(Unidade unidade, int linha, int coluna, boolean horizontal) {
        if (unidade == null || linha < 0 || linha >= LINHAS || coluna < 0 || coluna >= COLUNAS) {
            return false;
        }
        if (unidade.getPosicoes().length != 0 || unidade.getTamanho() <= 0) {
            return false;
        }

        int deslocamentoLinha = horizontal ? 0 : 1;
        int deslocamentoColuna = horizontal ? 1 : 0;
        int linhaFinal = linha + deslocamentoLinha * (unidade.getTamanho() - 1);
        int colunaFinal = coluna + deslocamentoColuna * (unidade.getTamanho() - 1);
        if (linhaFinal >= LINHAS || colunaFinal >= COLUNAS) {
            return false;
        }

        int[] posicoes = new int[unidade.getTamanho()];
        for (int i = 0; i < unidade.getTamanho(); i++) {
            int linhaAtual = linha + deslocamentoLinha * i;
            int colunaAtual = coluna + deslocamentoColuna * i;
            if (grade[linhaAtual][colunaAtual] != AGUA) {
                return false;
            }
            posicoes[i] = paraIndice(linhaAtual, colunaAtual);
        }

        unidade.definirPosicoes(posicoes);
        unidades.add(unidade);
        for (int posicao : posicoes) {
            grade[posicao / COLUNAS][posicao % COLUNAS] = NAVIO;
        }
        return true;
    }

    public ResultadoTiro atirar(int linha, int coluna) {
        if (linha < 0 || linha >= LINHAS || coluna < 0 || coluna >= COLUNAS) {
            return ResultadoTiro.INVALIDO;
        }
        char celula = grade[linha][coluna];
        if (celula == ACERTO || celula == ERRO) {
            return ResultadoTiro.INVALIDO;
        }
        if (celula == AGUA) {
            grade[linha][coluna] = ERRO;
            return ResultadoTiro.AGUA;
        }

        int posicao = paraIndice(linha, coluna);
        for (Unidade unidade : unidades) {
            if (unidade.atingir(posicao)) {
                grade[linha][coluna] = ACERTO;
                return unidade.isAfundado() ? ResultadoTiro.AFUNDADO : ResultadoTiro.ACERTO;
            }
        }
        throw new IllegalStateException("A matriz contém um navio sem unidade correspondente.");
    }

    public boolean tiroDisponivel(int linha, int coluna) {
        return linha >= 0 && linha < LINHAS && coluna >= 0 && coluna < COLUNAS
                && grade[linha][coluna] != ACERTO && grade[linha][coluna] != ERRO;
    }

    public char[][] getGradeVisivel(boolean revelarNavios) {
        char[][] copia = new char[LINHAS][COLUNAS];
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                char valor = grade[linha][coluna];
                copia[linha][coluna] = valor == NAVIO && !revelarNavios ? AGUA : valor;
            }
        }
        return copia;
    }

    public List<Unidade> getUnidades() {
        return Collections.unmodifiableList(unidades);
    }

    public boolean todosNaviosPosicionados(int quantidadeEsperada) {
        return unidades.size() == quantidadeEsperada;
    }

    public int quantidadeNaviosAfundados() {
        int afundados = 0;
        for (Unidade unidade : unidades) {
            if (unidade.isAfundado()) {
                afundados++;
            }
        }
        return afundados;
    }

    public char[][] copiarGradeCompleta() {
        char[][] copia = new char[LINHAS][COLUNAS];
        for (int linha = 0; linha < LINHAS; linha++) {
            copia[linha] = grade[linha].clone();
        }
        return copia;
    }

    /**
     * Substitui a matriz durante o carregamento de uma partida validada.
     */
    public void restaurarGrade(char[][] gradeSalva) {
        if (gradeSalva == null || gradeSalva.length != LINHAS) {
            throw new IllegalArgumentException("A matriz salva deve ter 10 linhas.");
        }
        char[][] copia = new char[LINHAS][COLUNAS];
        for (int linha = 0; linha < LINHAS; linha++) {
            if (gradeSalva[linha] == null || gradeSalva[linha].length != COLUNAS) {
                throw new IllegalArgumentException("A matriz salva deve ter 10 colunas por linha.");
            }
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                char valor = gradeSalva[linha][coluna];
                if (valor != AGUA && valor != NAVIO && valor != ACERTO && valor != ERRO) {
                    throw new IllegalArgumentException("A matriz salva contém um símbolo inválido.");
                }
                copia[linha][coluna] = valor;
            }
        }
        grade = copia;
    }

    public void restaurarUnidades(List<? extends Unidade> unidadesSalvas) {
        if (unidadesSalvas == null) {
            throw new IllegalArgumentException("A lista de unidades não pode ser nula.");
        }
        unidades.clear();
        for (Unidade unidade : unidadesSalvas) {
            if (unidade == null || unidade.getPosicoes().length != unidade.getTamanho()) {
                throw new IllegalArgumentException("O save contém uma unidade não posicionada.");
            }
            unidades.add(unidade);
        }
    }

    private char[][] criarGradeVazia() {
        char[][] novaGrade = new char[LINHAS][COLUNAS];
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                novaGrade[linha][coluna] = AGUA;
            }
        }
        return novaGrade;
    }

    private int paraIndice(int linha, int coluna) {
        return linha * COLUNAS + coluna;
    }
}
