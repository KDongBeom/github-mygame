package com.taewon.mygallag;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private Intent userIntent;
    ArrayList<Integer> bgMusicList;
    public static SoundPool effectSound;
    public static float effectVolumn;
    ImageButton specialShotBtn;
    public static ImageButton fireBtn, reloadBtn;
    JoystickView joyStick;
    public static TextView scoreTv;
    LinearLayout gameFrame;
    ImageView pauseBtn;
    public static LinearLayout lifeFrame;
    SpaceInvadersView spaceInvadersView;
    public static MediaPlayer bgMusic;
    int bgMusicIndex;
    public static TextView bulletCount;
    private static ArrayList<Integer> effectSoundList;
    public static final int PLAYER_SHOT = 0;
    public static final int PLAYER_HURT = 1;
    public static final int PLAYER_RELOAD = 2;
    public static final int PLAYER_GET_ITEM = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Activity가 생성될때 실행됨
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIntent = getIntent();
        bgMusicIndex = 0;
        bgMusicList = new ArrayList<Integer>(); //음악넣을 arrylist생성
        bgMusicList.add(R.raw.main_game_bgm1);
        bgMusicList.add(R.raw.main_game_bgm2);
        bgMusicList.add(R.raw.main_game_bgm3);


        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        effectVolumn = 1;

        specialShotBtn = findViewById(R.id.specialShotBtn); //번개
        joyStick = findViewById(R.id.joyStick); //조이스틱
        scoreTv = findViewById(R.id.score); //TextView 점수
        fireBtn = findViewById(R.id.fireBtn); //ImageButton 총알쏘는 버튼
        reloadBtn = findViewById(R.id.reloadBtn);//ImageButton 장전
        gameFrame = findViewById(R.id.gameFrame); //첫번째 LinearLayout 전체
        pauseBtn = findViewById(R.id.pauseBtn);//ImageButton 멈춤 버튼
        lifeFrame = findViewById(R.id.lifeFrame);//윗부분 LinearLayout pause life, score있는 레이아웃

        init();
        setBtnBehavior(); //조이스틱 작동함수 실행
    }

    @Override
    protected void onResume() {
        super.onResume();
        bgMusic.start();    //bgm 시작
        spaceInvadersView.resume(); //spaceInvadersView에 resume함수 호출
    }

    private void init() {
        Display display = getWindowManager().getDefaultDisplay();  //view의 display를 얻어온다.
        Point size = new Point();
        display.getSize(size);
        //??? 아이템 화면에 띄우기
        spaceInvadersView = new SpaceInvadersView(this, userIntent.getIntExtra("character", R.drawable.ship_0000), size.x, size.y);
        gameFrame.addView(spaceInvadersView); //프레임에 만든 아이템들 넣기
        //음악 바꾸기
        changeBgMusic();
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //음악이 끝나면
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                changeBgMusic();
            }
        });

        bulletCount = findViewById(R.id.bulletCount);   //총알 개수

        // spaceInvadersView의 getPlayer() 구현
        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30");  // 30/30표시
        scoreTv.setText(Integer.toString(spaceInvadersView.getScore())); //score:0 표시

        effectSoundList = new ArrayList<>(); //총 쏠때, 장전할때, 아이템을 먹을때, 라이프가 닳때 다른 sound가 나도록 하는 배열
        effectSoundList.add(PLAYER_SHOT, effectSound.load(MainActivity.this, R.raw.player_shot_sound, 1));
        effectSoundList.add(PLAYER_HURT, effectSound.load(MainActivity.this, R.raw.player_hurt_sound, 1));
        effectSoundList.add(PLAYER_RELOAD, effectSound.load(MainActivity.this, R.raw.reload_sound, 1));
        effectSoundList.add(PLAYER_GET_ITEM, effectSound.load(MainActivity.this, R.raw.player_get_item_sound, 1));
        bgMusic.start();  //음악이 바뀌면서 재생
    }

    private void changeBgMusic() {
        bgMusic = MediaPlayer.create(this, bgMusicList.get(bgMusicIndex)); //음악하나 가져와 만들기
        bgMusic.start();
        bgMusicIndex++; //음악 바꾸기 위해 증가해 놓기
        bgMusicIndex = bgMusicIndex % bgMusicList.size(); //음악 개수만큼만 바뀌게 하기 위해
    }

    @Override
    protected void onPause() {   //일시 정지시
        super.onPause();
        bgMusic.pause();    //bgm 정지
        spaceInvadersView.pause();  // spaceInvadersView에 pause함수 호출
    }

    public static void effectSound(int flag) {
        effectSound.play(effectSoundList.get(flag), effectVolumn, effectVolumn,
                0, 0, 1.0f);
        //Soundpool 실행
    }


    private void setBtnBehavior() { //조이스틱 동작 명령어
        joyStick.setAutoReCenterButton(true);   //자동으로 다시 조이스틱 중간으로 감
        joyStick.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("keycode", Integer.toString((i)));
                return false;
            }
        });

        //조이스틱 이동방향으로 비행기 이동하게 한다
        joyStick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
//                Log.d("angle", Integer.toString(angle));
//                Log.d("force", Integer.toString(strength));
                if (angle > 67.5 && angle < 112.5) {
                    //위
                    spaceInvadersView.getPlayer().moveUp(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                } else if (angle > 247.5 && angle < 292.5) {
                    //아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                } else if (angle > 112.5 && angle < 157.5) {
                    //왼쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                } else if (angle > 157.5 && angle < 202.5) {
                    //왼쪽
                    spaceInvadersView.getPlayer().moveLeft(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 202.5 && angle < 247.5) {
                    //왼쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                } else if (angle > 22.5 && angle < 67.5) {
                    //오른쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                } else if (angle > 337.5 || angle < 22.5) {
                    //오른쪽
                    spaceInvadersView.getPlayer().moveRight(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 292.5 && angle < 337.5) {
                    //오른쪽 아래
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                }
            }
        });

        //총알 버튼 눌렀을때
        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().fire();
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {   //새로고침
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().reloadBullets();
                //SpaceInvadersView -> StarshipSprite -> reloadButtlets()
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.pause();  //spaceInvadersView 일시정지
                PauseDialog pauseDialog = new PauseDialog(MainActivity.this);
                pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override   //onDismissListener pauseDialog가 종료 될때 수행되는작업을 허요하는 리스너
                    public void onDismiss(DialogInterface dialogInterface) {
                        spaceInvadersView.resume(); //spaceInvadersView 종료
                    }
                });
                pauseDialog.show(); //pauseDialog 띄움
            }
        });

        specialShotBtn.setOnClickListener(new View.OnClickListener() {  //스페셜샷 클릭시 리스터
            @Override
            public void onClick(View view) {
                if (spaceInvadersView.getPlayer().getSpecialShotCount() >= 0)
                    spaceInvadersView.getPlayer().specialShot();
            }
        });

    }
}