package com.example.finalproject_sudokugame;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuAiService {

    private final Client client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface SudokuCallback {
        void onSuccess(int[][] puzzle, int[][] solution);
        void onError(String error);
    }

    public SudokuAiService(String apiKey) {

        this.client = Client.builder().apiKey(apiKey).build();
    }

    public void generateSudoku(int emptyCells, SudokuCallback callback) {
        executor.execute(() -> {
            try {
                String prompt = "Generate a 9x9 Sudoku puzzle. " +
                        "Return a JSON object with two fields: 'puzzle' (the board with " + emptyCells + " zeros) " +
                        "and 'solution' (the fully solved board). " +
                        "Each should be a 9x9 array of integers. " +
                        "Ensure the puzzle has a unique solution and result ONLY in JSON format.";

                Log.d("SudokuAI", "Calling model: gemini-2.0-flash");
                GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash", prompt, null);

                String jsonText = response.text();

                if (jsonText.contains("```json")) {
                    jsonText = jsonText.substring(jsonText.indexOf("```json") + 7, jsonText.lastIndexOf("```"));
                } else if (jsonText.contains("```")) {
                    jsonText = jsonText.substring(jsonText.indexOf("```") + 3, jsonText.lastIndexOf("```"));
                }
                
                JSONObject json = new JSONObject(jsonText.trim());
                callback.onSuccess(
                    parseArray(json.getJSONArray("puzzle")), 
                    parseArray(json.getJSONArray("solution"))
                );

            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
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
