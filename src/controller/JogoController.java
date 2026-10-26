package controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Computador;
import model.Jogador;
import model.Navio;
import model.Tabuleiro;
import model.Unidade;
import util.LeitorArquivo;
import view.JogoView;

/**
 * Coordena a partida, os turnos e a persistência.
 */
public class JogoController {
    private static final Path ARQUIVO_SAVE = Paths.get("batalha_naval_save.txt");

    private final JogoView view;
    private Jogador jogador;
    private Computador computador;
    private String vez;

    public JogoController() {
        view = new JogoView();
    }

    public static void main(String[] args) {
        new JogoController().iniciar();
    }

    private void iniciar() {
        boolean executando = true;
        while (executando) {
            int opcao = view.exibirMenuInicial();
            if (opcao == 0) {
                executando = false;
            } else if (opcao == 1) {
                novaPartida();
                executarPartida();
            } else {
                if (carregarPartida()) {
                    executarPartida();
                }
            }
        }
        view.exibirMensagem("Até a próxima!");
        view.fechar();
    }

    private void novaPartida() {
        jogador = new Jogador("Jogador", "J");
        computador = new Computador();
        view.exibirMensagem("Posicione seus três navios. Coordenadas começam em 1.");
        for (Navio navio : jogador.getNavios()) {
            while (true) {
                view.exibirMensagem("Posicione o navio " + navio.getId() + " (tamanho 3).");
                int[] coordenadas = lerPosicaoNavio();
                if (navio.posicionar(jogador.getTabuleiro(), coordenadas[0], coordenadas[1],
                        coordenadas[2] == 1)) {
                    break;
                }
                view.exibirMensagem("Posição inválida ou ocupada. Tente novamente.");
            }
        }
        computador.posicionarNaviosAutomaticamente();
        vez = "J";
    }

    private int[] lerPosicaoNavio() {
        int linha = lerCoordenada("Linha inicial (1-10): ");
        int coluna = lerCoordenada("Coluna inicial (1-10): ");
        boolean horizontal;
        while (true) {
            view.exibirMensagem("Orientação: H para horizontal ou V para vertical.");
            String orientacao = view.lerEntrada("Orientação: ").trim();
            if ("H".equalsIgnoreCase(orientacao) || "V".equalsIgnoreCase(orientacao)) {
                horizontal = "H".equalsIgnoreCase(orientacao);
                break;
            }
        }
        return new int[]{linha, coluna, horizontal ? 1 : 0};
    }

    private void executarPartida() {
        while (!jogador.perdeu() && !computador.perdeu()) {
            view.exibirTabuleiros(jogador.getTabuleiro(), computador.getTabuleiro());
            int opcao = view.exibirMenuTurno();
            if (opcao == 2) {
                if (salvarPartida()) {
                    return;
                }
                continue;
            }
            if (opcao == 3) {
                carregarPartida();
                continue;
            }
            if ("J".equals(vez)) {
                turnoJogador();
            } else {
                turnoComputador();
            }
        }

        view.exibirTabuleiros(jogador.getTabuleiro(), computador.getTabuleiro());
        if (computador.perdeu()) {
            view.exibirMensagem("Parabéns! Você venceu a batalha naval!");
        } else {
            view.exibirMensagem("Todos os seus navios foram afundados. O computador venceu.");
        }
    }

    private void turnoJogador() {
        view.exibirMensagem("\nSua vez de atirar.");
        int[] tiro = view.solicitarTiro(computador.getTabuleiro());
        Tabuleiro.ResultadoTiro resultado = computador.getTabuleiro().atirar(tiro[0], tiro[1]);
        view.exibirResultadoTiro(jogador.getNome(), resultado);
        vez = "C";
    }

    private void turnoComputador() {
        int[] tiro = computador.escolherTiro(jogador.getTabuleiro());
        view.exibirMensagem("\nVez do computador: linha " + (tiro[0] + 1)
                + ", coluna " + (tiro[1] + 1) + ".");
        Tabuleiro.ResultadoTiro resultado = jogador.getTabuleiro().atirar(tiro[0], tiro[1]);
        view.exibirResultadoTiro(computador.getNome(), resultado);
        vez = "J";
    }

    private boolean salvarPartida() {
        try (BufferedWriter escritor = Files.newBufferedWriter(ARQUIVO_SAVE, StandardCharsets.UTF_8)) {
            escritor.write("BATALHA_NAVAL_V1");
            escritor.newLine();
            escritor.write("TURNO|" + vez);
            escritor.newLine();
            escreverGrade(escritor, "J", jogador.getTabuleiro().copiarGradeCompleta());
            escreverGrade(escritor, "C", computador.getTabuleiro().copiarGradeCompleta());
            for (Navio navio : jogador.getNavios()) {
                escreverNavio(escritor, navio);
            }
            for (Navio navio : computador.getNavios()) {
                escreverNavio(escritor, navio);
            }
            view.exibirMensagem("Partida salva em: " + ARQUIVO_SAVE.toAbsolutePath());
            return true;
        } catch (IOException excecao) {
            view.exibirMensagem("Não foi possível salvar a partida: " + excecao.getMessage());
            return false;
        }
    }

    private void escreverGrade(BufferedWriter escritor, String id, char[][] grade) throws IOException {
        escritor.write("GRADE|" + id);
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            escritor.write("|");
            escritor.write(grade[linha]);
        }
        escritor.newLine();
    }

    private void escreverNavio(BufferedWriter escritor, Unidade navio) throws IOException {
        escritor.write("NAVIO|" + navio.getId()
                + "|" + navio.getTamanho()
                + "|" + formatarPosicoes(navio.getPosicoes())
                + "|" + navio.getPartesAtingidas()
                + "|" + navio.isAfundado()
                + "|" + formatarPosicoes(navio.getPosicoesAtingidas())
                + "|FIM");
        escritor.newLine();
    }

    private String formatarPosicoes(int[] posicoes) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < posicoes.length; i++) {
            if (i > 0) {
                texto.append(',');
            }
            texto.append(posicoes[i]);
        }
        return texto.toString();
    }

    private boolean carregarPartida() {
        Jogador jogadorCarregado = new Jogador("Jogador", "J");
        Computador computadorCarregado = new Computador();
        try {
            String vezCarregada = new LeitorArquivo().carregar(
                    ARQUIVO_SAVE, jogadorCarregado, computadorCarregado);
            jogador = jogadorCarregado;
            computador = computadorCarregado;
            vez = vezCarregada;
            view.exibirMensagem("Partida carregada com sucesso.");
            return true;
        } catch (IOException | IllegalArgumentException excecao) {
            view.exibirMensagem("Não foi possível carregar a partida: " + excecao.getMessage());
            return false;
        }
    }

    private int lerCoordenada(String mensagem) {
        while (true) {
            String entrada = view.lerEntrada(mensagem);
            try {
                int valor = Integer.parseInt(entrada.trim());
                if (valor >= 1 && valor <= Tabuleiro.LINHAS) {
                    return valor - 1;
                }
            } catch (NumberFormatException excecao) {
                // Uma nova tentativa é solicitada abaixo.
            }
            view.exibirMensagem("Digite um número entre 1 e 10.");
        }
    }

}
