public class RedBlackTree {

    private static final boolean RED = true;

    private static final boolean BLACK = false;

    private class Node {

        int key;
        /** Cor do nó: {@link RedBlackTree#RED} ou {@link RedBlackTree#BLACK}. */
        boolean color;
        Node left;
        Node right;
        Node parent;

        Node(int key) {
            this.key = key;
            this.color = RED;
            this.left = NIL;
            this.right = NIL;
            this.parent = NIL;
        }
    }

    private final Node NIL;

    private Node root;

    public RedBlackTree() {
        NIL = new Node(0);
        NIL.color = BLACK;
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
        root = NIL;
    }

    /* ===================================================================== */
    /* ROTAÇÕES                                                               */
    /* ===================================================================== */


    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }


    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    /* ===================================================================== */
    /* INSERÇÃO                                                               */
    /* ===================================================================== */

    public void insert(int key) {
        Node z = new Node(key);
        Node y = NIL;
        Node x = root;

        // Busca a posição de inserção como em uma BST comum.
        while (x != NIL) {
            y = x;
            if (z.key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y == NIL) {
            root = z;
        } else if (z.key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.left = NIL;
        z.right = NIL;
        z.color = RED;

        insertFixup(z);
    }

    private void insertFixup(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right; // tio
                if (y.color == RED) {
                    // Caso 1: tio vermelho -> recolorir.
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        // Caso 2: tio preto, z é filho direito -> rotação à esquerda.
                        z = z.parent;
                        leftRotate(z);
                    }
                    // Caso 3: tio preto, z é filho esquerdo -> rotação à direita.
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left; // tio (espelhado)
                if (y.color == RED) {
                    // Caso 1 (espelhado): tio vermelho -> recolorir.
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        // Caso 2 (espelhado): rotação à direita.
                        z = z.parent;
                        rightRotate(z);
                    }
                    // Caso 3 (espelhado): rotação à esquerda.
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    /* ===================================================================== */
    /* EXCLUSÃO                                                               */
    /* ===================================================================== */

    private Node minimum(Node x) {
        while (x.left != NIL) {
            x = x.left;
        }
        return x;
    }

    private Node search(int key) {
        Node current = root;
        while (current != NIL && current.key != key) {
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return current;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == NIL) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }


    public void delete(int key) {
        Node z = search(key);
        if (z == NIL) {
            System.out.println("Chave " + key + " não encontrada. Nada foi removido.");
            return;
        }

        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;

        if (z.left == NIL) {
            // z não tem filho esquerdo: substitui z pelo filho direito.
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            // z não tem filho direito: substitui z pelo filho esquerdo.
            x = z.left;
            transplant(z, z.left);
        } else {
            // z tem dois filhos: usa o sucessor (mínimo da subárvore direita).
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                // y é filho direto de z.
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        // Se a cor removida era preta, pode haver violação de altura-preta.
        if (yOriginalColor == BLACK) {
            deleteFixup(x);
        }
    }

    private void deleteFixup(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right; // irmão de x

                // Caso 1: o irmão w é vermelho.
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }

                // Caso 2: w é preto e ambos os filhos de w são pretos.
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    // Caso 3: w é preto, filho esquerdo vermelho e direito preto.
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    // Caso 4: w é preto e o filho direito (distante) é vermelho.
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left; // irmão de x (espelhado)

                // Caso 1 (espelhado): o irmão w é vermelho.
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }

                // Caso 2 (espelhado): w é preto e ambos os filhos de w são pretos.
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    // Caso 3 (espelhado): w é preto, filho direito vermelho e esquerdo preto.
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    // Caso 4 (espelhado): w é preto e o filho esquerdo (distante) é vermelho.
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        // Remove o "preto extra", garantindo que x fique preto.
        x.color = BLACK;
    }

    /* ===================================================================== */
    /* IMPRESSÃO                                                              */
    /* ===================================================================== */

    public void print() {
        if (root == NIL) {
            System.out.println("(árvore vazia)");
            return;
        }
        printHelper(root, "", true);
    }

    private void printHelper(Node node, String prefix, boolean isRight) {
        if (node == NIL) {
            return;
        }
        printHelper(node.right, prefix + (isRight ? "        " : " |      "), true);

        String color = (node.color == RED) ? "R" : "B";
        System.out.println(prefix + (isRight ? " /----- " : " \\----- ")
                + node.key + "(" + color + ")");

        printHelper(node.left, prefix + (isRight ? " |      " : "        "), false);
    }

    /* ===================================================================== */
    /* MAIN - DEMONSTRAÇÃO                                                    */
    /* ===================================================================== */

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();

        int[] valores = {7, 3, 18, 10, 22, 8, 11, 26};
        System.out.println("Inserindo os valores: [7, 3, 18, 10, 22, 8, 11, 26]");
        for (int v : valores) {
            tree.insert(v);
        }

        System.out.println("\n===== Árvore após as inserções =====");
        tree.print();

        System.out.println("\n===== Removendo 18 =====");
        tree.delete(18);
        tree.print();

        System.out.println("\n===== Removendo 11 =====");
        tree.delete(11);
        tree.print();

        System.out.println("\n===== Removendo 3 =====");
        tree.delete(3);
        tree.print();
    }
}
