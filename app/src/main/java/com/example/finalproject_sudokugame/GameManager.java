package com.example.finalproject_sudokugame;

public class GameManager {

    private int[][] board;
    private int selectedRow;
    private int selectedCol;
    private final int SIZE = 9;
    private int[][] originalBoard;
    private int[][] solution;
    private final int[][] DEFAULT_SOLUTION = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    public GameManager(String param, boolean isDifficulty) {
        board = new int[SIZE][SIZE];
        originalBoard = new int[SIZE][SIZE];
        selectedRow = -1;
        selectedCol = -1;

        if (isDifficulty) {
            solution = new int[SIZE][SIZE];
            for (int r = 0; r < SIZE; r++) {
                System.arraycopy(DEFAULT_SOLUTION[r], 0, board[r], 0, SIZE);
                System.arraycopy(DEFAULT_SOLUTION[r], 0, originalBoard[r], 0, SIZE);
                System.arraycopy(DEFAULT_SOLUTION[r], 0, solution[r], 0, SIZE);
            }

            int emptyCells;
            if (param.equalsIgnoreCase("easy")) {
                emptyCells = 20;
            } else if (param.equalsIgnoreCase("medium")) {
                emptyCells = 40;
            } else {
                emptyCells = 55;
            }

            removeCells(emptyCells);
        } else {
            loadBoardFromString(param);
        }
    }

    public GameManager(String currentBoardStr, String originalBoardStr) {
        this(currentBoardStr, originalBoardStr, null);
    }

    public GameManager(String currentBoardStr, String originalBoardStr, String solutionBoardStr) {
        board = new int[SIZE][SIZE];
        originalBoard = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
        selectedRow = -1;
        selectedCol = -1;

        if (solutionBoardStr == null || solutionBoardStr.isEmpty()) {
            solution = DEFAULT_SOLUTION;
        }

        loadTripleBoards(currentBoardStr, originalBoardStr, solutionBoardStr);
    }

    public GameManager(int[][] board, int[][] solution) {
        this.board = board;
        this.solution = solution;
        this.originalBoard = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(board[r], 0, originalBoard[r], 0, SIZE);
        }
        this.selectedRow = -1;
        this.selectedCol = -1;
    }

    private void loadBoardFromString(String boardState) {
        solution = DEFAULT_SOLUTION;
        int index = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = Character.getNumericValue(boardState.charAt(index));
                board[row][col] = value;
                originalBoard[row][col] = (value == 0) ? 0 : value;
                index++;
            }
        }
    }

    private void loadTripleBoards(String currentStr, String originalStr, String solutionStr) {
        int index = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (index >= currentStr.length() || index >= originalStr.length()) break;

                board[row][col] = Character.getNumericValue(currentStr.charAt(index));
                originalBoard[row][col] = Character.getNumericValue(originalStr.charAt(index));
                
                if (solutionStr != null && index < solutionStr.length()) {
                    solution[row][col] = Character.getNumericValue(solutionStr.charAt(index));
                }
                
                index++;
            }
        }
    }

    private void removeCells(int count) {
        java.util.Random rand = new java.util.Random();
        while (count > 0) {
            int r = rand.nextInt(SIZE);
            int c = rand.nextInt(SIZE);
            if (board[r][c] != 0) {
                board[r][c] = 0;
                originalBoard[r][c] = 0;
                count--;
            }
        }
    }

    /**
     * Returns the currently selected row index.
     * return the selected row (0–8), or -1 if no cell selected
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Returns the currently selected column index.
     * return the selected column (0–8), or -1 if no cell selected
     */
    public int getSelectedCol() {
        return selectedCol;
    }

    /**
     * Returns the value stored in a specific cell of the board.
     * parameter row the row index (0–8)
     * parameter col the column index (0–8)
     * return the value in the cell (0 if empty, 1–9 if filled)
     */
    public int getCell(int row, int col) {
        return board[row][col];
    }

    /**
     * Sets a value in a specific cell of the board.
     * parameter row the row index (0–8)
     * parameter col the column index (0–8)
     * parameter value the number to place (0 for empty, 1–9 for valid values)
     */
    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }

    public int getSolutionCell(int row, int col) {
        return solution[row][col];
    }

    public boolean isOriginalCell(int row, int col) {
        return originalBoard[row][col] != 0;
    }

    public int getOriginalCell(int row, int col) {
        return originalBoard[row][col];
    }

    public String getOriginalBoardString() {
        return getBoardString(originalBoard);
    }

    public String getSolutionBoardString() {
        return getBoardString(solution);
    }

    private String getBoardString(int[][] targetBoard) {
        if (targetBoard == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = targetBoard[row][col];
                if (value < 0 || value > 9) value = 0;
                sb.append(value);
            }
        }
        return sb.toString();
    }


    /**
     * בודק אם תא מסוים מכיל מספר שהמשתמש הכניס
     * מחזיר true אם התא לא היה מלא מההתחלה והמשתמש הכניס מספר
     */
    public boolean isUserCell(int row, int col) {
        return originalBoard[row][col] == 0 && board[row][col] != 0;
    }

    /**
     Checks if placing a given value in a specific cell is valid
     according to Sudoku rules (no duplicates in row, column, or 3x3 block).
     parameter row the row index (0–8)
     parameter col the column index (0–8)
     parameter value the number to check (1–9)
     return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int row, int col, int value){
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == value) return false;
        }
        for (int r = 0; r < 9; r++) {
            if (board[r][col] == value) return false;
        }
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c] == value) return false;
            }
        }
        return true;
    }

    /**
     Checks if the board is completely filled (no empty cells).
     return true if all cells are filled, false if at least one cell is empty
     */
    public boolean isComplete() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0) return false;
            }
        }
        return true;
    }

    /**
     Selects a specific cell on the board.
     parameter row the row index (0–8)
     parameter col the column index (0–8)
     */
    public void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
    }

    /**
     Attempts to place a number in the currently selected cell.
     Only allows the correct number according to the solution.
     parameter number the number to place (1–9)
     return true if the number was successfully placed, false otherwise
     */
    public boolean tryPlaceNumber(int number) {
        if (selectedRow == -1 || selectedCol == -1) {
            return false;
        }
        if (number == solution[selectedRow][selectedCol]) {
            setCell(selectedRow, selectedCol, number);
            return true;
        }
        return false;
    }

    //public boolean solve(){}
    //public void generateBoard(int clues){}
}