import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Sudoku
{

    int totalSolved = 0;
    int totalFailed = 0;
    Map<String, Set<String>> sudokuGraph;

    public Sudoku()
    {
        sudokuGraph = buildGraph();
        load("sudoku-3m.csv");
        System.out.println("Total Solved: " + totalSolved);
        System.out.println("Total Failed: " + totalFailed);
    }

    public Map<String, Set<String>> buildGraph() {
        Map<String, Set<String>> graph = new HashMap<>();

        for (int row = 0; row < 9; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                String cell = "r" + row + "c" + col;
                graph.putIfAbsent(cell, new HashSet<>());

                for (int c = 0; c < 9; c++)
                {
                    if (c != col) {
                        String neighbor = "r" + row + "c" + c;
                        graph.get(cell).add(neighbor);
                    }
                }

                for (int r = 0; r < 9; r++)
                {
                    if (r != row) {
                        String neighbor = "r" + r + "c" + col;
                        graph.get(cell).add(neighbor);
                    }
                }

                int boxRowStart = (row / 3) * 3;
                int boxColStart = (col / 3) * 3;
                for (int r = boxRowStart; r < boxRowStart + 3; r++) {
                    for (int c = boxColStart; c < boxColStart + 3; c++) {
                        if (r != row || c != col) {
                            String neighbor = "r" + r + "c" + c;
                            graph.get(cell).add(neighbor);
                        }
                    }
                }
            }
        }
        return graph;
    }

    public void load(String fileName)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String header = reader.readLine();
            String line;
            int puzzleCount = 0;

            while ((line = reader.readLine()) != null && puzzleCount < 100)
            {
                String[] parts = line.split(",");
                String puzzle = parts[1];
                int clueCount = Integer.parseInt(parts[3]);
                double difficulty = Double.parseDouble(parts[4]);
                int[][] board = convertToBoard(puzzle);
				long start = System.currentTimeMillis();
				boolean solved = dfsPath(board);
				long end = System.currentTimeMillis();
				double timeMs = end - start;
                if (solved) {
                    totalSolved++;
                } else {
                    totalFailed++;
                }

                System.out.println("Puzzle #" + (puzzleCount + 1));
				   printBoard(board);

                System.out.println("Clues: " + clueCount + ", Difficulty: " + difficulty);
                if (solved)
                {
                    System.out.println("Solved: Yes, Time: " + timeMs + " ms\n");
                } else
                {
                    System.out.println("Solved: No, Time: " + timeMs + " ms\n");
                }
                puzzleCount++;
            }

        } catch (IOException e) {
            System.err.println("File Error: " + e.getMessage());
        }
    }


    public int[][] convertToBoard(String puzzle)
    {
        int[][] board = new int[9][9];
        for (int i = 0; i < 81; i++)
        {
            char ch = puzzle.charAt(i);
            if (ch == '.' || ch == '0')
            {
                board[i / 9][i % 9] = 0;
            }
            else {
                board[i / 9][i % 9] = ch- '0';
            }
        }
        return board;
    }

    public boolean dfsPath(int[][] board)
    {
        for (int row = 0; row < 9; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                if (board[row][col] == 0)
                {
                    for (int num = 1; num <= 9; num++)
                    {
                        if (isSafeUsingGraph(board, row, col, num))
                        {
                            board[row][col] = num;
                            if (dfsPath(board))
                            {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
	public void printBoard(int[][] board)
	{
		for (int row = 0; row < 9; row++)
		{
			for (int col = 0; col < 9; col++)
			{
			if (board[row][col] == 0)
			{
				System.out.print(". ");
			} else
			{
				System.out.print(board[row][col] + " ");
			}

			if (col == 2 || col == 5)
				System.out.print("| ");
			}
			System.out.println();
			if (row == 2 || row == 5)
				System.out.println("------+-------+------");
    }
    System.out.println();
}

    public int countTotalEdges()
    {
        int totalEdges = 0;
        for (Set<String> neighbors : sudokuGraph.values())
        {
            totalEdges += neighbors.size();
        }
        return totalEdges / 2;
    }

    public boolean isSafeUsingGraph(int[][] board, int row, int col, int num)
    {
        String cell = "r" + row + "c" + col;
        Set<String> neighbors = sudokuGraph.get(cell);

        for (String neighbor : neighbors) {
            int r = Integer.parseInt(neighbor.substring(1, 2));
            int c = Integer.parseInt(neighbor.substring(3, 4));
            if (board[r][c] == num) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args)
    {
        Sudoku app = new Sudoku();
    }

}
// DFS Time Complexity: O(9^k) (Worst case where k represents number of cells)
// Build Graph Time Complexity: O(1)
// isSafe Time Complexity: O(n) (Worst Case)