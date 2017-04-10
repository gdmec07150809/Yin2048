package com.sqq.yin2048.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sqq.yin2048.R;

public class Welcome extends AppCompatActivity {

    private RelativeLayout rl_welcome;
    private ImageView img_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        rl_welcome = (RelativeLayout) findViewById(R.id.rl_welcome);
        img_play = (ImageView) findViewById(R.id.img_play);

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //设置动画效果
        AlphaAnimation aa = new AlphaAnimation(0,1);
        aa.setDuration(5000);  //设置持续播放时间
        aa.setFillAfter(true);

        ScaleAnimation sa = new ScaleAnimation(0,1,0,1,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(5000);
        sa.setFillAfter(true);

        RotateAnimation ra = new RotateAnimation(0,360,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(5000);
        ra.setFillAfter(true);

        //组合三个动画同时播放
        AnimationSet as = new AnimationSet(false);
        //添加三个动画效果，没有先后顺序
        as.addAnimation(aa);
        as.addAnimation(sa);
        as.addAnimation(ra);
        as.setDuration(5000);

        rl_welcome.startAnimation(as);

        as.setAnimationListener(new MyAnimationListener());
    }

    class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
