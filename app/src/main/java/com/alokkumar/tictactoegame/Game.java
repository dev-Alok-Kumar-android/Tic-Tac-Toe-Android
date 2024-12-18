package com.alokkumar.tictactoegame;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Game extends AppCompatActivity {

    private static class Move {
        int index;
        int score;
        Move(){
            String TAG = "TAG";
            Log.d(TAG, "Move: index: "+index+" score: "+score);
        }
    }

    private String[] board = {"", "", "", "", "", "", "", "", ""};
    private String currentPlayer = "X";
    private String gameMode = "human";
    private String humanSymbol = "X";
    private String aiSymbol = "O";
    private String level = "Easy";
    private int player1Score = 0;
    private int player2Score = 0;
    private Button platAgain,resetButton;
    MediaPlayer gameSuccessSound,dropSound,tfNotificationSound,aiWinSound;
    private int winningPosition=-1;
    private RadioGroup firstMove;

    private final int[][] winningCombos = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {2, 4, 6}, {0, 4, 8}
    };


    private TextView player1ScoreDisplay;
    private TextView player2ScoreDisplay;
    private GridLayout boardGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player1ScoreDisplay = findViewById(R.id.player1Score);
        player2ScoreDisplay = findViewById(R.id.player2Score);
        boardGrid = findViewById(R.id.board);
        resetButton = findViewById(R.id.resetButton);
        platAgain = findViewById(R.id.playAgain);
        Spinner modeSpinner = findViewById(R.id.mode);
        Spinner symbolSpinner = findViewById(R.id.symbol);
        Spinner levelSpinner = findViewById(R.id.level);
        firstMove= findViewById(R.id.firstMove);

        Intent intent = getIntent();
        int game_mode_code =intent.getIntExtra("Game Mode",0);
        Toast.makeText(this,"game_mode_code "+game_mode_code,Toast.LENGTH_SHORT).show();
        switch (game_mode_code){
            case 0:
                break;
            case 1:
                levelSpinner.setVisibility(View.VISIBLE);
                TextView player2=findViewById(R.id.player2);
                player2.setText(R.string.ai);
                TextView player=findViewById(R.id.player1);
                player.setText(R.string.player);
                break;
            case 2:
                gameMode="bt";
                break;
        }

        gameSuccessSound=MediaPlayer.create(this,R.raw.game_success_alert);
        tfNotificationSound=MediaPlayer.create(this,R.raw.tf_notification);
        dropSound=MediaPlayer.create(this,R.raw.drop);
        aiWinSound=MediaPlayer.create(this,R.raw.ai_win);

        if (gameMode.equals("human")) levelSpinner.setVisibility(View.GONE);

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = parent.getItemAtPosition(position).toString();
                resetBoard();
                aiFirstAlert();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                level = "Easy";
            }
        });

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gameMode = position == 1? "ai" : "human";
                if (gameMode.equals("ai")){
                    levelSpinner.setVisibility(View.VISIBLE);
                    TextView player2=findViewById(R.id.player2);
                    player2.setText(R.string.ai);
                    TextView player=findViewById(R.id.player1);
                    player.setText(R.string.player);
                } else {
                    levelSpinner.setVisibility(View.GONE);
                    TextView player1=findViewById(R.id.player1);
                    player1.setText(R.string.player_1);
                    TextView player2=findViewById(R.id.player2);
                    player2.setText(R.string.player_2);
                }
                player2ScoreDisplay.setText(String.valueOf(player2Score));
                resetBoard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        symbolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                humanSymbol = position == 0 ? "X" : "O";
                aiSymbol = humanSymbol.equals("X") ? "O" : "X";
                resetBoard();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        resetButton.setOnClickListener(v -> resetGame());

        platAgain.setOnClickListener(v -> {
            resetBoard();
            if (Objects.equals(gameMode, "ai")) aiFirstAlert();
        });

        for (int i = 0; i < boardGrid.getChildCount(); i++) {
            final int index = i;
            boardGrid.getChildAt(i).setOnClickListener(v -> handleCellClick(index));
        }
        updateScoreDisplay();
    }

    private void handleCellClick(int index) {
        if (board[index].isEmpty()) {
            board[index] = currentPlayer;
            ((TextView) boardGrid.getChildAt(index)).setText(currentPlayer);
            if (checkWin(currentPlayer)) {
                updateScore(currentPlayer);
                if (!gameSuccessSound.isPlaying()) gameSuccessSound.start();
                Toast.makeText(this, currentPlayer + " wins!", Toast.LENGTH_SHORT).show();
                resetButton.setVisibility(View.GONE);
                platAgain.setVisibility(View.VISIBLE);
            } else if (isBoardFull()) {
                if (!tfNotificationSound.isPlaying()) tfNotificationSound.start();
                Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
                resetBoard();
            } else {
                if (!dropSound.isPlaying()) dropSound.start();
                if (gameMode.equals("human") || currentPlayer.equals(humanSymbol)) {
                    switchPlayer();
                }
                if (gameMode.equals("ai") && currentPlayer.equals(aiSymbol)) {
                    aiMove();
                }
            }
        }
        for (int[] combo : winningCombos) {
            if (board[combo[0]].equals(currentPlayer) && board[combo[1]].equals(currentPlayer) && board[combo[2]].equals(currentPlayer)) {
                onGameOver();
            }
        }
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    private boolean checkWin(String player) {
        for (int[] combo : winningCombos) {
            if (board[combo[0]].equals(player) && board[combo[1]].equals(player) && board[combo[2]].equals(player)) {
//                onGameOver();
                return true;
            }
        }
        return false;
    }


    private void onGameOver(){
        for (int i = 0; i < boardGrid.getChildCount(); i++) {
            boardGrid.getChildAt(i).setClickable(false);
        }
        for (int i = 0;i < winningCombos.length; i++) {
            int[] combo = winningCombos[i];
            if (board[combo[0]].equals(currentPlayer) && board[combo[1]].equals(currentPlayer) && board[combo[2]].equals(currentPlayer)) {
                winningPosition=i;
            }
        }
        if (winningPosition!=-1) {
            setLines(winningPosition);
        }
    }

    private boolean isBoardFull() {
        for (String cell : board) {
            if (cell.isEmpty()) return false;
        }
        return true;
    }

    private void resetBoard() {
            resetButton.setVisibility(View.VISIBLE);
            platAgain.setVisibility(View.GONE);
        for (int i = 0; i < board.length; i++) {
            board[i] = "";
            ((TextView) boardGrid.getChildAt(i)).setText("");
        }
        for (int j = 0; j < boardGrid.getChildCount(); j++) {
            boardGrid.getChildAt(j).setClickable(true);
            setLines(j);
        }
        currentPlayer = humanSymbol;
        winningPosition=-1;
    }

    private void resetGame() {
        resetBoard();
        player1Score = 0;
        player2Score = 0;
        updateScoreDisplay();
    }

    private void updateScore(@NonNull String player) {
        if (player.equals(humanSymbol)) {
            player1Score++;
        } else {
            player2Score++;
        }
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        player1ScoreDisplay.setText(String.valueOf(player1Score));
        player2ScoreDisplay.setText(String.valueOf(player2Score));
    }

    private void aiMove() {
        if(level.equals("Medium")) {
            aiMoveMedium();
        }

        else if(level.equals("Hard")) {
          aiMoveHard();
        }

        else {
          aiMoveEasy();
        }

        if (checkWin(aiSymbol)) {
            updateScore(aiSymbol);
            if (!aiWinSound.isPlaying()){
                dropSound.pause();
                aiWinSound.start();
            }
            Toast.makeText(this, aiSymbol + " wins!", Toast.LENGTH_SHORT).show();
            platAgain.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.GONE);
        } else if (isBoardFull()) {
            if (!tfNotificationSound.isPlaying()) tfNotificationSound.start();
            Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
            resetBoard();
        } else {
            switchPlayer();
        }
    }
    boolean isBoardEmpty(){
        for (int i = 0; i < 9; i++) {
            if (!board[i].isEmpty()) return false;
        }
        return true;
    }
    private void aiMoveHard() {
        if (isBoardEmpty()&& Objects.equals(currentPlayer, aiSymbol)){
            int firstMove = getPreComFirstMove();
            board[firstMove]=aiSymbol;
            winningPosition=-1;
            ((TextView)boardGrid.getChildAt(firstMove)).setText(aiSymbol);
        }else {
            Move bestMove = minimax(board, aiSymbol,Integer.MIN_VALUE,Integer.MAX_VALUE);
            board[bestMove.index] = aiSymbol;
            ((TextView) boardGrid.getChildAt(bestMove.index)).setText(aiSymbol);
            for (int i = 0; i < boardGrid.getChildCount(); i++) {
                boardGrid.getChildAt(i).setClickable(true);
            }
        }
        for (int i = 0;i < winningCombos.length; i++) {
            int[] combo = winningCombos[i];
            if (board[combo[0]].equals(currentPlayer) && board[combo[1]].equals(currentPlayer) && board[combo[2]].equals(currentPlayer)) {
                winningPosition=i;
            }
        }
        setLines(winningPosition);
    }

    private int getPreComFirstMove() {
        int[] bestFirstMove = {0,2,6,8};
        return bestFirstMove[new Random().nextInt(bestFirstMove.length)];
    }

    private void aiMoveMedium() {

        for (int[] combo : winningCombos) {
            if (canWin(combo, aiSymbol)) {
                for (int index : combo) {
                    if (board[index].isEmpty()) {
                        board[index] = aiSymbol;
                        ((TextView) boardGrid.getChildAt(index)).setText(aiSymbol);
                        return;
                    }
                }
            }
        }
        for (int[] combo : winningCombos) {
            if (canWin(combo, humanSymbol)) {
                for (int index : combo) {
                    if (board[index].isEmpty()) {
                        board[index] = aiSymbol;
                        ((TextView) boardGrid.getChildAt(index)).setText(aiSymbol);
                        return;
                    }
                }
            }
        }

        aiMoveEasy();
    }

    private void aiMoveEasy() {
        List<Integer> emptyCells = new ArrayList<>();

        for (int i = 0; i < board.length; i++){
            if (board[i].isEmpty()){
                emptyCells.add(i);
            }
        }

        Random random = new Random();
        int move = emptyCells.get(random.nextInt(emptyCells.size()));
        board[move] = aiSymbol;
        ((TextView) boardGrid.getChildAt(move)).setText(aiSymbol);
    }

    private boolean canWin(@NonNull int[] combo, String player){
        int count = 0;
        int emptyIndex =-1;
        for (int index : combo){
            if (board[index].equals(player)){
                count++;
            }else if (board[index].isEmpty()){
                emptyIndex = index;
            }
        }
        return count==2&&emptyIndex!=-1;
    }

    private Move minimax(String[] board, String player,int alpha,int beta) {
        if (checkWin(humanSymbol)) {
            Move move = new Move();
            move.score = -10;
            return move;
        } else if (checkWin(aiSymbol)) {
            Move move = new Move();
            move.score = 10;
            return move;
        } else if (isBoardFull()) {
            Move move = new Move();
            move.score = 0;
            return move;
        }
        List<Move> availableMoves = new ArrayList<>();
        // Loop through empty cells and simulate moves
        for (int i = 0; i < board.length; i++) {
            if (board[i].isEmpty()) {
                Move move = new Move();
                move.index = i;
                // Simulate move
                board[i] = player;
                // Recursively call minimax for the opponent
                if (player.equals(aiSymbol)) {
                    move.score = minimax(board, humanSymbol,alpha,beta).score;
                    alpha=Math.max(alpha,move.score);
                } else {
                    move.score = minimax(board, aiSymbol,alpha,beta).score;
                    beta=Math.min(beta,move.score);
                }
                // Undo move
                board[i] = "";
                // Store the move
                availableMoves.add(move);
                //Alpha- beta pruning
                if (beta<=alpha)break;
            }
        }
        return getBestMove(player, availableMoves);
    }

    private @NonNull Move getBestMove(@NonNull String player, List<Move> availableMoves) {
        Move bestMove = new Move();
        if (player.equals(aiSymbol)) {
            int bestScore = Integer.MIN_VALUE;
            for (Move move : availableMoves) {
                if (move.score > bestScore) {
                    bestScore = move.score;
                    bestMove = move;
                }
            }
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (Move move : availableMoves) {
                if (move.score < bestScore) {
                    bestScore = move.score;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }


    private void setLines(int combo) {
        if (combo >= 0 && combo < winningCombos.length) {
            for (int i = 0; i < 9; i++) {
                boardGrid.getChildAt(i).setBackground(ContextCompat.getDrawable(this, R.color.gray));
            }
            for (int cellIndex : winningCombos[combo]) {
                boardGrid.getChildAt(cellIndex).setBackground(ContextCompat.getDrawable(this, R.drawable.winning_button_color));
            }
        } else {
            for (int i = 0; i < 9; i++) {
                boardGrid.getChildAt(i).setBackground(ContextCompat.getDrawable(this, R.drawable.board_button_color));
            }
        }
    }

    private void aiFirstAlert() {
        firstMove.setVisibility(View.VISIBLE);
        firstMove.setOnCheckedChangeListener((rg,id)->{
            if (id==2){
                currentPlayer=aiSymbol;
                aiMove();
                firstMove.setVisibility(View.GONE);
            } else if (id==1) {
                currentPlayer=humanSymbol;
                firstMove.setVisibility(View.GONE);
            }else {
                Random random = new Random();
                int i = random.nextInt(2);
                if (i==1){
                    currentPlayer=aiSymbol;
                    aiMove();
                }else {
                    currentPlayer=humanSymbol;
                }
                firstMove.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the game state (board, currentPlayer, scores, etc.)
        outState.putStringArray("board", board);
        outState.putString("currentPlayer", currentPlayer);
        outState.putInt("player1Score", player1Score);
        outState.putInt("player2Score", player2Score);
        outState.putString("gameMode", gameMode);
        outState.putString("level", level);
        outState.putString("humanSymbol", humanSymbol);
        outState.putString("aiSymbol", aiSymbol);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the game state
        board = savedInstanceState.getStringArray("board");
        currentPlayer = savedInstanceState.getString("currentPlayer");
        player1Score = savedInstanceState.getInt("player1Score");
        player2Score = savedInstanceState.getInt("player2Score");
        gameMode = savedInstanceState.getString("gameMode");
        level = savedInstanceState.getString("level");
        humanSymbol = savedInstanceState.getString("humanSymbol");
        aiSymbol = savedInstanceState.getString("aiSymbol");

        // Update the UI (repopulate the board and score display)
        for (int i = 0; i < board.length; i++) {
            ((TextView) boardGrid.getChildAt(i)).setText(board[i]);
        }
        updateScoreDisplay();
    }
}