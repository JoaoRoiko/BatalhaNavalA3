# Batalha Naval 🚢💥

Jogo de Batalha Naval feito em Java puro (sem bibliotecas externas), rodando no console.
O jogador posiciona três navios em um tabuleiro 10x10 e disputa turnos com o computador
até alguém afundar todos os navios do outro.

## Funcionalidades

- Tabuleiro 10x10 colorido, com colunas e linhas numeradas
- Três níveis de dificuldade para o computador (fácil, médio e difícil)
- Placar de vitórias e derrotas que fica salvo entre as partidas
- Posicionamento manual dos navios (horizontal ou vertical)
- Computador posiciona os navios e atira de forma aleatória
- Emojis nas mensagens: 💥 acerto, 🌊 água, 💾 salvar, 📂 carregar, 🏆 vitória
- Animação de explosão em arte ASCII quando um navio afunda
- Placar de navios afundados em cada tabuleiro
- Salvar e carregar partida em arquivo de texto (`batalha_naval_save.txt`)

## Como rodar

### Windows (mais fácil)

Dê dois cliques em `run.bat` (ou execute ele pelo terminal). O script compila o projeto
e abre o jogo já com o console em UTF-8, que é o que faz os emojis aparecerem.

### Pelo terminal

```bash
javac -encoding UTF-8 -d out -sourcepath src src/controller/JogoController.java
java -Dstdout.encoding=UTF-8 -cp out controller.JogoController
```

No Linux/macOS o passo do `-Dstdout.encoding` não é necessário:

```bash
javac -encoding UTF-8 -d out -sourcepath src src/controller/JogoController.java
java -cp out controller.JogoController
```

### Pelo VS Code

Abra a pasta do projeto e rode a classe `controller.JogoController`.
Se os emojis aparecerem como `?`, use o `run.bat` ou rode `chcp 65001` no terminal
antes de iniciar o jogo (é o console do Windows trocando para UTF-8).

## Como jogar

1. Escolha **1 - Nova partida**.
2. Escolha a dificuldade do computador: **1 - Fácil**, **2 - Médio** ou **3 - Difícil**.
3. Posicione os três navios informando linha, coluna e orientação (`H` ou `V`).
   As coordenadas vão de 1 a 10.
4. A cada turno escolha **1 - Atirar** e informe linha e coluna do disparo.
5. Ganha quem afundar os três navios do adversário primeiro.

Diferença entre as dificuldades:

| Nível | Comportamento do computador |
|-------|-----------------------------|
| `1` Fácil | sorteia qualquer posição livre |
| `2` Médio | na metade das vezes atira em volta do último acerto |
| `3` Difícil | sempre atira em volta do último acerto |

O placar de vitórias e derrotas aparece no menu principal e fica guardado no arquivo
`batalha_naval_placar.txt`.

Legenda do tabuleiro:

| Símbolo | Significado |
|---------|-------------|
| `~` | água (posição ainda não atingida) |
| `N` | seu navio |
| `X` | acerto em um navio |
| `o` | tiro que caiu na água |

## Estrutura do projeto

```
src/
  controller/JogoController.java   -> fluxo da partida, turnos e save/load
  model/Tabuleiro.java             -> matriz 10x10, regras de tiro
  model/Unidade.java               -> classe base das unidades
  model/Navio.java                 -> navio de 3 partes
  model/Jogador.java               -> jogador e seus navios
  model/Computador.java            -> posicionamento, tiros automáticos e dificuldade
  util/LeitorArquivo.java          -> leitura e validação do arquivo de save
  util/Placar.java                 -> vitórias e derrotas guardadas em arquivo
  view/JogoView.java               -> menus, tabuleiros e mensagens no console
run.bat                            -> compila e executa no Windows
```

## Salvamento

O jogo grava a partida em `batalha_naval_save.txt` na raiz do projeto. O arquivo
é lido de volta pelo menu **Carregar partida salva** e passa por validação antes de
ser usado, para não aceitar um save corrompido.

O placar fica em `batalha_naval_placar.txt`, também na raiz do projeto. Se o arquivo
não existir ou estiver corrompido, o jogo simplesmente começa o placar do zero.
