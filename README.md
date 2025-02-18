# Space Jockey

Space Jockey é um jogo de tiro em 2D desenvolvido em Java. O objetivo do jogo é controlar uma nave espacial (UFO) e destruir os invasores (Invaders) que aparecem na tela, 
desviando dos tiros inimigos e evitando colisões. O jogo conta com um sistema de pontuação e um ranking para os melhores jogadores.

---
## Projeto
Um projeto final para uma cadeira da faculdade, a ideia foi recriar o jogo Space Jockey de 1982 do Atari2600 em POO.
---

## Visão Geral

O projeto tem como objetivo proporcionar uma experiência de jogo desafiadora, onde o jogador deve:
- Controlar a nave espacial e se mover verticalmente.
- Atirar nos invasores para acumular pontos.
- Evitar colisões e tiros inimigos para manter suas vidas.
- Concorrer para alcançar a maior pontuação possível e entrar no ranking dos melhores jogadores.

---

## Estrutura do Projeto

O projeto está organizado da seguinte forma:

### Classes Principais

- **Jogo.java**  
  Classe principal do jogo. Responsável por inicializar a janela, carregar os recursos, gerenciar o loop principal e tratar a lógica de entrada do jogador.

- **UFO.java**  
  Representa a nave controlada pelo jogador. Gerencia a posição, movimento, quantidade de vidas e a animação de explosão.

- **Invader.java**  
  Representa os inimigos. Cada inimigo possui um tipo específico (avião, helicóptero, canhão, etc.) e pode atirar. A classe controla posição, movimento, disparos e animação de explosão.

- **Tiro.java**  
  Gerencia os tiros disparados tanto pela nave do jogador quanto pelos inimigos, controlando sua posição, movimento e renderização.

- **Ranking.java**  
  Responsável pelo sistema de ranking do jogo. Carrega, salva e exibe os melhores jogadores com base na pontuação.

- **Elemento.java**  
  Classe base para todos os elementos do jogo (nave, inimigos, tiros, etc.). Define propriedades comuns, como posição, tamanho, velocidade e métodos para desenhar e atualizar os elementos.

- **Player.java**  
  Representa um jogador no ranking, armazenando nome e pontuação.

- **Texto.java**  
  Gerencia a exibição de textos no jogo, incluindo a utilização de uma fonte personalizada.

- **Util.java**  
  Contém métodos utilitários, como verificação de colisões entre os elementos do jogo.

### Assets

- **Sons:**  
  Arquivos de áudio para efeitos sonoros, como explosões e tiros.

- **Sprites:**  
  Imagens utilizadas para representar a nave, os invasores, explosões e as vidas.

- **Fonte:**  
  Fonte personalizada para exibição de textos dentro do jogo.

---

## Como Jogar

- **Movimentação:** Use as setas para cima e para baixo para mover a nave.
- **Disparo:** Pressione a barra de espaço para atirar.
- **Objetivo:** Destrua os invasores para ganhar pontos, evite tiros inimigos e colisões, e tente alcançar a maior pontuação possível para entrar no ranking.

---
