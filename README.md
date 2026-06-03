# Árvore Rubro-Negra (Red-Black Tree) em Java

Implementação completa e didática de uma **Árvore Rubro-Negra** em Java, com
suporte a **inserção**, **impressão** e **exclusão** de chaves inteiras. O
projeto segue o algoritmo clássico descrito em *CLRS — Introduction to
Algorithms* (Cormen, Leiserson, Rivest e Stein), utilizando um nó sentinela
`NIL`.

---

## Índice

- [O que é uma Árvore Rubro-Negra](#o-que-é-uma-árvore-rubro-negra)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Autores](#autores)
- [Como compilar e executar](#como-compilar-e-executar)
- [Saída esperada](#saída-esperada)
- [Detalhes da implementação](#detalhes-da-implementação)
  - [O nó sentinela NIL](#o-nó-sentinela-nil)
  - [Rotações](#rotações)
  - [Inserção](#inserção)
  - [Exclusão](#exclusão)
  - [Os 4 casos do deleteFixup](#os-4-casos-do-deletefixup)
  - [Impressão da árvore](#impressão-da-árvore)
- [Complexidade](#complexidade)
- [Solução de problemas](#solução-de-problemas)
- [Referências](#referências)

---

## O que é uma Árvore Rubro-Negra

Uma Árvore Rubro-Negra é uma **árvore binária de busca (BST) auto-balanceada**.
Cada nó recebe uma cor (vermelho ou preto), e um conjunto de regras garante que
a árvore permaneça aproximadamente balanceada, assegurando operações em tempo
**O(log n)** no pior caso.

As cinco propriedades que devem ser sempre mantidas:

1. Todo nó é **vermelho (RED)** ou **preto (BLACK)**.
2. A **raiz** é sempre **preta**.
3. Toda **folha** (nó sentinela `NIL`) é **preta**.
4. Se um nó é **vermelho**, então **ambos os filhos são pretos**
   (não existem dois nós vermelhos consecutivos).
5. Para cada nó, **todos os caminhos** dele até as folhas descendentes contêm
   o **mesmo número de nós pretos** (mesma *altura-preta*).

Sempre que uma inserção ou remoção viola alguma dessas regras, a árvore é
corrigida por meio de **recolorações** e **rotações**.

---

## Autores

- **Arthur Batelo Bastos**
- **Gabriel Boni**

---

## Estrutura do projeto

Trata-se de um projeto **Maven** (gerado no IntelliJ IDEA). Por isso, o código
fonte fica no diretório padrão `src/main/java`.

```
Arvore-Rubro-Negra/
├── src/
│   └── main/
│       └── java/
│           └── RedBlackTree.java   <- toda a implementação + método main()
├── pom.xml                         <- arquivo Maven
├── README.md                       <- este arquivo
└── ...
```

Todo o código está concentrado em **um único arquivo** (`RedBlackTree.java`)
para facilitar a leitura e a entrega:

- `class RedBlackTree` — a árvore em si.
- `class Node` (interna) — representa um nó (chave, cor, pai, filhos).
- `main()` — demonstração de uso.

> **Importante:** o arquivo precisa estar em `src/main/java` para que o Maven e o
> IntelliJ o reconheçam como código-fonte (e exibam a seta verde ▶ de execução).

---

## Como compilar e executar

### Opção 1 — Pelo IntelliJ IDEA

1. Abra o arquivo **`src/main/java/RedBlackTree.java`** (sempre o `.java`,
   nunca a aba `.class`).
2. Clique na **seta verde ▶** ao lado do método `public static void main`.
3. Escolha **Run 'RedBlackTree.main()'**.
4. Veja o resultado na janela **Run** (parte inferior).

> Atalho: posicione o cursor dentro do arquivo e pressione **Ctrl + Shift + F10**.

### Opção 2 — Pelo Terminal

```bash
cd src/main/java
javac -encoding UTF-8 RedBlackTree.java
java RedBlackTree
```

> O parâmetro `-encoding UTF-8` é importante por causa dos acentos
> (Árvore, não, etc.).

---

## Saída esperada

O método `main()` executa a seguinte demonstração:

1. Insere as chaves `[7, 3, 18, 10, 22, 8, 11, 26]`.
2. Imprime a árvore resultante.
3. Remove `18`, depois `11`, depois `3` — imprimindo após cada remoção.

A impressão é feita "deitada" (a raiz fica à esquerda, subárvores direitas
acima e subárvores esquerdas abaixo). Cada nó é mostrado como `chave(Cor)`,
onde `R` = vermelho e `B` = preto. Exemplo do estado inicial:

```
                         /----- 26(R)
                 /----- 22(B)
         /----- 18(R)
         |       |       /----- 11(R)
         |       \----- 10(B)
         |               \----- 8(R)
 /----- 7(B)
 |       \----- 3(B)
```

---

## Detalhes da implementação

### O nó sentinela NIL

Em vez de usar `null` para representar folhas e ausência de filhos, a
implementação usa um único objeto especial, o `NIL`, que é **sempre preto**.

Isso simplifica bastante o código: não é preciso verificar `null` o tempo
todo, e podemos acessar livremente `NIL.color`, `NIL.left`, etc. O `NIL`
também serve como "pai da raiz".

### Rotações

As rotações reorganizam localmente a árvore mantendo a ordem da BST. São a base
do rebalanceamento.

- **`leftRotate(x)`** — o filho direito de `x` sobe e ocupa o lugar de `x`.
- **`rightRotate(x)`** — o filho esquerdo de `x` sobe e ocupa o lugar de `x`.

```
      x                 y
     / \               / \
    a   y    ==>       x   c
       / \            / \
      b   c          a   b
        (leftRotate em x)
```

### Inserção

`insert(int key)`:

1. Insere a chave como em uma BST comum.
2. Pinta o novo nó de **vermelho**.
3. Chama `insertFixup` para corrigir eventuais violações (dois vermelhos
   consecutivos), usando recolorações e rotações.
4. Garante, ao final, que a **raiz seja preta**.

### Exclusão

`delete(int key)` segue o algoritmo de CLRS:

1. Localiza o nó `z` com a chave (via `search`). Se não existir, nada é feito.
2. Trata os casos estruturais:
   - **`z` sem filho esquerdo** → transplanta o filho direito.
   - **`z` sem filho direito** → transplanta o filho esquerdo.
   - **`z` com dois filhos** → encontra o **sucessor** (`minimum` da subárvore
     direita), que assume o lugar de `z`.
3. Guarda a **cor original** do nó efetivamente removido/movido.
4. Se essa cor era **preta**, a propriedade de altura-preta pode ter sido
   violada → chama `deleteFixup`.

Métodos auxiliares envolvidos:

- **`transplant(u, v)`** — substitui a subárvore enraizada em `u` pela
  enraizada em `v`, ajustando apenas as ligações de **pai**.
- **`minimum(x)`** — retorna o nó de menor chave da subárvore de `x`.
- **`search(key)`** — localiza o nó de uma chave, ou retorna `NIL`.

### Os 4 casos do deleteFixup

Após remover um nó preto, o nó `x` passa a carregar um **"preto extra"**. O
`deleteFixup` elimina esse desequilíbrio tratando quatro casos (cada um com seu
espelho esquerda/direita). Considerando `w` como o **irmão** de `x`:

| Caso | Condição | Ação |
|------|----------|------|
| **1** | `w` é **vermelho** | Recolore `w` e o pai, rotaciona o pai; reduz ao caso 2, 3 ou 4. |
| **2** | `w` é preto e **ambos os filhos de `w` são pretos** | Pinta `w` de vermelho e move o "preto extra" para o pai. |
| **3** | `w` é preto, filho **próximo vermelho** e **distante preto** | Recolore e rotaciona `w`; transforma no caso 4. |
| **4** | `w` é preto e o **filho distante é vermelho** | Recolore e rotaciona o pai; **elimina** o preto extra e encerra. |

No código, cada bloco está identificado por comentários (`// Caso 1`,
`// Caso 2`, etc.), inclusive nas versões espelhadas.

### Impressão da árvore

`print()` chama o auxiliar recursivo `printHelper`, que percorre a árvore e
desenha o recuo proporcional à profundidade, exibindo `chave(Cor)`. É uma
visualização rotacionada 90°: leia inclinando a cabeça para a esquerda.

---

## Complexidade

| Operação   | Tempo (pior caso) |
|------------|-------------------|
| Busca      | O(log n)          |
| Inserção   | O(log n)          |
| Exclusão   | O(log n)          |

O balanceamento garantido pelas propriedades Rubro-Negras mantém a altura da
árvore em **O(log n)**, onde `n` é o número de nós.

---

## Solução de problemas

- **Não aparece a seta verde ▶ de execução:** verifique se o arquivo está em
  `src/main/java`. Se estiver solto em `src`, o IntelliJ não o reconhece como
  código-fonte. Alternativamente, clique com o botão direito na pasta de origem
  e use **Mark Directory as → Sources Root**.
- **Aparece "Nothing here" ao clicar em executar:** você está na aba
  `RedBlackTree.class` (bytecode decompilado). Feche-a e abra o `RedBlackTree.java`.
- **Erro `cannot find symbol` ou erros antigos persistentes:** apague eventuais
  arquivos `.class` antigos e faça **Build → Rebuild Project** no IntelliJ.
- **Acentos quebrados no terminal:** sempre compile com `javac -encoding UTF-8`.

---

## Referências

- CORMEN, T. H.; LEISERSON, C. E.; RIVEST, R. L.; STEIN, C.
  *Introduction to Algorithms*. 3ª ed. MIT Press — Capítulo 13:
  *Red-Black Trees*.
