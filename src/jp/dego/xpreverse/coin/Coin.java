package jp.dego.xpreverse.coin;

import jp.dego.xpreverse.CoinsActivity;
import jp.dego.xpreverse.R;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Coin extends FrameLayout
{
    public static final int Padding = 2;
    public static final int DURATION = 200;
    
    // ローカル変数
    private ImageView frontView, backView; // コインの表と裏の画像
    private float centerX, centerY; // 
    private boolean isFront; // 表面判定用
    private boolean isRotate; // 回転中判定用
    
    public Coin(Context context)
    {
        super(context);
        this.frontView = new ImageView(context);
        this.backView = new ImageView(context);
    }
    
    /* 初期化 */
    public void LayoutInit(int size)
    {
        this.addView(frontView, new LayoutParams(size, size));
        this.addView(backView, new LayoutParams(size, size));
        this.setPadding(Padding, Padding, Padding, Padding);
        this.isFront = true;
        this.frontView.setVisibility(View.VISIBLE);
        this.backView.setVisibility(View.GONE); // 裏面を非表示
        this.isRotate = false;
    }
    
    /* Drawableの取得 */
    public void setDrawable(int id)
    {
        if (id == CoinsActivity.MODE_ID1) {
            this.frontView.setImageResource(R.drawable.coin1f);
            this.backView.setImageResource(R.drawable.coin1b);
        } else if (id == CoinsActivity.MODE_ID2) {
            this.frontView.setImageResource(R.drawable.coin2f);
            this.backView.setImageResource(R.drawable.coin2b);
        }
    }
    
    /* startからendまでY軸回転する */
    public void applyRotation()
    {
        if (this.isRotate == true)
            return;
        this.isRotate = true;
        this.centerX = this.getWidth() / 2.0f;
        this.centerY = this.getHeight() / 2.0f;
        
        float start = 0f;
        float mid = 90f;
        float end = 180f;
        float depth = 0f;
        Rotate3dAnimation rot = new Rotate3dAnimation(start, mid, centerX, centerY, depth, true);
        rot.setDuration(DURATION);
        rot.setFillAfter(true);
        rot.setInterpolator(new AccelerateInterpolator());
        rot.setAnimationListener(new NextAnimationView(this, mid, end, depth));
        this.startAnimation(rot);
        this.isRotate = false;
        this.isFront = !this.isFront;
    }
    
    //
    // Getters and Setters
    //
    // 表裏の状態取得
    public boolean isFront()
    {
        return isFront;
    }
    
    //
    public ImageView getFrontView()
    {
        return frontView;
    }
    
    //
    public ImageView getBackView()
    {
        return backView;
    }
    
}
