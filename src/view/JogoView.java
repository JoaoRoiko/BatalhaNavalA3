package view;

import java.util.Scanner;

import model.Tabuleiro;

/**
 * Entrada e saída do jogo no console.
 */
public class JogoView {
    private final Scanner scanner;

    public JogoView() {
        scanner = new Scanner(System.in);
    }

    public int exibirMenuInicial() {
        System.out.println("\n=== BATALHA NAVAL ===");
        System.out.println("1 - Nova partida");
        System.out.println("2 - Carregar partida salva");
        System.out.println("0 - Sair");
        return lerInteiro("Escolha: ", 0, 2);
    }

    public int exibirMenuTurno() {
        System.out.println("\n1 - Fazer disparo");
        System.out.println("2 - Salvar e sair");
        System.out.println("3 - Carregar partida salva");
        return lerInteiro("Escolha: ", 1, 3);
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public int[] solicitarTiro(Tabuleiro tabuleiroAlvo) {
        while (true) {
            int linha = lerInteiro("Linha do disparo (1-10): ", 1, 10) - 1;
            int coluna = lerInteiro("Coluna do disparo (1-10): ", 1, 10) - 1;
            if (tabuleiroAlvo.tiroDisponivel(linha, coluna)) {
                return new int[]{linha, coluna};
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
            System.out.printf("%2d ", coluna);
        }
        System.out.println();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            System.out.printf("%2d  ", linha + 1);
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                System.out.print(" " + grade[linha][coluna] + " ");
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
