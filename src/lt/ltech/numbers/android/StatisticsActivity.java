package lt.ltech.numbers.android;

import lt.ltech.numbers.android.entity.Stats;
import lt.ltech.numbers.android.persistence.StatsDao;
import lt.ltech.numbers.player.Player;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This activity displays a page with player statistics.
 * @author Severinas Monkevicius
 */
public class StatisticsActivity extends Activity {
    public static final String PLAYER = "player";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.statistics);

        Bundle bundle = getIntent().getExtras();
        Player player = (Player) bundle.getSerializable(PLAYER);

        String string = getString(R.string.stats_player);
        String text = String.format(string, player.getName());
        TextView playerText = (TextView) findViewById(R.id.statsPlayerText);
        playerText.setText(text);

        Stats stats = new StatsDao(this).findByPlayer(player);
        setStatistic(R.id.statsPlayed, stats.getGamesPlayed());
        setStatistic(R.id.statsWon, stats.getGamesWon());
        setStatistic(R.id.statsLost, stats.getGamesLost());
        setStatistic(R.id.statsCorrect, stats.getCorrectGuesses());
        setStatistic(R.id.statsAverage, stats.getAverageGuesses());
    }

    private void setStatistic(int viewId, Object statistic) {
        String stat = statistic != null ? statistic.toString() : "";
        TextView text = (TextView) findViewById(viewId);
        text.setText(stat);
    }
}
