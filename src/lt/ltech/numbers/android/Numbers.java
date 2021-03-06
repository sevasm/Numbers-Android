package lt.ltech.numbers.android;

import java.util.List;

import lt.ltech.numbers.android.game.GameType;
import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.android.persistence.PlayerDao;
import lt.ltech.numbers.android.persistence.mapping.PlayerMapper;
import lt.ltech.numbers.player.Player;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * The activity started when the game is launched. If no players are present on
 * the device the game is installed on, asks the player to
 * {@link SelectPlayerActivity create a new player}.
 * @author Severinas Monkevicius
 */
public class Numbers extends Activity {
    private static final Logger logger = new Logger(Numbers.class.getName());

    public static final int RC_SELECT_PLAYER = 1;

    private Player player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        List<Player> players = new PlayerDao(this).findAll(new PlayerMapper());
        if (players.size() >= 1) {
            player = players.get(0);
            setWelcomeMessage();
        } else {
            Intent i = new Intent(this, SelectPlayerActivity.class);
            startActivityForResult(i, RC_SELECT_PLAYER);
        }
        Button newGameButton = (Button) findViewById(R.id.menuNewGameButton);
        newGameButton.setOnClickListener(getNewGameListener());
        Button practiceButton = (Button) findViewById(R.id.menuPracticeButton);
        practiceButton.setOnClickListener(getPracticeListener());
        Button statisticsButton = (Button) findViewById(R.id.menuStatisticsButton);
        statisticsButton.setOnClickListener(getStatisticsListener());
        Button changePlayerButton = (Button) findViewById(R.id.menuChangePlayerButton);
        changePlayerButton.setOnClickListener(getSelectPlayerListener());
    }

    private OnClickListener getNewGameListener() {
        final Numbers context = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, GameActivity.class);
                i.putExtra(GameActivity.PLAYER, context.player);
                i.putExtra(GameActivity.GAME_TYPE, GameType.VS_COMPUTER);
                context.startActivity(i);
            }
        };
    }

    private OnClickListener getPracticeListener() {
        final Numbers context = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, GameActivity.class);
                i.putExtra(GameActivity.PLAYER, context.player);
                i.putExtra(GameActivity.GAME_TYPE, GameType.PRACTICE);
                context.startActivity(i);
            }
        };
    }

    private OnClickListener getStatisticsListener() {
        final Numbers context = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StatisticsActivity.class);
                i.putExtra(StatisticsActivity.PLAYER, context.player);
                context.startActivity(i);
            }
        };
    }

    private OnClickListener getSelectPlayerListener() {
        final Numbers context = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SelectPlayerActivity.class);
                context.startActivityForResult(i, RC_SELECT_PLAYER);
            }
        };
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_PLAYER || resultCode == RC_SELECT_PLAYER) {
            if (resultCode == RESULT_OK) {
                this.player = (Player) data.getSerializableExtra("player");
                this.setWelcomeMessage();
            }
        }
    }

    private void setWelcomeMessage() {
        TextView tv = (TextView) findViewById(R.id.menuWelcomeText);
        tv.setText(String.format(getString(R.string.menu_welcome),
                player.getName()));
        TextView hint = (TextView) findViewById(R.id.menuWelcomeHintText);
        hint.setText(String.format(getString(R.string.menu_welcome_hint),
                player.getName()));
    }
}