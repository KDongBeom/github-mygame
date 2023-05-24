package com.taewon.mygallag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_dialog);
        init();
    }

    private void init(){
        findViewById(R.id.goMainBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this,
                        StartActivity.class);
                startActivity(intent);  //처음으로 버튼 클릭시 처음으로 이동하게 해주는 명령
                finish();
            }
        }); //score점수를 넘겨받고 점수 띄우는 명령어이고
        ((TextView)findViewById(R.id.userFinalScoreText)).setText(getIntent().getIntExtra("score",0)+"");
    }
}
