package jp.dego.xpreverse;

import java.util.Random;

import jp.dego.xpreverse.coin.Coin;
import jp.dego.xpreverse.highscores.DBHelper;
import jp.dego.xpreverse.highscores.HighScores;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CoinsActivity extends Activity
{
    // コインの回転モードID
    public static final int MODE_ID1 = 1;
    public static final int MODE_ID2 = 2;
    
    // LayoutParams
    private LayoutParams LP_WW = new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT);
    
    // ローカル変数
    private Coin[][] Coins;
    private int stage_num = 1; // ステージID (1列のコインの数 - 4)
    private Chronometer mChronometer; // 表示用
    private long StartTime; // 測定用
    private Button StartButton; // スタートボタン
    private boolean isStarted; // ゲーム開始状態
    private boolean firstSelect; // 最初のステージ選択判定用
    private int CURRENT_MODE = 0; // 現在の回転モード
    
    // データベース
    // private static SQLiteDatabase mDatabase;
    private DBHelper mDBHelper = new DBHelper(this);
    
    //
    // Activityのライフサイクル
    //
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.coins);
        
        // 回転モードの取得
        Intent intent = getIntent();
        this.CURRENT_MODE = intent.getIntExtra("MODE_ID", 0);
        
        this.firstSelect = true;
        
        // ステージ選択ダイアログの表示
        AlertDialog.Builder StageDialog = getStageSelectDialog();
        StageDialog.show();
    }
    
    //
    // 画面の作成
    //
    private void CreateDisplay()
    {
        setContentView(R.layout.coins);
        this.firstSelect = false; // 初回のステージ選択完了
        
        // まだゲームは開始しない
        this.isStarted = false;
        // Chronometerの取得
        this.mChronometer = (Chronometer)findViewById(R.id.Chronometer);
        // スタートボタンの取得
        this.StartButton = (Button)findViewById(R.id.Button_StartButton);
        
        int padding = Coin.Padding; // コインのすき間
        int num = this.stage_num + 4;
        
        // 画面サイズの取得
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int size = display.getWidth() / num - 2 * padding;
        
        TableLayout tl = getCoinsArray(num, size);
        LinearLayout ll = (LinearLayout)findViewById(R.id.Layout_CoinsArray);
        ll.addView(tl, LP_WW);
    }
    
    //
    // コイン配列レイアウトの取得
    //
    private TableLayout getCoinsArray(int num, int size)
    {
        // 宣言
        Coins = new Coin[num][num];
        TableLayout tl = new TableLayout(this);
        TableRow[] tr = new TableRow[num];
        
        for (int j = 0; j < num; j++) {
            tr[j] = new TableRow(this);
            for (int i = 0; i < num; i++) {
                // インスタンスの生成
                Coins[j][i] = new Coin(this);
                Coins[j][i].LayoutInit(size);
                // 画像イメージの貼り付け
                Coins[j][i].setDrawable(CURRENT_MODE);
                
                final int x = i;
                final int y = j;
                Coins[j][i].setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Rotate(x, y);
                    }
                });
                
                // TableLayoutに追加
                tr[j].addView(Coins[j][i], size + 2 * Coin.Padding, size + 2 * Coin.Padding);
            }
            tl.addView(tr[j], LP_WW); // TableLayoutに追加
        }
        return tl;
    }
    
    //
    // ステージ選択ダイアログの作成
    //
    private AlertDialog.Builder getStageSelectDialog()
    {
        String[] items = { "Stage 1 ( 4 x 4 )", "Stage 2 ( 5 x 5 )", "Stage 3 ( 6 x 6 )",
                "Stage 4 ( 7 x 7 )" };
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.DialogTitle_StageSelect));
        dialog.setSingleChoiceItems(items, stage_num, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                stage_num = which;
                CreateDisplay();
                dialog.dismiss();
            }
        });
        // キャンセルした場合
        dialog.setOnCancelListener(new OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if (firstSelect)
                    finish();
            }
        });
        
        return dialog;
    }
    
    //
    // Menuの作成
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, Menu.FIRST, 0, getString(R.string.Menu_StageSelect));
        menu.add(0, Menu.FIRST + 1, 0, getString(R.string.Menu_HighScore));
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
            // ステージ選択ダイアログの表示
            AlertDialog.Builder StageDialog = getStageSelectDialog();
            StageDialog.show();
            break;
        case Menu.FIRST + 1:
            Intent intent = new Intent(this, HighScoreActivity.class);
            startActivity(intent);
            break;
        }
        return true;
    }
    
    //
    // スタートボタンクリック時の処理
    //
    public void onStartButtonClicked(View view)
    {
        setRandomState();
        this.isStarted = true; // ゲーム開始
        this.StartButton.setEnabled(false);
        this.mChronometer.setBase(SystemClock.elapsedRealtime()); // リセット
        this.mChronometer.start(); // 表示用タイマをスタート
        this.StartTime = System.currentTimeMillis();
    }
    
    //
    // 初期状態をランダムで決定する
    //
    private void setRandomState()
    {
        final int num = this.stage_num + 4;
        final int rep = 99;
        Random rand = new Random();
        int x, y;
        for (int i = 0; i < rep; i++) {
            x = rand.nextInt(num);
            y = rand.nextInt(num);
            Rotate(x, y);
        }
        if (Judge()) {
            setRandomState();
        }
    }
    
    //
    // コイン回転処理
    //
    private void Rotate(int x, int y)
    {
        this.Coins[y][x].applyRotation();
        if (CURRENT_MODE == MODE_ID1) {
            /** x mode **/
            // 右下方向
            for (int n = 1; x + n < stage_num + 4 && y + n < stage_num + 4; n++)
                this.Coins[y + n][x + n].applyRotation();
            // 左上方向
            for (int n = 1; x - n > -1 && y - n > -1; n++)
                this.Coins[y - n][x - n].applyRotation();
            // 右上方向
            for (int n = 1; x + n < stage_num + 4 && y - n > -1; n++)
                this.Coins[y - n][x + n].applyRotation();
            // 左下方向
            for (int n = 1; x - n > -1 && y + n < stage_num + 4; n++)
                this.Coins[y + n][x - n].applyRotation();
        } else if (CURRENT_MODE == MODE_ID2) {
            /** + mode **/
            if (y > 0)
                this.Coins[y - 1][x].applyRotation();
            if (x > 0)
                this.Coins[y][x - 1].applyRotation();
            if (y < stage_num + 4 - 1)
                this.Coins[y + 1][x].applyRotation();
            if (x < stage_num + 4 - 1)
                this.Coins[y][x + 1].applyRotation();
        }
        // ゲームが開始していればそろっているかチェック
        if (this.isStarted && Judge()) {
            // タイマを停止
            this.mChronometer.stop();
            // クリアタイム
            long EndTime = System.currentTimeMillis();
            long ClearTime = EndTime - this.StartTime;
            String TimeText = HighScores.getScoreText(ClearTime);
            
            String dialog_text = "クリアタイム\n" + TimeText;
            //
            // ハイスコア登録処理
            //
            try {
                MainActivity.Database = mDBHelper.getWritableDatabase(); // データベースの取得
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            int stage_id = stage_num + (CURRENT_MODE - 1) * 4;
            long old = DBHelper.selectHighScores(MainActivity.Database, stage_id);
            DBHelper.insertScore(MainActivity.Database, stage_id, ClearTime, EndTime);
            if (ClearTime < old) {
                // ハイスコア更新
                dialog_text += "\nハイスコア更新!!";
            }
            mDBHelper.close();
            
            // ダイアログの表示
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear!!");
            builder.setMessage(dialog_text);
            builder.setNeutralButton("OK", null);
            builder.show();
            
            // スタートボタンを回復
            this.StartButton.setEnabled(true);
            // ゲーム状態を終了
            this.isStarted = false;
        }
    }
    
    //
    // Judge関数
    // 全てのコインがそろっているとtrueを返す
    //
    private boolean Judge()
    {
        int num = this.stage_num + 4;
        // コインの状態を取得
        boolean[] state = new boolean[num * num];
        for (int j = 0; j < num; j++) {
            for (int i = 0; i < num; i++) {
                state[j * num + i] = Coins[j][i].isFront();
            }
        }
        // そろっているかを判定
        for (int i = 1; i < num * num; i++) {
            if (state[i - 1] != state[i])
                return false;
        }
        return true;
    }
}
