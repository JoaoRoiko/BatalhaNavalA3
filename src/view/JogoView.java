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

    public void exibirPlacar(int vitorias, int derrotas) {
        System.out.println();
        System.out.println(DOURADO + "🏆 vitórias: " + vitorias + RESET_COR
                + "   " + VERMELHO + "💀 derrotas: " + derrotas + RESET_COR);
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

    public int exibirMenuDificuldade() {
        System.out.println();
        System.out.println(NEGRITO + "⚙️ DIFICULDADE DO COMPUTADOR" + RESET_COR);
        System.out.println("   1 - 🐣 Fácil (tiros aleatórios)");
        System.out.println("   2 - 🎯 Médio (às vezes mira perto do acerto)");
        System.out.println("   3 - 😈 Difícil (sempre mira perto do acerto)");
        return lerInteiro("👉 Escolha: ", 1, 3);
    }

    public static String nomeDificuldade(int nivel) {
        switch (nivel) {
            case 1:
                return "🐣 Fácil";
            case 3:
                return "😈 Difícil";
            default:
                return "🎯 Médio";
        }
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public int[] solicitarTiro(Tabuleiro tabuleiroAlvo) {
        System.out.println("   💡 Dica: linhas e colunas vão de 1 a 10.");
        while (true) {
            int linha = lerInteiro("   🎯 Linha do disparo (1-10): ", 1, 10) - 1;
            int coluna = lerInteiro("   🎯 Coluna do disparo (1-10): ", 1, 10) - 1;
            if (tabuleiroAlvo.tiroDisponivel(linha, coluna)) {
                return new int[] { linha, coluna };
            }
            exibirMensagem("   🚫 Essa posição já foi atingida. Escolha outra.");
        }
    }

    public void exibirTabuleiros(Tabuleiro tabuleiroJogador, Tabuleiro tabuleiroComputador) {
        System.out.println();
        System.out.println(CIANO + NEGRITO + "🚢 SEU TABULEIRO" + RESET_COR + "   "
                + VERMELHO + "navios afundados: " + tabuleiroJogador.quantidadeNaviosAfundados()
                + "/3" + RESET_COR);
        exibirGrade(tabuleiroJogador.getGradeVisivel(true));
        System.out.println();
        System.out.println(AZUL_CLARO + NEGRITO + "🎯 TABULEIRO DO COMPUTADOR" + RESET_COR + "   "
                + VERDE + "navios afundados: " + tabuleiroComputador.quantidadeNaviosAfundados()
                + "/3" + RESET_COR);
        exibirGrade(tabuleiroComputador.getGradeVisivel(false));
    }

    public void exibirTabuleiroPosicionamento(Tabuleiro tabuleiro) {
        System.out.println();
        System.out.println(CIANO + NEGRITO + "🚢 SEUS NAVIOS ATÉ AGORA" + RESET_COR);
        exibirGrade(tabuleiro.getGradeVisivel(true));
    }

    public void exibirResultadoTiro(String atirador, Tabuleiro.ResultadoTiro resultado) {
        switch (resultado) {
            case AGUA:
                exibirMensagem("   " + AZUL_CLARO + "🌊 " + atirador + ": água! O tiro caiu no mar." + RESET_COR);
                break;
            case ACERTO:
                exibirMensagem("   " + VERMELHO + NEGRITO + "💥 " + atirador + ": ACERTOU UM NAVIO!" + RESET_COR);
                break;
            case AFUNDADO:
                exibirMensagem("   " + VERMELHO + NEGRITO + "💥 " + atirador
                        + ": BOOOOM! NAVIO AFUNDADO! 🔥" + RESET_COR);
                exibirExplosao();
                break;
            default:
                exibirMensagem("   ❌ Disparo inválido.");
        }
    }

    private void exibirExplosao() {
        System.out.println(VERMELHO + "           \\    💥    /" + RESET_COR);
        System.out.println(VERMELHO + "            💥  🚢  💥" + RESET_COR);
        System.out.println(VERMELHO + "           /    💥    \\" + RESET_COR);
    }

    public void exibirResultadoFinal(boolean vitoria) {
        System.out.println();
        if (vitoria) {
            System.out.println(DOURADO + NEGRITO + "🏆 PARABÉNS! VOCÊ VENCEU A BATALHA NAVAL! 🏆" + RESET_COR);
        } else {
            System.out.println(VERMELHO + NEGRITO + "💀 Todos os seus navios foram afundados. O computador venceu." + RESET_COR);
        }
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
            System.out.print(DOURADO + NEGRITO + coluna + RESET_COR + " ");
        }
        System.out.println();
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            System.out.print((linha + 1 < 10 ? " " : "")
                    + DOURADO + NEGRITO + (linha + 1) + RESET_COR + "  ");
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                char celula = grade[linha][coluna];
                String cor = celula == Tabuleiro.ACERTO ? VERMELHO + NEGRITO
                        : celula == Tabuleiro.ERRO ? AZUL_ESCURO
                                : celula == Tabuleiro.AGUA ? AZUL_CLARO : VERDE;
                System.out.print(" " + cor + celula + RESET_COR + " ");
            }
            System.out.println();
        }
        exibirLegenda();
    }

    public void exibirLegenda() {
        System.out.println("     " + AZUL_CLARO + "~" + RESET_COR + " água   "
                + VERDE + "N" + RESET_COR + " seu navio   "
                + VERMELHO + NEGRITO + "X" + RESET_COR + " acerto   "
                + AZUL_ESCURO + "o" + RESET_COR + " tiro na água");
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
