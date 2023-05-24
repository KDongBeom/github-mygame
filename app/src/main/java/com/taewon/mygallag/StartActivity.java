package com.taewon.mygallag;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    int characterId, effectId;
    ImageButton startBtn;
    TextView guideTv;
    MediaPlayer mediaPlayer;
    ImageView imgView[] = new ImageView[8];
    Integer img_id[] = {R.id.ship_001, R.id.ship_002, R.id.ship_003, R.id.ship_004, R.id.ship_005, R.id.ship_006, R.id.ship_007, R.id.ship_008};
    Integer img[] = {R.drawable.ship_0000, R.drawable.ship_0001, R.drawable.ship_0002, R.drawable.ship_0003, R.drawable.ship_0004, R.drawable.ship_0005, R.drawable.ship_0006, R.drawable.ship_0007};
    SoundPool soundPool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mediaPlayer = MediaPlayer.create(this, R.raw.robby_bgm); //이 명령어를 씀으로써 시작할때 bgm 깔림
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        effectId = soundPool.load(this, R.raw.reload_sound, 1);
        startBtn = findViewById(R.id.startBtn); //시작버튼
        guideTv = findViewById(R.id.guideTv);   //마지막 TextView

        for (int i=0; i< imgView.length; i++){
            imgView[i] = findViewById(img_id[i]);
            int index = i; //선택한 이미지 번호 알기
            imgView[i].setOnClickListener(new View.OnClickListener() {      //이미지 클릭시 리스너
                @Override
                public void onClick(View view) {
                    characterId = img[index];   //img 0~7까지
                    startBtn.setVisibility(View.VISIBLE);   // 이미지 클릭하기전에 버튼이 보이지 않음
                    startBtn.setEnabled(true); // true를 씀으로 버튼이 클릭이 가능하게하는 명령
                    startBtn.setImageResource(characterId); //버튼에 선택한 이미지 넣기
                    guideTv.setVisibility(View.INVISIBLE); //마지막 TextView 숨기기
                    soundPool.play(effectId,1,1,0,0,1.0f); //소리 재생
                }
            });
        }
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); // 화면 세로로 돌리기
        init(); //이 함수를 써서 선택된 이미지를 넘김으로 다음을 수행
        }
        private void init(){
        findViewById(R.id.startBtn).setVisibility(View.GONE); //버튼 위치는 남겨두고 숨기기
        findViewById(R.id.startBtn).setEnabled(false);  //선택 안되게 하기
        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("character", characterId); //선택한 이미지 넘기기
                startActivity(intent);
                finish(); //액티비티 종료
            }
        });
        }

    @Override
    protected void onDestroy() {    //액티비티 소멸직전 호출 mediaPlayer가 살아있으면 리소스 소멸시킨다.
        super.onDestroy();
        if(mediaPlayer != null) {   //mediaPlayer가 재생되고있으면
            mediaPlayer.release();
            mediaPlayer =null;
        }
    }
}
