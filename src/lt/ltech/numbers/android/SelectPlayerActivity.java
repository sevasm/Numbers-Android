package lt.ltech.numbers.android;

import java.util.List;

import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.persistence.PlayerDao;
import lt.ltech.numbers.persistence.mapping.PlayerMapper;
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
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this,
                R.layout.player_view);
        this.playerList.setAdapter(adapter);
        this.players = new PlayerDao(this).findAll(new PlayerMapper());
        for (Player player: this.players) {
            adapter.add(player);
        }
        this.playerList.setOnItemClickListener(this.getOnItemClickListener());
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
                    Player p = new Player(name);
                    PlayerMapper pm = new PlayerMapper();
                    Long id = dao.insert(p, pm);
                    p = dao.findById(id, pm);
                    logger.d("Created %s", p);
                    Intent result = new Intent();
                    result.putExtra("player", p);
                    activity.setResult(RESULT_OK, result);
                    activity.finish();
                }
            }
        };
    }
}
