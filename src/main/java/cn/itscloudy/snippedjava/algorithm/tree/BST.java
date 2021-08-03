package cn.itscloudy.snippedjava.algorithm.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree
 */
public interface BST<N extends BSTNode<N>> {

    N top();

    void insert(int i, String words);

    void delete(int i);

    default N search(int i) {
        return search(i, top());
    }

    private N search(int i, N parent) {
        if (parent == null) {
            return null;
        }
        if (i == parent.i) {
            return parent;
        }
        if (parent.leftMightContain(i)) {
            return search(i, parent.left);
        }
        return search(i, parent.right);
    }

    default void print() {
        new Printer<>(this).print();
    }

    class Printer<N extends BSTNode<N>> {

        private final ArrayList<String> cellStrings;
        private final N top;
        private final int cellWidth;
        private final int width;
        private final int height;

        public Printer(BST<N> bst) {
            this.top = bst.top();
            this.cellStrings = new ArrayList<>();
            cellWidth = cellWidthOf(top);
            height = heightOf(top);
            width = cellWidth * (int) Math.pow(2, height - 1);
        }

        private void print() {
            collectStrings(top, 0);
            for (int i = 0; i < cellStrings.size(); i++) {
                if (cellStrings.get(i) == null) {
                    cellStrings.set(i, cellString(""));
                }
            }

            int level = 0;
            while (level < height) {
                int levelStart = (int) Math.pow(2, level) - 1;
                int levelEnd = (int) Math.pow(2, level + 1) - 1;

                print(cellStrings.subList(levelStart, levelEnd), width);
                System.out.println();
                level++;
            }
        }

        private void collectStrings(N node, int index) {
            if (node == null) {
                setCellString(index, null);
                return;
            }

            String cellString = cellString(node.getWords());
            setCellString(index, cellString);

            collectStrings(node.left, index * 2 + 1);
            collectStrings(node.right, index * 2 + 2);
        }

        private void setCellString(int index, String string) {
            if (index >= cellStrings.size()) {
                for (int i = cellStrings.size(); i <= index; i++) {
                    cellStrings.add(null);
                }
            }
            cellStrings.set(index, string);
        }

        private String cellString(String words) {
            int startIndex = (cellWidth - words.length()) / 2;
            StringBuilder sb = new StringBuilder();
            int index = 0;
            while (index < startIndex) {
                sb.append(' ');
                index++;
            }
            sb.append(words);
            index += words.length();
            while (index < cellWidth) {
                sb.append(' ');
                index++;
            }
            return sb.toString();
        }

        private void print(List<String> words, int len) {
            int wordsLen = words.size() * cellWidth;
            int gapSize = (len - wordsLen) / (words.size() + 1);

            int index = 0;
            while (index < words.size()) {
                int gi = 0;
                while (gi < gapSize) {
                    System.out.print(' ');
                    gi++;
                }
                System.out.print(words.get(index));
                index++;
            }
        }

        private int heightOf(N n) {
            if (n == null) {
                return 0;
            }
            return Math.max(heightOf(n.left) + 1, heightOf(n.right) + 1);
        }

        private int cellWidthOf(N n) {
            if (n == null) {
                return 0;
            }
            int max = n.getWords().length() + 2;
            max = Math.max(max, cellWidthOf(n.left));
            max = Math.max(max, cellWidthOf(n.right));
            return max;
        }
    }
}
