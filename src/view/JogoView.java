package view;

import java.util.Scanner;

import model.Tabuleiro;

/**
 * Entrada e saída do jogo no console.
 */
public class JogoView {
    private static final String VERMELHO = "\u001B[31m";
    private static final String VERDE = "\u001B[32m";
    private static final String DOURADO = "\u001B[33m";
    private static final String AZUL_ESCURO = "\u001B[34m";
    private static final String CIANO = "\u001B[36m";
    private static final String AZUL_CLARO = "\u001B[94m";
    private static final String NEGRITO = "\u001B[1m";
    private static final String RESET_COR = "\u001B[0m";

    private final Scanner scanner;

    public JogoView() {
        scanner = new Scanner(System.in);
    }

    public void exibirBoasVindas() {
        System.out.println();
        System.out.println(DOURADO + "==============================================" + RESET_COR);
        System.out.println(DOURADO + "   ⚓   B A T A L H A   N A V A L   ⚓" + RESET_COR);
        System.out.println(DOURADO + "        🧑 você  vs  computador 🤖" + RESET_COR);
        System.out.println(DOURADO + "==============================================" + RESET_COR);
    }

    public int exibirMenuInicial() {
        System.out.println();
        System.out.println(NEGRITO + "🏠 MENU PRINCIPAL" + RESET_COR);
        System.out.println("   1 - 🎮 Nova partida");
        System.out.println("   2 - 💾 Carregar partida salva");
        System.out.println("   0 - 🚪 Sair");
        return lerInteiro("👉 Escolha: ", 0, 2);
    }

    public int exibirMenuTurno() {
        System.out.println();
        System.out.println(NEGRITO + "🎯 O QUE VOCÊ QUER FAZER?" + RESET_COR);
        System.out.println("   1 - 💥 Atirar");
        System.out.println("   2 - 💾 Salvar e sair");
        System.out.println("   3 - 📂 Carregar partida salva");
        return lerInteiro("👉 Escolha: ", 1, 3);
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public int[] solicitarTiro(Tabuleiro tabuleiroAlvo) {
        while (true) {
            int linha = lerInteiro("Linha do disparo (1-10): ", 1, 10) - 1;
            int coluna = lerInteiro("Coluna do disparo (1-10): ", 1, 10) - 1;
            if (tabuleiroAlvo.tiroDisponivel(linha, coluna)) {
                return new int[] { linha, coluna };
            }
            exibirMensagem("Essa posição já foi atingida. Escolha outra.");
        }
    }

    public void exibirTabuleiros(Tabuleiro tabuleiroJogador, Tabuleiro tabuleiroComputador) {
        System.out.println("\nSeu tabuleiro:");
        exibirGrade(tabuleiroJogador.getGradeVisivel(true));
        System.out.println("\nTabuleiro do computador (seus disparos):");
        exibirGrade(tabuleiroComputador.getGradeVisivel(false));
    }

    public void exibirResultadoTiro(String atirador, Tabuleiro.ResultadoTiro resultado) {
        String mensagem;
        switch (resultado) {
            case AGUA:
                mensagem = "Água.";
                break;
            case ACERTO:
                mensagem = "Acertou um navio!";
                break;
            case AFUNDADO:
                mensagem = "Navio afundado!";
                break;
            default:
                mensagem = "Disparo inválido.";
        }
        exibirMensagem(atirador + ": " + mensagem);
    }

    public void fechar() {
        scanner.close();
    }

    public String lerEntrada(String mensagem) {
        return lerTexto(mensagem);
    }

    private void exibirGrade(char[][] grade) {
        System.out.print("    ");
        for (int coluna = 1; coluna <= Tabuleiro.COLUNAS; coluna++) {
            System.out.print(coluna < 10 ? " " : "");
            System.out.print(DOURADO + coluna + RESET_COR + " ");
        }
        System.out.println();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            System.out.print((linha + 1 < 10 ? " " : "")
                    + DOURADO + (linha + 1) + RESET_COR + "  ");
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                char celula = grade[linha][coluna];
                String cor = celula == Tabuleiro.ACERTO ? VERMELHO
                        : celula == Tabuleiro.ERRO ? AZUL_ESCURO
                                : celula == Tabuleiro.AGUA ? AZUL_CLARO : "";
                System.out.print(" " + cor + celula + (cor.isEmpty() ? "" : RESET_COR) + " ");
            }
            System.out.println();
        }
        System.out.println("Legenda: ~ água, N navio, X acerto, o tiro na água.");
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            String entrada = lerTexto(mensagem).trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException excecao) {
                // A mensagem ao usuário abaixo orienta uma nova tentativa.
            }
            exibirMensagem("Digite um número entre " + minimo + " e " + maximo + ".");
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }
}
