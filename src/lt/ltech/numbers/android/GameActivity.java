package lt.ltech.numbers.android;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lt.ltech.numbers.GameException;
import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.game.Answer;
import lt.ltech.numbers.game.GameConfiguration;
import lt.ltech.numbers.game.GameState;
import lt.ltech.numbers.game.GameStep;
import lt.ltech.numbers.game.Number;
import lt.ltech.numbers.player.ArtificialPlayer;
import lt.ltech.numbers.player.Player;
import lt.ltech.numbers.player.RandomPlayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    private static final Logger logger = new Logger(
            GameActivity.class.getName());

    private ScrollView scrollView;
    private LinearLayout guessListLeft;
    private LinearLayout guessListRight;
    private LinearLayout lowerButtonLayout;
    private LinearLayout upperButtonLayout;
    private Button guessButton;
    private Button[] buttonArray;
    private Button clearButton;
    private EditText guessText;

    private GameState gameState;

    private Player humanPlayer;
    private Player computerPlayer;
    private Map<Player, ArtificialPlayer> artificialPlayers;

    private TextView numberLeft;
    private TextView numberRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.game);
        this.guessButton = (Button) this.findViewById(R.id.gameGuessButton);

        this.scrollView = (ScrollView) this.findViewById(R.id.scrollView);
        this.guessListLeft = (LinearLayout) this
                .findViewById(R.id.guessListLeft);
        this.guessListRight = (LinearLayout) this
                .findViewById(R.id.guessListRight);
        this.guessText = (EditText) this.findViewById(R.id.guessText);
        this.guessText.setEnabled(false);
        this.guessText.setClickable(false);
        this.guessText.setFocusable(false);

        this.guessButton.setOnClickListener(this.getOnClickListener());

        this.upperButtonLayout = (LinearLayout) this
                .findViewById(R.id.gameButtonLayoutUpper);
        this.lowerButtonLayout = (LinearLayout) this
                .findViewById(R.id.gameButtonLayoutLower);

        this.buttonArray = new Button[10];
        for (int i = 0; i < 5; i++) {
            Button b = new Button(this);
            b.setText(String.valueOf(i));
            b.setOnClickListener(this.getButtonOnClickListener(i));
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            this.upperButtonLayout.addView(b, lp);
            this.buttonArray[i] = b;
        }

        for (int i = 5; i < 10; i++) {
            Button b = new Button(this);
            b.setText(String.valueOf(i));
            b.setOnClickListener(this.getButtonOnClickListener(i));
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            this.lowerButtonLayout.addView(b, lp);
            this.buttonArray[i] = b;
        }

        this.clearButton = (Button) this.findViewById(R.id.gameClearButton);
        this.clearButton.setOnClickListener(this.getClearOnClickListener());

        this.numberLeft = (TextView) this.findViewById(R.id.gameNumberLeft);
        this.numberRight = (TextView) this.findViewById(R.id.gameNumberRight);

        this.gameState = new GameState();

        this.humanPlayer = (Player) this.getIntent().getExtras()
                .getSerializable("player");
        ((TextView) this.findViewById(R.id.gameNameLeft))
                .setText(this.humanPlayer.getName());
        this.computerPlayer = new Player(UUID.randomUUID(), "Computer player");
        this.computerPlayer.setId(2l);
        ((TextView) this.findViewById(R.id.gameNameRight))
                .setText(this.computerPlayer.getName());
        this.artificialPlayers = new HashMap<Player, ArtificialPlayer>();
        this.artificialPlayers.put(this.computerPlayer, new RandomPlayer());

        try {
            this.gameState.addPlayer(humanPlayer);
            this.gameState.addPlayer(computerPlayer);
        } catch (GameException ge) {
            logger.d(ge.getMessage());
        }
    }

    private OnClickListener getOnClickListener() {
        final GameActivity a = this;
        final LinearLayout gl = this.guessListLeft;
        final EditText gt = this.guessText;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = gt.getText().toString();

                Number guess = null;
                try {
                    String[] splitLine = text.split("");
                    Byte[] guessArray = new Byte[splitLine.length - 1];
                    for (int i = 1; i < splitLine.length; i++) {
                        guessArray[i - 1] = Byte.valueOf(splitLine[i]);
                    }
                    guess = new Number(Arrays.asList(guessArray));
                } catch (NumberFormatException nfe) {
                    logger.d(nfe.getMessage());
                } catch (GameException ge) {
                    logger.d(ge.getMessage());
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
                            logger.d(ge.getMessage());
                        }
                        break;
                    case GUESS:
                        try {
                            ll = gl;
                            a.gameState.guess(a.humanPlayer, guess);
                            answer = a.gameState.getLastRound().getAnswers()
                                    .get(a.humanPlayer);
                        } catch (GameException ge) {
                            logger.d(ge.getMessage());
                        }
                        break;
                }

                if (ll != null) {
                    TextView tv = new TextView(a);
                    tv.setTextAppearance(a, R.style.GuessText);
                    tv.setText(String.format("%s %s", guess, answer));
                    ll.addView(tv);
                    a.scrollView.fullScroll(View.FOCUS_DOWN);
                    // TODO make the scroll view scroll all the way to the
                    // bottom
                }

                Player p = null;
                ArtificialPlayer ap = null;
                while ((ap = a.artificialPlayers.get(p = a.gameState
                        .getGameStep().player())) != null) {
                    final GameStep.Type t = a.gameState.getGameStep().type();
                    switch (t) {
                        case SET_NUMBER:
                            try {
                                a.gameState.setNumber(p, ap.inventNumber());
                                logger.i("%s has chosen %s", a.computerPlayer,
                                        a.computerPlayer.getNumber());
                                String secretNumber = "";
                                for (int i = 0; i < GameConfiguration
                                        .numberLength(); i++) {
                                    secretNumber += "?";
                                }
                                a.numberRight.setText(secretNumber);
                            } catch (GameException ge) {
                                logger.d(ge.getMessage());
                            }
                            break;
                        case GUESS:
                            try {
                                Number g = ap.makeGuess(a.gameState);
                                a.gameState.guess(p, g);
                                Answer ans = a.gameState.getLastRound()
                                        .getAnswers().get(a.computerPlayer);
                                TextView tv = new TextView(a);
                                tv.setTextAppearance(a, R.style.GuessText);
                                tv.setText(String.format("%s %s", g, ans));
                                a.guessListRight.addView(tv);
                                a.scrollView.fullScroll(View.FOCUS_DOWN);
                                // TODO make the scroll view scroll all the way
                                // to the bottom
                            } catch (GameException ge) {
                                logger.d(ge.getMessage());
                            }
                            break;
                    }
                }

                if (a.isGameOver()) {
                    logger.i("Game Over");
                    AlertDialog.Builder b = new AlertDialog.Builder(a);
                    Player winner = a.gameState.getWinner();
                    int turns = a.gameState.getRounds().size();
                    b.setTitle("Game Over");
                    b.setMessage(String.format(
                            "The game is over. %s has won in %d turns", winner,
                            turns));
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

    private OnClickListener getButtonOnClickListener(final int buttonNumber) {
        final GameActivity a = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = a.guessText.getText().toString();
                a.guessText.setText(text + buttonNumber);
                a.buttonArray[buttonNumber].setEnabled(false);
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
            this.setButtonsEnabled(false);
            return;
        } else {
            this.setButtonsEnabled(true);
        }
        for (String s: text.split("")) {
            if (s.equals("")) {
                continue;
            }
            try {
                int n = Integer.parseInt(s);
                this.buttonArray[n].setEnabled(false);
            } catch (NumberFormatException e) {
                logger.d(e.getMessage());
                this.guessText.setText("");
                setButtonsEnabled(true);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button b: this.buttonArray) {
            b.setEnabled(enabled);
        }
    }

    private boolean isGameOver() {
        return this.gameState.isGameOver();
    }
}
