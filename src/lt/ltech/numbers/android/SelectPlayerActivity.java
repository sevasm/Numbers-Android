package lt.ltech.numbers.android;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lt.ltech.numbers.android.entity.Stats;
import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.android.persistence.PlayerDao;
import lt.ltech.numbers.android.persistence.StatsDao;
import lt.ltech.numbers.android.persistence.mapping.PlayerMapper;
import lt.ltech.numbers.android.persistence.mapping.StatsMapper;
import lt.ltech.numbers.player.Player;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SelectPlayerActivity extends Activity {
    private static final Logger logger = new Logger(
            SelectPlayerActivity.class.getName());

    private TextView nameText;
    private ListView playerList;
    private List<Player> players;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.player);

        this.playerList = (ListView) this.findViewById(R.id.playerList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.player_view);
        this.playerList.setAdapter(adapter);
        this.playerList.setOnItemClickListener(this.getOnItemClickListener());

        this.players = new PlayerDao(this).findAll(new PlayerMapper());
        for (Player player: this.players) {
            adapter.add(player.getName());
        }

        this.nameText = (TextView) this.findViewById(R.id.playerName);

        Button b = (Button) this.findViewById(R.id.playerButton);
        b.setOnClickListener(this.getOnClickListener());
    }

    private OnItemClickListener getOnItemClickListener() {
        final SelectPlayerActivity activity = this;
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Player p = activity.players.get((int) id);
                logger.d("Selected %s", p);
                Intent result = new Intent();
                result.putExtra("player", p);
                activity.setResult(RESULT_OK, result);
                activity.finish();
            }
        };
    }

    private OnClickListener getOnClickListener() {
        final SelectPlayerActivity activity = this;
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = activity.nameText.getText().toString();
                if (name != null && !name.equals("")) {
                    PlayerDao dao = new PlayerDao(activity);
                    Player player = new Player(UUID.randomUUID(), name);
                    PlayerMapper pm = new PlayerMapper();
                    Long id = dao.insert(player, pm);
                    player = dao.findById(id, pm);
                    Stats stats = new Stats();
                    stats.setPlayerId(player.getId());
                    stats.setGamesPlayed(0);
                    stats.setGamesWon(0);
                    stats.setGamesDrawn(0);
                    stats.setCorrectGuesses(0);
                    stats.setAverageGuesses(BigDecimal.ZERO);
                    StatsDao statsDao = new StatsDao(activity);
                    statsDao.insert(stats, new StatsMapper());
                    logger.d("Created %s", player);
                    Intent result = new Intent();
                    result.putExtra("player", player);
                    activity.setResult(RESULT_OK, result);
                    activity.finish();
                }
            }
        };
    }
}
