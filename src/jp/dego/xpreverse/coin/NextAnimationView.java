package jp.dego.xpreverse.coin;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

/* 内部クラス 3D回転アニメーションでコインの反対側が見えるときに実行される処理を記述 */
class NextAnimationView implements AnimationListener
{
    private float mid;
    private float end;
    private float depth;
    
    private Coin mCoin;
    private ImageView frontView, backView;
    private boolean isFront;
    private float centerX, centerY;
    
    private static final int DURATION = 300;
    
    public NextAnimationView(Coin coin, float mid, float end, float depth)
    {
        this.mCoin = coin;
        this.frontView = coin.getFrontView();
        this.backView = coin.getBackView();
        this.isFront = coin.isFront();
        this.centerX = coin.getWidth() / 2.0f;
        this.centerY = coin.getWidth() / 2.0f;
        this.mid = mid;
        this.end = end;
        this.depth = depth;
    }
    
    @Override
    public void onAnimationEnd(Animation animation)
    {
        mCoin.post(new Runnable()
        {
            public void run()
            {
                if (isFront) {
                    frontView.setVisibility(View.GONE);
                    backView.setVisibility(View.VISIBLE);
                    isFront = false;
                } else {
                    frontView.setVisibility(View.VISIBLE);
                    backView.setVisibility(View.GONE);
                    isFront = true;
                }
                
                Rotate3dAnimation rot = new Rotate3dAnimation(mid + 180f, end + 180f, centerX,
                        centerY, depth, false);
                rot.setDuration(DURATION);
                rot.setInterpolator(new OvershootInterpolator());
                rot.setAnimationListener(new AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {}
                    
                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {}
                    
                    @Override
                    public void onAnimationEnd(Animation animation)
                    {}
                });
                mCoin.startAnimation(rot);
            }
        });
    }
    
    @Override
    public void onAnimationRepeat(Animation animation)
    {}
    
    @Override
    public void onAnimationStart(Animation animation)
    {}
}
