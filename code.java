import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Comparator;

 class SlidingPuzzle {

    private int size;
    private int puzzle[][];
    private final int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private final int l_moves = moves.length;

    public SlidingPuzzle(int puzzle[][]) {
        this.puzzle = puzzle;
        this.size = puzzle.length;
        aStar();
    }

    class Node {

        int puzzle[][];
        int blankX, blankY;
        Node previous;
        int f, h, g;


        public Node(Node previous, int puzzle[][]) {
            this.previous = previous;
            this.puzzle = puzzle;

            // init start node with blankX, blankY and manhattan distance
            if(previous == null) {
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {

                        int n = puzzle[x][y];

                        if (n == 0) {
                            blankX = x;
                            blankY = y;
                        }else {
                            int x1 = (n - 1) / size;
                            int y1 = (n - 1) % size;
                            this.h += Math.abs(x - x1) + Math.abs(y - y1);
                        }

                    }
                }
            }
        }

        private boolean inBoard(int x, int y) {
            return (x >= 0 && y >= 0 && x < size && y < size);
        }

        private void swapNumbers(int x1, int y1, int x2, int y2) {
            int tmp = puzzle[x1][y1];
            puzzle[x1][y1] = puzzle[x2][y2];
            puzzle[x2][y2] = tmp;
        }

        /*
            from a boardstate to a childstate, manhattan distance can only change by
            +1 or -1, so I don't need to calculate the manhattan distance for the whole board.
         */
        public ArrayList<Node> getChildren() {
            ArrayList<Node> children = new ArrayList<>();

            for (int i = 0; i < l_moves; i++) {

                if (inBoard(blankX + moves[i][0], blankY + moves[i][1])) {

                    int n1 = puzzle[blankX + moves[i][0]][blankY + moves[i][1]];
                    int m1 = calcManhattan(blankX + moves[i][0], (n1 - 1) / size, blankY + moves[i][1],  (n1 - 1) % size);

                    swapNumbers(blankX, blankY, blankX + moves[i][0], blankY + moves[i][1]);

                    int copy[][] = new int[size][size];
                    for (int j = 0; j < size; j++) {
                        copy[j] = puzzle[j].clone();
                    }

                    int n2 = copy[blankX][blankY];
                    int x2 = (n2 - 1) / size;
                    int y2 = (n2 - 1) % size;

                    Node child = new Node(this, copy);
                    child.blankX = this.blankX + moves[i][0];
                    child.blankY = this.blankY + moves[i][1];

                    int m2 = calcManhattan(blankX, x2, blankY, y2);
                    int nm;

                    if(m1 < m2) {
                        nm = 1;
                    }else {
                        nm = -1;
                    }

                    child.h = this.h + nm;
                    children.add(child);

                    swapNumbers(blankX, blankY, blankX + moves[i][0], blankY + moves[i][1]);
                }
            }
            return children;
        }

        public int calcManhattan(int x1, int x2, int y1, int y2) {
            return Math.abs(x1 - x2) + Math.abs(y1 -y2);
        }

        @Override
        public boolean equals(Object o) {
            Node node = (Node) o;
            return Arrays.deepEquals(puzzle, node.puzzle);
        }
    }

    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node n1, Node n2) {
            return Integer.compare(n1.f, n2.f);
        }
    }

    private void printBoard(int[][] board) {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                String l = "";
                if (board[i][j] < 10) {
                    l = " ";
                }
                System.out.print(l + board[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    private void reconstructPath(Node current) {
        Stack<Node> path = new Stack<>();
        path.push(current);

        int l = 0;
        while (current.previous != null) {
            current = current.previous;
            path.push(current);
            l++;
        }

        while (!path.isEmpty()) {
            Node n = path.pop();
            printBoard(n.puzzle);
        }

        System.out.println("Solution length: " + l);
    }

    private void aStar() {

        Node start = new Node(null, puzzle);
        Comparator<Node> nodeComparator = new NodeComparator();

        ArrayList<Node> closedList = new ArrayList<>();
        PriorityQueue<Node> openList = new PriorityQueue<>(nodeComparator);

        openList.add(start);

        while (!openList.isEmpty()) {

            Node current = openList.poll();
            closedList.add(current);

            if (current.h == 0) {
                reconstructPath(current);
                break;
            }

            ArrayList<Node> children = current.getChildren();

            for (Node child : children) {

                if (closedList.contains(child) || openList.contains(child) && child.g >= current.g) {
                    continue;
                }

                child.g = current.g + 1;
                child.f = child.g + child.h;

                if(openList.contains(child)){
                    openList.remove(child);
                    openList.add(child);
                }else {
                    openList.add(child);
                }
            }

        }

    }

    public static void main(String[] args) {

        int puzzle[][] =   {{6, 2, 1},
                            {0, 4, 3},
                            {5, 8, 7}};
        long start = System.nanoTime();
        new SlidingPuzzle(puzzle);
        long stop = System.nanoTime();

        double time = (stop - start) / 1000000000.0;

        System.out.println("Time: " +time);
    }
}