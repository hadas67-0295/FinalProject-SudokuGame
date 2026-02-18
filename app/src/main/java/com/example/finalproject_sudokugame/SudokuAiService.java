package com.example.finalproject_sudokugame;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuAiService {

    private static SudokuAiService instance;
    private final Client client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isGenerating = false;

    public interface SudokuCallback {
        void onSuccess(int[][] puzzle, int[][] solution);
        void onError(String error);
    }

    private SudokuAiService(String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
    }

    public static synchronized SudokuAiService getInstance(String apiKey) {
        if (instance == null) {
            instance = new SudokuAiService(apiKey);
        }
        return instance;
    }

    public boolean isGenerating() {
        return isGenerating;
    }

    public void generateSudoku(int emptyCells, SudokuCallback callback) {
        if (isGenerating) {
            callback.onError("Request already in progress");
            return;
        }
        isGenerating = true;
        executor.execute(() -> {
            try {
                int attempts = 0;
                boolean success = false;
                String lastError = "";

                while (attempts < 3 && !success) {
                    attempts++;
                    try {
                        Log.d("SudokuAI", "Generation attempt " + attempts + " for " + emptyCells + " empty cells");

                        String prompt = "You are a Sudoku puzzle generator.\n" +
                                "\n" +
                                "Your task is to generate a VALID, SOLVABLE 9×9 Sudoku puzzle WITH A UNIQUE SOLUTION.\n" +
                                "\n" +
                                "You MUST strictly follow all rules below.\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "GENERAL SUDOKU RULES\n" +
                                "────────────────────────\n" +
                                "A valid Sudoku grid must satisfy ALL of the following:\n" +
                                "1. The grid is exactly 9 rows × 9 columns.\n" +
                                "2. Each row contains the numbers 1 through 9 exactly once.\n" +
                                "3. Each column contains the numbers 1 through 9 exactly once.\n" +
                                "4. Each 3×3 subgrid contains the numbers 1 through 9 exactly once.\n" +
                                "5. No duplicates are allowed in any row, column, or subgrid.\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "OUTPUT STRUCTURE (VERY IMPORTANT)\n" +
                                "────────────────────────\n" +
                                "You must output EXACTLY ONE JSON object.\n" +
                                "Do NOT include markdown, explanations, comments, or extra text.\n" +
                                "Output RAW JSON ONLY.\n" +
                                "\n" +
                                "The JSON object must have EXACTLY these two keys:\n" +
                                "- \"solution\"\n" +
                                "- \"puzzle\"\n" +
                                "\n" +
                                "Structure:\n" +
                                "{\n" +
                                "  \"solution\": [[int,int,...],[...],...],\n" +
                                "  \"puzzle\":   [[int,int,...],[...],...]\n" +
                                "}\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "SOLUTION GRID RULES\n" +
                                "────────────────────────\n" +
                                "1. \"solution\" must be a COMPLETE and VALID Sudoku solution.\n" +
                                "2. It must contain ONLY integers from 1 to 9.\n" +
                                "3. No zeros are allowed in the solution.\n" +
                                "4. The solution must strictly obey all Sudoku rules.\n" +
                                "\n" +
                                "Example of a VALID solution row:\n" +
                                "[5,3,4,6,7,8,9,1,2]\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "PUZZLE GRID RULES\n" +
                                "────────────────────────\n" +
                                "1. \"puzzle\" MUST be derived from \"solution\".\n" +
                                "2. Replace EXACTLY " + emptyCells + " cells with the number 0 (zero).\n" +
                                "3. All other cells MUST be identical to the solution.\n" +
                                "4. Zeros represent empty cells.\n" +
                                "5. The puzzle MUST have EXACTLY ONE valid solution — the provided \"solution\".\n" +
                                "\n" +
                                "Example:\n" +
                                "Solution cell: 7\n" +
                                "Puzzle cell may be:\n" +
                                "- 7 (if revealed)\n" +
                                "- 0 (if hidden)\n" +
                                "\n" +
                                "It may NEVER contain:\n" +
                                "- Any number different from the solution\n" +
                                "- Any number outside 0–9\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "UNIQUENESS REQUIREMENT (CRITICAL)\n" +
                                "────────────────────────\n" +
                                "The puzzle must have ONE AND ONLY ONE valid solution.\n" +
                                "No alternative solutions are allowed.\n" +
                                "If multiple solutions exist, the puzzle is INVALID.\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "CONSISTENCY CHECKS YOU MUST PASS\n" +
                                "────────────────────────\n" +
                                "Before outputting, mentally verify:\n" +
                                "1. The solution is a valid Sudoku.\n" +
                                "2. The puzzle respects the solution exactly.\n" +
                                "3. The number of zeros in the puzzle is EXACTLY " + emptyCells + ".\n" +
                                "4. Filling the zeros leads ONLY to the provided solution.\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "FORMATTING RULES\n" +
                                "────────────────────────\n" +
                                "- Use square brackets for arrays.\n" +
                                "- Use commas correctly.\n" +
                                "- Do not add trailing commas.\n" +
                                "- Use integers only.\n" +
                                "- No strings, no comments.\n" +
                                "\n" +
                                "────────────────────────\n" +
                                "FINAL INSTRUCTION\n" +
                                "────────────────────────\n" +
                                "Generate ONE Sudoku puzzle now.\n" +
                                "\n" +
                                "- Size: 9×9\n" +
                                "- Empty cells (zeros): " + emptyCells + "\n" +
                                "- Difficulty: logical, solvable by standard Sudoku techniques\n" +
                                "- Output: RAW JSON ONLY\n";

                        GenerateContentResponse response =
                                client.models.generateContent(
                                    "gemini-2.5-flash",
                                        prompt,
                                        null
                                );

                        String jsonText = response.text();
                        if (jsonText == null) throw new Exception("AI returned null response");

                        jsonText = jsonText.trim();
                        int start = jsonText.indexOf("{");
                        int end = jsonText.lastIndexOf("}");

                        if (start != -1 && end != -1 && end > start) {
                            jsonText = jsonText.substring(start, end + 1);
                        }

                        JSONObject json = new JSONObject(jsonText);
                        int[][] puzzle = parseArray(json.getJSONArray("puzzle"));
                        int[][] solution = parseArray(json.getJSONArray("solution"));

                        if (!isValidSudoku(solution)) {
                            throw new Exception("AI generated an invalid Sudoku solution (duplicates found).");
                        }

                        int actualEmpty = countEmptyCells(puzzle);
                        if (Math.abs(actualEmpty - emptyCells) > 2) {
                            Log.w("SudokuAI", "AI generated " + actualEmpty + " empty cells, requested " + emptyCells);
                        }

                        if (!isValidPuzzle(puzzle, solution)) {
                            throw new Exception("AI generated puzzle does not match solution.");
                        }

                        callback.onSuccess(puzzle, solution);
                        success = true;

                    } catch (Exception e) {
                        Log.e("SudokuAI", "Error generating sudoku (Attempt " + attempts + ")", e);
                        lastError = e.getMessage();
                    }
                }

                if (!success) {
                    callback.onError("Failed to generate valid puzzle after " + attempts + " attempts. Last error: " + lastError);
                }
            } finally {
                isGenerating = false;
            }
        });
    }

    private boolean isValidSudoku(int[][] board) {
        for (int i = 0; i < 9; i++) {
            boolean[] rowCheck = new boolean[10];
            boolean[] colCheck = new boolean[10];
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    if (rowCheck[board[i][j]]) return false;
                    rowCheck[board[i][j]] = true;
                }

                if (board[j][i] != 0) {
                    if (colCheck[board[j][i]]) return false;
                    colCheck[board[j][i]] = true;
                }
            }
        }

        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] boxCheck = new boolean[10];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int num = board[boxRow + i][boxCol + j];
                        if (num != 0) {
                            if (boxCheck[num]) return false;
                            boxCheck[num] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    private int countEmptyCells(int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) count++;
            }
        }
        return count;
    }

    private boolean isValidPuzzle(int[][] puzzle, int[][] solution) {
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                if(puzzle[i][j] != 0 && puzzle[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] parseArray(JSONArray jsonArray) throws Exception {
        int[][] res = new int[9][9];
        for (int i = 0; i < 9; i++) {
            JSONArray row = jsonArray.getJSONArray(i);
            for (int j = 0; j < 9; j++) {
                res[i][j] = row.getInt(j);
            }
        }
        return res;
    }
}
