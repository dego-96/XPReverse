package jp.dego.xpreverse.highscores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

// データベースの情報
// ファイル名 : scores.db
// テーブル名 : SCORES
// フィールド : stage_id | score_time | score_date
//
// Stage ID 対応番号
// 00 : x mode stage 1
// 01 : x mode stage 2
// 02 : x mode stage 3
// 03 : x mode stage 4
// 04 : p mode stage 1
// 05 : p mode stage 2
// 06 : p mode stage 3
// 07 : p mode stage 4

public class DBHelper extends SQLiteOpenHelper
{
    // ハイスコアのテーブル名
    public static final String DB_TABLE_NAME = "SCORES";
    // ハイスコアテーブルのフィールド名 (スコアタイム, スコアの日付)
    public static final String[] DB_COLUMNS = { "stage_id", "score_time", "score_date" };
    
    // テーブル作成時のSQL文
    public static final String DROP_TABLE = "drop table " + DB_TABLE_NAME + ";"; // テーブル削除時のSQL文
    public static final String EXIST_TABLE = "select count(*) from sqlite_master where type='table' and name='{"
            + DB_TABLE_NAME + "}'"; // テーブルの存在チェックのSQL文
    
    private static final String DB_NAME = "scores.db"; // データベースの名前
    private static final int DB_VERSION = 1; // データベースのバージョン
    
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    public DBHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, DB_NAME, factory, version);
    }
    
    //
    // onCreateメソッド
    // データベースが作成された時に呼ばれます
    // テーブルの作成などを行います
    //
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + DB_TABLE_NAME
                + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_COLUMNS[0] + " TEXT NOT NULL, "
                + DB_COLUMNS[1] + " INTEGER NOT NULL, " + DB_COLUMNS[2] + " );";
        db.execSQL(CREATE_TABLE);
    }
    
    //
    // onUpgradeメソッド
    // onUpgrade()メソッドはデータベースをバージョンアップした時に呼ばれます。
    // 現在のレコードを退避し、テーブルを再作成した後、退避したレコードを戻すなどの処理を行います。
    //
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // db.execSQL(DROP_TABLE);
        // onCreate(db);
    }
    
    //
    // データベースからスコアを取得
    //
    public static long selectHighScores(SQLiteDatabase db, int stage_id)
    {
        long res; // 出力用変数
        
        // クエリを発行
        String param = StageID(stage_id);
        String sql = "SELECT " + DB_COLUMNS[1] + " FROM " + DBHelper.DB_TABLE_NAME + " WHERE "
                + DBHelper.DB_COLUMNS[0] + "='" + param + "';";
        Cursor c = db.rawQuery(sql, null);
        
        // 保存してあるのスコアを取得
        if (c.moveToFirst()) {
            res = c.getLong(0);
            long score;
            while (c.moveToNext()) {
                score = c.getLong(0);
                if (score < res)
                    res = score;
            }
        } else {
            res = -1;
        }
        return res;
    }
    
    //
    // データベースにスコアを登録
    //
    public static boolean insertScore(SQLiteDatabase db, int stage_id, long score, long date)
    {
        ContentValues values = new ContentValues(); // 登録用データの作成
        String[] columns = DBHelper.DB_COLUMNS; // フィールド名を取得
        values.put(columns[0], StageID(stage_id)); // ステージID
        values.put(columns[1], score); // クリアタイム
        values.put(columns[2], date); // クリア日時
        db.insert(DBHelper.DB_TABLE_NAME, null, values);
        return true;
    }
    
    //
    // ステージIDを変換
    //
    public static String StageID(int id)
    {
        String res;
        switch (id) {
        case 0:
            res = "x1";
            break;
        case 1:
            res = "x2";
            break;
        case 2:
            res = "x3";
            break;
        case 3:
            res = "x4";
            break;
        case 4:
            res = "p1";
            break;
        case 5:
            res = "p2";
            break;
        case 6:
            res = "p3";
            break;
        case 7:
            res = "p4";
            break;
        
        default:
            res = null;
            break;
        }
        return res;
    }
}
