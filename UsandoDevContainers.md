## O que é um Dev Container?

Um **Dev Container** é um ambiente de desenvolvimento isolado, pré-configurado com todas as ferramentas necessárias para o projeto. Ele assegura que todos que executam o projeto utilizem o mesmo ambiente, evitando divergências e problemas de compatibilidade entre diferentes máquinas.

---

## Pré-requisitos

Antes de começar, você precisa instalar a extensão "Dev Containers" no VS Code:

1. Abra o VS Code
2. Vá em "Extensões" (ícone de quadrado à esquerda ou `Ctrl+Shift+X`)
3. Busque por `Dev Containers` e instale a extensão oficial da Microsoft

---

## Passo a Passo: Abrindo o Projeto no Dev Container

### 1. Abra a pasta do projeto no VS Code

- No VS Code, clique em "Arquivo" > "Abrir Pasta..." e selecione a pasta do laboratório.

### 2. Abra o projeto no Dev Container

- Assim que abrir a pasta, o VS Code deve detectar o arquivo `Dockerfile` e sugerir abrir no Dev Container. Se aparecer um pop-up, clique em **"Reabrir no Container"**.
- Se não aparecer, faça manualmente:
  1. Pressione `F1` ou `Ctrl+Shift+P` para abrir a paleta de comandos.
  2. Digite `Dev Containers: Reabrir no Container` e selecione essa opção.

O VS Code vai baixar a imagem do Docker (pode demorar na primeira vez) e montar o ambiente para você.

### 3. Aguarde a preparação do ambiente

- O processo pode levar alguns minutos na primeira vez.
- Quando terminar, você verá que o terminal do VS Code está rodando **dentro do container** (veja o nome do container no canto inferior esquerdo).

---

## Dicas Importantes

- **Tudo que você fizer dentro do Dev Container é salvo normalmente na sua pasta do projeto.**
- Se precisar instalar pacotes ou rodar comandos, use o terminal do VS Code (ele já estará no ambiente correto).
- Se fechar o VS Code, basta reabrir a pasta e repetir o passo 3 para voltar ao container.

---

## Como sair do Dev Container?

Para sair do Dev Container e voltar a usar o VS Code normalmente no seu computador:

1. Clique no canto inferior esquerdo do VS Code, onde aparece o nome do container (ou um símbolo verde).
2. Selecione a opção **"Close Remote Connection"** (Fechar conexão remota) ou **"Reopen Folder Locally"** (Reabrir pasta localmente).
3. O VS Code fechará o container e abrirá a pasta normalmente no seu computador.

Você também pode usar a paleta de comandos (`Ctrl+Shift+P`), digitar `Dev Containers: Close Remote Connection` e selecionar essa opção.