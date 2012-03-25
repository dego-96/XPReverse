package jp.dego.xpreverse;

import java.util.ArrayList;
import java.util.List;

import jp.dego.xpreverse.highscores.DBHelper;
import jp.dego.xpreverse.highscores.HighScoreListItem;
import jp.dego.xpreverse.highscores.HighScores;
import jp.dego.xpreverse.highscores.HighScoresAdapter;
import android.app.TabActivity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class HighScoreActivity extends TabActivity implements TabHost.TabContentFactory
{
    private HighScores mHighScores = new HighScores(); // ハイスコア
    private DBHelper mDBHelper = new DBHelper(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.mHighScores = new HighScores(); // ハイスコアデータのインスタンスを作成
        
        // データベースの読み込み
        try {
            MainActivity.Database = mDBHelper.getWritableDatabase();
            mHighScores.LoadFromDatabase(MainActivity.Database);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        mDBHelper.close();
        
        // タブ画面の作成
        createTabDisplay();
    }
    
    private void createTabDisplay()
    {
        // TabHostインスタンスを取得
        final TabHost tabHost = getTabHost();
        
        // 1つ目のタブを生成
        TabSpec tabX = tabHost.newTabSpec("x");
        tabX.setIndicator("x Mode", getResources().getDrawable(R.drawable.tabx));
        // 2つ目のタブを生成
        TabSpec tabP = tabHost.newTabSpec("p");
        tabP.setIndicator("+ Mode", getResources().getDrawable(R.drawable.tabp));
        
        // 選択時に表示する内容
        tabX.setContent(this);
        tabP.setContent(this);
        
        tabHost.addTab(tabX);
        tabHost.addTab(tabP);
    }
    
    //
    // タブの中身のレイアウトを記述
    //
    @Override
    public View createTabContent(String tag)
    {
        // ListViewを作成
        ListView lv = new ListView(this);
        lv.setEnabled(false); // 表示するだけにする
        
        // ListViewに表示するデータリスト
        List<HighScoreListItem> list = new ArrayList<HighScoreListItem>();
        // 1行に表示させるデータ
        HighScoreListItem item;
        
        // ハイスコアのデータを設定
        for (int i = 0; i < 4; i++) {
            item = new HighScoreListItem();
            
            item.mStageNum = "Stage " + (i + 1); // ステージ番号
            
            if (0 < mHighScores.getTime(tag, i))
                item.mScoreTime = mHighScores.getTimeString(tag, i);
            else
                item.mScoreTime = "Not Charanged";
            if (mHighScores.getDate(tag, i) != null)
                item.mScoreDate = mHighScores.getDateString(tag, i);
            else
                item.mScoreDate = "";
            
            list.add(item);
        }
        
        // ListViewにアダプタをセット
        HighScoresAdapter adapter = new HighScoresAdapter(this, R.layout.score, list);
        lv.setAdapter(adapter);
        
        return lv;
    }
    
    //
    // Menuの作成
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, Menu.FIRST, 0, getString(R.string.Menu_ScoreClear));
        return super.onCreateOptionsMenu(menu);
    }
    
    //
    // Menuの中身を処理を作成
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
        case Menu.FIRST:
            // ハイスコアを全て削除する(データベースのテーブルを削除)
            try {
                MainActivity.Database = mDBHelper.getWritableDatabase();
                MainActivity.Database.delete(DBHelper.DB_TABLE_NAME, null, null);
                String text = getString(R.string.Toast_RemoveTable);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            mDBHelper.close();
            // Toastの表示
            Toast.makeText(this, getString(R.string.Toast_RemoveTable), Toast.LENGTH_SHORT).show();
            break;
        
        }
        return true;
    }
}
