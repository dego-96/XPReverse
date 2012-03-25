package jp.dego.xpreverse;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

// アプリ起動時に最初に呼び出されるActivity
// モードを選択する画面
public class MainActivity extends Activity
{
    // ハイスコア保存用データベース
    public static SQLiteDatabase Database;
    
    //
    // Activityのライフサイクル
    //
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    //
    // Button クリック時に呼び出される関数
    //
    public void onXButtonClicked(View view)
    {
        Intent intent = new Intent(this, CoinsActivity.class);
        intent.putExtra("MODE_ID", CoinsActivity.MODE_ID1);
        startActivity(intent);
    }
    
    public void onPButtonClicked(View view)
    {
        Intent intent = new Intent(this, CoinsActivity.class);
        intent.putExtra("MODE_ID", CoinsActivity.MODE_ID2);
        startActivity(intent);
    }
    
    //
    // Menuの作成
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, Menu.FIRST, 0, getString(R.string.Menu_HighScore));
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
            Intent intent = new Intent(this, HighScoreActivity.class);
            startActivity(intent);
            break;
        }
        return true;
    }
}
