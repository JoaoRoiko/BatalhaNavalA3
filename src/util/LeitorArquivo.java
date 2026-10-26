package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Computador;
import model.Jogador;
import model.Navio;
import model.Tabuleiro;
import model.Unidade;

/**
 * Lê e interpreta um save, restaurando os seis navios e os dois tabuleiros.
 */
public class LeitorArquivo {
    private static final String CABECALHO = "BATALHA_NAVAL_V1";

    /**
     * Carrega o arquivo nos objetos já criados e devolve o participante da vez.
     */
    public String carregar(Path caminho, Jogador jogador, Computador computador) throws IOException {
        List<String> linhas = new ArrayList<String>();
        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    linhas.add(linha);
                }
            }
        }
        if (linhas.isEmpty() || !CABECALHO.equals(linhas.get(0))) {
            throw new IOException("Formato de save inválido ou incompatível.");
        }

        String vez = null;
        char[][] gradeJogador = null;
        char[][] gradeComputador = null;
        Map<String, Unidade> unidadesPorId = new HashMap<String, Unidade>();
        Map<String, Unidade> unidadesEsperadas = criarMapaUnidades(jogador, computador);

        for (int indice = 1; indice < linhas.size(); indice++) {
            String[] campos = linhas.get(indice).split("\\|", -1);
            if (campos.length == 2 && "TURNO".equals(campos[0])) {
                if (vez != null || (!"J".equals(campos[1]) && !"C".equals(campos[1]))) {
                    throw new IOException("Campo TURNO inválido ou repetido.");
                }
                vez = campos[1];
            } else if (campos.length == 12 && "GRADE".equals(campos[0])) {
                if ("J".equals(campos[1]) && gradeJogador == null) {
                    gradeJogador = parsearGrade(campos);
                } else if ("C".equals(campos[1]) && gradeComputador == null) {
                    gradeComputador = parsearGrade(campos);
                } else {
                    throw new IOException("Identificador ou duplicidade de GRADE inválido.");
                }
            } else if (campos.length == 8 && "NAVIO".equals(campos[0])) {
                String id = campos[1];
                Unidade unidade = unidadesEsperadas.get(id);
                if (unidade == null || unidadesPorId.containsKey(id)) {
                    throw new IOException("Navio desconhecido ou repetido no save: " + id);
                }
                int tamanho = parsearInteiro(campos[2], "tamanho");
                int[] posicoes = parsearPosicoes(campos[3]);
                int partes = parsearInteiro(campos[4], "partes atingidas");
                boolean afundado = parsearBooleano(campos[5]);
                int[] acertos = parsearPosicoes(campos[6]);
                if (!"FIM".equals(campos[7])) {
                    throw new IOException("Registro de navio incompleto: " + id);
                }
                try {
                    unidade.restaurarEstado(tamanho, posicoes, acertos, partes, afundado);
                } catch (IllegalArgumentException excecao) {
                    throw new IOException("Estado inválido para o navio " + id + ".", excecao);
                }
                unidadesPorId.put(id, unidade);
            } else {
                throw new IOException("Linha inválida no arquivo de save: " + linhas.get(indice));
            }
        }

        if (vez == null || gradeJogador == null || gradeComputador == null
                || unidadesPorId.size() != Jogador.QUANTIDADE_NAVIOS * 2) {
            throw new IOException("O save não contém todos os dados da partida.");
        }
        validarConsistenciaGrade(gradeJogador, jogador.getNavios());
        validarConsistenciaGrade(gradeComputador, computador.getNavios());
        jogador.getTabuleiro().restaurarUnidades(asLista(jogador.getNavios()));
        computador.getTabuleiro().restaurarUnidades(asLista(computador.getNavios()));
        jogador.getTabuleiro().restaurarGrade(gradeJogador);
        computador.getTabuleiro().restaurarGrade(gradeComputador);
        return vez;
    }

    private Map<String, Unidade> criarMapaUnidades(Jogador jogador, Computador computador) {
        Map<String, Unidade> mapa = new HashMap<String, Unidade>();
        for (Navio navio : jogador.getNavios()) {
            mapa.put(navio.getId(), navio);
        }
        for (Navio navio : computador.getNavios()) {
            mapa.put(navio.getId(), navio);
        }
        return mapa;
    }

    private char[][] parsearGrade(String[] campos) throws IOException {
        char[][] grade = new char[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            String valores = campos[linha + 2];
            if (valores.length() != Tabuleiro.COLUNAS) {
                throw new IOException("Cada linha da grade deve conter exatamente 10 caracteres.");
            }
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                grade[linha][coluna] = valores.charAt(coluna);
            }
        }
        try {
            new Tabuleiro().restaurarGrade(grade);
        } catch (IllegalArgumentException excecao) {
            throw new IOException("A grade salva contém dados inválidos.", excecao);
        }
        return grade;
    }

    private int[] parsearPosicoes(String valor) throws IOException {
        if (valor.isEmpty()) {
            return new int[0];
        }
        String[] itens = valor.split(",", -1);
        int[] posicoes = new int[itens.length];
        for (int i = 0; i < itens.length; i++) {
            posicoes[i] = parsearInteiro(itens[i], "posição");
        }
        return posicoes;
    }

    private int parsearInteiro(String valor, String campo) throws IOException {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException excecao) {
            throw new IOException("Valor inválido para " + campo + ".", excecao);
        }
    }

    private boolean parsearBooleano(String valor) throws IOException {
        if ("true".equals(valor)) {
            return true;
        }
        if ("false".equals(valor)) {
            return false;
        }
        throw new IOException("Valor inválido para afundado.");
    }

    private void validarConsistenciaGrade(char[][] grade, Navio[] navios) throws IOException {
        boolean[] posicoesDeNavios = new boolean[Tabuleiro.LINHAS * Tabuleiro.COLUNAS];
        for (Navio navio : navios) {
            int[] posicoes = navio.getPosicoes();
            for (int posicao : posicoes) {
                if (posicoesDeNavios[posicao]) {
                    throw new IOException("Dois navios ocupam a mesma posição no save.");
                }
                posicoesDeNavios[posicao] = true;
                char celula = grade[posicao / Tabuleiro.COLUNAS][posicao % Tabuleiro.COLUNAS];
                boolean atingida = contem(navio.getPosicoesAtingidas(), posicao);
                if ((atingida && celula != Tabuleiro.ACERTO)
                        || (!atingida && celula != Tabuleiro.NAVIO)) {
                    throw new IOException("A grade e os dados do navio " + navio.getId() + " divergem.");
                }
            }
        }
        for (int posicao = 0; posicao < posicoesDeNavios.length; posicao++) {
            char celula = grade[posicao / Tabuleiro.COLUNAS][posicao % Tabuleiro.COLUNAS];
            if ((celula == Tabuleiro.NAVIO || celula == Tabuleiro.ACERTO) && !posicoesDeNavios[posicao]) {
                throw new IOException("A grade contém uma parte de navio sem unidade correspondente.");
            }
        }
    }

    private boolean contem(int[] valores, int procurado) {
        for (int valor : valores) {
            if (valor == procurado) {
                return true;
            }
        }
        return false;
    }

    private List<Unidade> asLista(Navio[] navios) {
        List<Unidade> lista = new ArrayList<Unidade>();
        for (Navio navio : navios) {
            lista.add(navio);
        }
        return lista;
    }
}
