package jp.dego.xpreverse.highscores;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HighScores
{
    // メンバ変数
    private long[][] ScoreTimes;
    private Date[][] ScoreDate;
    
    public HighScores()
    {
        this.ScoreTimes = new long[2][4];
        this.ScoreDate = new Date[2][4];
        for (int i = 0; i < 4; i++) {
            this.ScoreTimes[0][i] = -1L;
            this.ScoreTimes[1][i] = -1L;
            this.ScoreDate[0][i] = null;
            this.ScoreDate[1][i] = null;
        }
    }
    
    //
    // データベースから全てのスコアを取得
    //
    public void LoadFromDatabase(SQLiteDatabase db)
    {
        String[] columns = DBHelper.DB_COLUMNS;
        long hs = -1; // HighScore
        long date;
        
        // 全ステージでハイスコアを取得
        for (int i = 0; i < 8; i++) {
            String param = DBHelper.StageID(i); // ステージIDを取得
            String sql = "SELECT " + columns[1] + ", " + columns[2] + " FROM "
                    + DBHelper.DB_TABLE_NAME + " WHERE " + DBHelper.DB_COLUMNS[0] + "='" + param
                    + "';";
            Cursor c = db.rawQuery(sql, null); // クエリの発行
            // ハイスコアを検索
            if (c.moveToFirst()) {
                hs = c.getLong(0);
                date = c.getLong(1);
                long score;
                while (c.moveToNext()) {
                    score = c.getLong(0);
                    if (hs > score) {
                        hs = score;
                        date = c.getLong(1);
                    }
                }
                this.ScoreTimes[i / 4][i % 4] = hs;
                this.ScoreDate[i / 4][i % 4] = new Date(date);
            } else {
                this.ScoreTimes[i / 4][i % 4] = -1;
                this.ScoreDate[i / 4][i % 4] = null;
            }
        }
    }
    
    //
    // ハイスコアの取得
    //
    public long getTime(String mode, int stage)
    {
        long res = -1;
        
        // 範囲例外処理
        if (stage < 0 || 3 < stage)
            return res;
        
        if ("x".equals(mode))
            res = this.ScoreTimes[0][stage];
        else if ("p".equals(mode))
            res = this.ScoreTimes[1][stage];
        
        return res;
    }
    
    //
    // String型でハイスコアを取得
    //
    public String getTimeString(String mode, int stage)
    {
        String res = "";
        long score_time = getTime(mode, stage);
        long min = score_time / (60 * 1000);
        long sec = (score_time / 1000) % 60;
        long ms = score_time % 1000 / 10;
        res = String.format("%02d分 %02d秒 %02d", min, sec, ms);
        
        return res;
    }
    
    //
    // スコアの日付を取得
    //
    public Date getDate(String mode, int stage_num)
    {
        Date res = null;
        
        // 範囲例外処理
        if (stage_num < 0 || 3 < stage_num)
            return res;
        
        if ("x".equals(mode)) {
            res = this.ScoreDate[0][stage_num];
        } else if ("p".equals(mode)) {
            res = this.ScoreDate[1][stage_num];
        }
        return res;
    }
    
    //
    // String型でスコアの日付を取得
    //
    public String getDateString(String mode, int stage_num)
    {
        Date date = getDate(mode, stage_num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        
        return sdf.format(date);
    }
    
    //
    // スコアをセット
    //
    public void setScore(String mode, int stage, long score, long date)
    {
        if ("x".equals(mode)) {
            this.ScoreTimes[0][stage] = score;
            this.ScoreDate[0][stage] = new Date(date);
        } else if ("p".equals(mode)) {
            this.ScoreTimes[1][stage] = score;
            this.ScoreDate[1][stage] = new Date(date);
        }
    }
    
    //
    // String型でスコアを取得
    //
    public static String getScoreText(long score_time)
    {
        String res = "";
        long min = score_time / (60 * 1000);
        long sec = (score_time / 1000) % 60;
        long ms = score_time % 1000 / 10;
        res = String.format("%02d分 %02d秒 %2d", min, sec, ms);
        return res;
    }
}
