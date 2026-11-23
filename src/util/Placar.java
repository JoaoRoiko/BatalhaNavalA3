package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Vitórias e derrotas acumuladas, guardadas em um arquivo de texto simples.
 */
public class Placar {
    private final Path arquivo;
    private int vitorias;
    private int derrotas;

    public Placar(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    public void registrarVitoria() {
        vitorias++;
        salvar();
    }

    public void registrarDerrota() {
        derrotas++;
        salvar();
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    private void carregar() {
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            if (linhas.size() < 2) {
                return;
            }
            vitorias = Integer.parseInt(linhas.get(0).trim());
            derrotas = Integer.parseInt(linhas.get(1).trim());
        } catch (IOException | NumberFormatException excecao) {
            // Placar ilegível ou corrompido: começa do zero em vez de quebrar o jogo.
            vitorias = 0;
            derrotas = 0;
        }
    }

    private void salvar() {
        try {
            Files.write(arquivo, (vitorias + "\n" + derrotas + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException excecao) {
            // Sem o placar o jogo continua; só não fica registrado no disco.
        }
    }
}
