package lt.ltech.numbers.android;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import lt.ltech.numbers.GameException;
import lt.ltech.numbers.android.entity.Stats;
import lt.ltech.numbers.android.game.GameType;
import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.android.persistence.StatsDao;
import lt.ltech.numbers.android.persistence.mapping.StatsMapper;
import lt.ltech.numbers.game.Answer;
import lt.ltech.numbers.game.GameConfiguration;
import lt.ltech.numbers.game.GameState;
import lt.ltech.numbers.game.GameStep;
import lt.ltech.numbers.game.Number;
import lt.ltech.numbers.game.Round;
import lt.ltech.numbers.player.ArtificialPlayer;
import lt.ltech.numbers.player.DefaultPlayer;
import lt.ltech.numbers.player.Player;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class GameActivity extends Activity {
    public static final String PLAYER = "player";
    public static final String GAME_TYPE = "gameType";
    private static final String GAME_STATE = "gameState";
    private static final String HUMAN_PLAYER = "humanPlayer";
    private static final String COMPUTER_PLAYER = "computerPlayer";
    private static final String ARTIFICIAL_PLAYERS = "artificialPlayers";
    private static final String GUESS_TEXT = "guessText";

    private static final Logger logger = new Logger(
            GameActivity.class.getName());

    private ScrollView scrollView;
    private LinearLayout playerColumns;
    private LinearLayout columnLeft;
    private LinearLayout columnRight;
    private LinearLayout lowerButtonLayout;
    private LinearLayout upperButtonLayout;
    private Button guessButton;
    private Button[] buttons;
    private Button clearButton;
    private EditText guessText;
    private TextView hintText;

    private GameState gameState;
    private GameType gameType;

    private Player humanPlayer;
    private Player computerPlayer;
    private HashMap<Player, ArtificialPlayer> artificialPlayers;

    private LinearLayout numbers;
    private TextView numberLeft;
    private TextView numberRight;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(GAME_STATE, gameState);
        outState.putSerializable(GAME_TYPE, gameType);
        outState.putSerializable(HUMAN_PLAYER, humanPlayer);
        outState.putSerializable(COMPUTER_PLAYER, computerPlayer);
        outState.putSerializable(ARTIFICIAL_PLAYERS, artificialPlayers);
        outState.putString(GUESS_TEXT, guessText.getText().toString());
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.game);
        guessButton = (Button) findViewById(R.id.gameGuessButton);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        playerColumns = (LinearLayout) findViewById(R.id.playerColumns);

        LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, 1.0f);

        columnLeft = (LinearLayout) inf.inflate(R.layout.player_column, null);
        columnLeft.setLayoutParams(params);
        playerColumns.addView(columnLeft);

        columnRight = (LinearLayout) inf.inflate(R.layout.player_column, null);
        if (!isPractice()) {
            columnRight.setLayoutParams(params);
            playerColumns.addView(columnRight);
        }

        hintText = (TextView) findViewById(R.id.hintText);

        guessText = (EditText) findViewById(R.id.guessText);
        guessText.setEnabled(false);
        guessText.setClickable(false);
        guessText.setFocusable(false);

        guessButton.setOnClickListener(getOnClickListener());

        upperButtonLayout = (LinearLayout) findViewById(R.id.gameButtonLayoutUpper);
        lowerButtonLayout = (LinearLayout) findViewById(R.id.gameButtonLayoutLower);

        buttons = new Button[10];
        createButtons(upperButtonLayout, 0, 4);
        createButtons(lowerButtonLayout, 5, 9);

        clearButton = (Button) findViewById(R.id.gameClearButton);
        clearButton.setOnClickListener(getClearOnClickListener());

        numbers = (LinearLayout) findViewById(R.id.gameNumbers);
        numberLeft = (TextView) findViewById(R.id.gameNumberLeft);
        numberRight = (TextView) findViewById(R.id.gameNumberRight);

        if (savedState != null) {
            gameState = (GameState) savedState.getSerializable(GAME_STATE);
            gameType = (GameType) savedState.getSerializable(GAME_TYPE);
            humanPlayer = (Player) savedState.getSerializable(HUMAN_PLAYER);
            computerPlayer = (Player) savedState
                    .getSerializable(COMPUTER_PLAYER);
            artificialPlayers = (HashMap<Player, ArtificialPlayer>) savedState
                    .getSerializable(ARTIFICIAL_PLAYERS);
            boolean gsnn = gameState != null;
            boolean gtnn = gameType != null;
            boolean hpnn = humanPlayer != null;
            boolean cpnn = computerPlayer != null;
            boolean apnn = artificialPlayers != null;
            if (gsnn && gtnn && hpnn && cpnn && apnn) {
                boolean cpap = artificialPlayers.get(computerPlayer) != null;
                boolean hpgs = gameState.containsPlayer(humanPlayer);
                boolean cpgs = gameState.containsPlayer(computerPlayer);
                if (cpap && hpgs && cpgs) {
                    resumeGame();
                } else {
                    startNewGame();
                }
            } else {
                startNewGame();
            }

            String guess = savedState.getString(GUESS_TEXT);
            if (guess != null) {
                guessText.setText(guess);
                resetButtonState();
            }
        } else {
            startNewGame();
        }

        TextView humanPlayerName = (TextView) findViewById(R.id.gameNameLeft);
        humanPlayerName.setText(humanPlayer.getName());
        TextView computerPlayerName = (TextView) findViewById(R.id.gameNameRight);
        computerPlayerName.setText(computerPlayer.getName());
    }

    private void startNewGame() {
        gameState = new GameState();

        Bundle extras = getIntent().getExtras();
        gameType = (GameType) extras.getSerializable(GAME_TYPE);
        humanPlayer = (Player) extras.getSerializable(PLAYER);

        computerPlayer = new Player(UUID.randomUUID(), "Computer player");
        computerPlayer.setId(2l);

        artificialPlayers = new HashMap<Player, ArtificialPlayer>();
        ArtificialPlayer artificialPlayer = new DefaultPlayer(computerPlayer);
        artificialPlayers.put(computerPlayer, artificialPlayer);

        try {
            gameState.addPlayer(humanPlayer);
            gameState.addPlayer(computerPlayer);

            if (isPractice()) {
                Integer[] digits = { 0, 1, 2, 3 };
                Number number = new Number(Arrays.asList(digits));
                gameState.setNumber(humanPlayer, number);
                guessButton.setText(getString(R.string.game_make_guess));
                callArtifialPlayers();
            }
        } catch (GameException ge) {
            logger.e(ge.getMessage());
        }

        this.updateHint();
    }

    private void resumeGame() {
        if (humanPlayer.getNumber() != null) {
            if (!isPractice()) {
                numberLeft.setText(humanPlayer.getNumber().toString());
            } else {
                numberLeft.setText("");
            }
            guessButton.setText(getString(R.string.game_make_guess));

            if (computerPlayer.getNumber() != null) {
                Number number = computerPlayer.getNumber();
                numberRight.setText(maskNumber(number));
            } else {
                callArtifialPlayers();
            }

            for (Round round: gameState.getRounds()) {
                for (Player player: round.getAnswers().keySet()) {
                    Number guess = round.getGuesses().get(player);
                    Answer answer = round.getAnswers().get(player);
                    if (player.equals(humanPlayer)) {
                        addGuessAndAnswer(columnLeft, guess, answer);
                    } else if (player.equals(computerPlayer)) {
                        addComputerGuessAndAnswer(guess, answer);
                    }
                }
            }
        } else {
            if (isPractice()) {
                try {
                    Integer[] digits = new Integer[] { 0, 1, 2, 3 };
                    Number number = new Number(Arrays.asList(digits));
                    gameState.setNumber(this.humanPlayer, number);
                } catch (GameException e) {
                    logger.e(e.getMessage());
                }
            }
        }

        this.updateHint();
    }

    private OnClickListener getOnClickListener() {
        final GameActivity a = this;
        final LinearLayout gl = columnLeft;
        final EditText gt = guessText;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = gt.getText().toString();

                Number guess = null;
                try {
                    String[] splitLine = text.split("");
                    Integer[] guessArray = new Integer[splitLine.length - 1];
                    for (int i = 1; i < splitLine.length; i++) {
                        guessArray[i - 1] = Integer.valueOf(splitLine[i]);
                    }
                    guess = new Number(Arrays.asList(guessArray));
                } catch (NumberFormatException nfe) {
                } catch (GameException ge) {
                    logger.e(ge.getMessage());
                }
                if (guess == null) {
                    return;
                } else {
                    a.guessText.setText("");
                    a.resetButtonState();
                }

                LinearLayout ll = null;
                Answer answer = null;
                GameStep gs = a.gameState.getGameStep();
                final GameStep.Type type = gs.type();
                switch (type) {
                    case SET_NUMBER:
                        try {
                            a.gameState.setNumber(a.humanPlayer, guess);
                            logger.i("%s has chosen %s", a.humanPlayer,
                                    a.humanPlayer.getNumber());
                            a.numberLeft.setText(a.humanPlayer.getNumber()
                                    .toString());
                            a.guessButton.setText(a
                                    .getString(R.string.game_make_guess));
                        } catch (GameException ge) {
                            logger.e(ge.getMessage());
                        }
                        break;
                    case GUESS:
                        try {
                            ll = gl;
                            a.gameState.guess(a.humanPlayer, guess);
                            answer = a.gameState.getLastRound().getAnswers()
                                    .get(a.humanPlayer);
                        } catch (GameException ge) {
                            logger.e(ge.getMessage());
                        }
                        break;
                }

                if (ll != null) {
                    a.addGuessAndAnswer(ll, guess, answer);
                }

                a.callArtifialPlayers();

                if (a.isGameOver()) {
                    Player winner = a.gameState.getWinner();
                    logger.i("Winner: %s, Player: %s", winner, a.humanPlayer);
                    logger.i("HUman won: %b", a.humanPlayer.equals(winner));
                    boolean humanWon = a.humanPlayer.equals(winner);

                    updateStats(a.humanPlayer, humanWon, false);

                    AlertDialog.Builder b = new AlertDialog.Builder(a);
                    int turns = a.gameState.getRounds().size();
                    b.setTitle("Game Over");
                    String message;
                    if (humanWon) {
                        String s = getString(R.string.game_winner_player);
                        logger.i("Message: %s", s);
                        message = String.format(s, turns);
                    } else {
                        String s = getString(R.string.game_winner_computer);
                        logger.i("Message: %s", s);
                        message = String.format(s, turns, winner.getName());
                    }
                    b.setMessage(message);
                    b.setNeutralButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    a.finish();
                                }
                            });
                    b.show();
                }
            }
        };
    }

    private void updateStats(Player player, boolean isWinner, boolean isDraw) {
        StatsDao statsDao = new StatsDao(this);
        Stats stats = statsDao.findByPlayer(player);

        if (!isPractice()) {
            stats.setGamesPlayed(stats.getGamesPlayed() + 1);
            if (isWinner) {
                stats.setGamesWon(stats.getGamesWon() + 1);
            }
            if (isDraw) {
                stats.setGamesDrawn(stats.getGamesDrawn() + 1);
            }
        }

        if (isWinner || isDraw) {
            BigDecimal guessCount = new BigDecimal(gameState.getRounds().size());
            BigDecimal correctGuesses = new BigDecimal(
                    stats.getCorrectGuesses());
            BigDecimal averageGuesses = stats.getAverageGuesses();

            BigDecimal totalGuesses = averageGuesses.multiply(correctGuesses)
                    .add(guessCount);
            correctGuesses = correctGuesses.add(BigDecimal.ONE);
            MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
            averageGuesses = totalGuesses.divide(correctGuesses, mc).setScale(
                    2, RoundingMode.HALF_UP);

            stats.setCorrectGuesses(correctGuesses.toBigInteger().intValue());
            stats.setAverageGuesses(averageGuesses);
        }
        statsDao.update(stats, stats.getId(), new StatsMapper());
    }

    private void callArtifialPlayers() {
        Player p = null;
        ArtificialPlayer ap = null;
        while ((ap = this.artificialPlayers.get(p = gameState.getGameStep()
                .player())) != null) {
            final GameStep.Type t = gameState.getGameStep().type();
            switch (t) {
                case SET_NUMBER:
                    try {
                        Number number = ap.inventNumber();
                        logger.i("Computer invented %s", number.toString());
                        gameState.setNumber(p, number);
                        numberRight.setText(maskNumber(number));
                        updateHint();
                    } catch (GameException ge) {
                        logger.e(ge.getMessage());
                    }
                    break;
                case GUESS:
                    try {
                        Number g = null;
                        if (!isPractice()) {
                            g = ap.makeGuess(gameState);
                        } else {
                            Integer[] digits = { 9, 8, 7, 6 };
                            g = new Number(Arrays.asList(digits));
                        }
                        gameState.guess(p, g);
                        Answer ans = gameState.getLastRound().getAnswers()
                                .get(computerPlayer);
                        addComputerGuessAndAnswer(g, ans);
                    } catch (GameException ge) {
                        logger.e(ge.getMessage());
                    }
                    break;
            }
        }
    }

    private void addComputerGuessAndAnswer(Number guess, Answer answer) {
        if (!isPractice()) {
            addGuessAndAnswer(columnRight, guess, answer);
        }
    }

    private void addGuessAndAnswer(LinearLayout ll, Number guess, Answer answer) {
        TextView tv = new TextView(this);
        tv.setTextAppearance(this, R.style.GuessText);
        tv.setText(String.format("%s %s", guess, answer));
        ll.addView(tv);
        this.scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void createButtons(LinearLayout view, int start, int end) {
        for (int i = start; i <= end; i++) {
            Button b = new Button(this);
            b.setText(String.valueOf(i));
            b.setOnClickListener(getButtonOnClickListener(i));
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            view.addView(b, lp);
            buttons[i] = b;
        }
    }

    private OnClickListener getButtonOnClickListener(final int buttonNumber) {
        final GameActivity a = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = a.guessText.getText().toString();
                a.guessText.setText(text + buttonNumber);
                a.buttons[buttonNumber].setEnabled(false);
                a.resetButtonState();
            }
        };
    }

    private OnClickListener getClearOnClickListener() {
        final GameActivity a = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = a.guessText.getText().toString();
                if (text.length() > 0) {
                    a.guessText.setText(text.substring(0, text.length() - 1));
                }
                a.resetButtonState();
            }
        };
    }

    private void resetButtonState() {
        String text = this.guessText.getText().toString();
        if (text.length() >= GameConfiguration.numberLength()) {
            setButtonsEnabled(false);
            return;
        } else {
            setButtonsEnabled(true);
        }
        for (String s: text.split("")) {
            if (s.equals("")) {
                continue;
            }
            try {
                int n = Integer.parseInt(s);
                buttons[n].setEnabled(false);
            } catch (NumberFormatException e) {
                logger.d(e.getMessage());
                guessText.setText("");
                setButtonsEnabled(true);
            }
        }
    }

    private void updateHint() {
        GameStep.Type type = gameState.getGameStep().type();
        if (type == GameStep.Type.SET_NUMBER) {
            hintText.setText(R.string.hint_number);
        } else if (type == GameStep.Type.GUESS) {
            hintText.setText(R.string.hint_guess);
        }
    }

    private String maskNumber(Number number) {
        return number.toString().replaceAll(".", "?");
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button b: buttons) {
            b.setEnabled(enabled);
        }
    }

    private boolean isGameOver() {
        return gameState.isGameOver();
    }

    private boolean isPractice() {
        return gameType == GameType.PRACTICE;
    }
}
