package jp.dego.xpreverse.highscores;

import java.util.List;

import jp.dego.xpreverse.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighScoresAdapter extends ArrayAdapter<HighScoreListItem>
{
    private LayoutInflater inflater;
    
    public HighScoresAdapter(Context context, int ResourceId, List<HighScoreListItem> list)
    {
        super(context, ResourceId, list);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // 
        HighScoreListItem item = (HighScoreListItem)getItem(position);
        
        // ビューを受け取る
        View view = convertView;
        if (view == null) {
            // 受け取ったビューがnullなら新しくビューを生成
            view = inflater.inflate(R.layout.score, null);
        }
        
        // 表示すべきデータの取得
        TextView tv1 = (TextView)view.findViewById(R.id.TextView_ScoreStage);
        tv1.setText(item.mStageNum);
        
        TextView tv2 = (TextView)view.findViewById(R.id.TextView_HighScore);
        tv2.setText(item.mScoreTime);
        
        TextView tv3 = (TextView)view.findViewById(R.id.TextView_ScoreDate);
        tv3.setText(item.mScoreDate);
        
        return view;
    }
}
