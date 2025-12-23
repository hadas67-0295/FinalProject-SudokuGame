package com.example.finalproject_sudokugame;
public class GameManager {

    private int[][] board;
    private int selectedRow;
    private int selectedCol;
    private final int SIZE = 9;

    public GameManager() {
        board = new int[SIZE][SIZE];
        selectedRow = -1;
        selectedCol = -1;
    }

    /**
     Returns the currently selected row index.
     return the selected row (0–8), or -1 if no cell selected
     */
    public int getSelectedRow() {

        return selectedRow;
    }

    /**
     Returns the currently selected column index.
     return the selected column (0–8), or -1 if no cell selected
     */
    public int getSelectedCol() {

        return selectedCol;
    }

    /**
     Returns the value stored in a specific cell of the board.
     parameter row the row index (0–8)
     parameter col the column index (0–8)
     return the value in the cell (0 if empty, 1–9 if filled)
     */
    public int getCell(int row, int col) {

        return board[row][col];
    }

    /**
     Sets a value in a specific cell of the board.
     parameter row the row index (0–8)
     parameter col the column index (0–8)
     parameter value the number to place (0 for empty, 1–9 for valid values)
     */
    public void setCell(int row, int col, int value) {

        board[row][col] = value;
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
     Validates the move using Sudoku rules before placing.
     parameter number the number to place (1–9)
     return true if the number was successfully placed, false otherwise
     */
    public boolean tryPlaceNumber(int number) {
        if (selectedRow == -1 || selectedCol == -1) {
            return false;
        }
        if (isValidMove(selectedRow, selectedCol, number)) {
            setCell(selectedRow, selectedCol, number);
            return true;
        }
        return false;
    }
    //public boolean solve(){}
    //public void generateBoard(int clues){}
}
