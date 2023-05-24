package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{
    Context context;
    SpaceInvadersView game;
    public float speed;
    private int bullets, life=3, powerLevel;
    private int specialShotCount;
    private boolean isSpecialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<Integer>();
    private final static float MAX_SPEED = 3.5f;
    private final static int MAX_HEART = 3;

    private RectF rectF;
    private boolean isReloading = false;

    public StarshipSprite(Context context, SpaceInvadersView game, int resId, int x,
                          int y, float speed){
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        this.speed = speed;
        init();
    }

    public void init(){
        dx=dy=0;
        bullets=30;
        life=3;
        specialShotCount = 3;
        powerLevel=0;
        Integer [] shots = {R.drawable.shot_001,R.drawable.shot_002,R.drawable.shot_003,
                R.drawable.shot_004,R.drawable.shot_005,R.drawable.shot_006,R.drawable.shot_007};

        for(int i =0; i<shots.length; i++){
            bulletSprites.add(shots[i]);
        }
    }

    @Override
    public void move() {
        //벽에 부딪히면 못 가게 하기
        if((dx<0) && (x <120)) return;
        if((dx>0) && (x > game.screenW - 120)) return;
        if((dy<0) && (y <120)) return;
        if((dy>0) && (y > game.screenH-120)) return;
        super.move();
    }

    //총알 개수 리턴
    public int getBulletsCount() {return bullets;}

    //위, 아래, 오른쪽, 왼쪽 이동하기
    public void moveRight(double force) {setDx((float)(1*force*speed));}
    public void moveLeft(double force) {setDx((float)(-1*force*speed));}
    public void moveDown(double force) {setDy((float)(1*force*speed));}
    public void moveUp(double force) {setDy((float)(-1*force*speed));}

    public void resetDx(){ setDx(0); }
    public void resetDy(){ setDy(0); }

    //스피드 제어
    public void plusSpeed(float speed) { this.speed += speed;}
    //총알 발사
    public void fire(){
        if(isReloading | isSpecialShooting){return;}
        MainActivity.effectSound(MainActivity.PLAYER_SHOT);
        //ShotSprite 생성자구현
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel),
                getX()+10, getY()-30, -16 );

        //SpaceInvadersView 의 getSprites()구현
        game.getSprites().add(shot);
        bullets--;

        MainActivity.bulletCount.setText(bullets + "/30");
        Log.d("bullets", bullets + "/30");
        if(bullets ==0){
            reloadBullets();
            return;
        }
    }
    public void powerUp(){
        if(powerLevel >= bulletSprites.size() - 1){
            game.setScore(game.getScore() + 1);
            MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
            return;
        }
        powerLevel++;
        MainActivity.fireBtn.setImageResource(bulletSprites.get(powerLevel));
        MainActivity.fireBtn.setBackgroundResource(R.drawable.round_button_shape);
    }
    //총알 다시 셋팅
    public void reloadBullets(){
        isReloading = true;
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD);
        MainActivity.fireBtn.setEnabled(false);
        MainActivity.reloadBtn.setEnabled(false);
        //Thread sleep 사용하지 않고 지연시키는 클래스
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                bullets=30;
                MainActivity.fireBtn.setEnabled(true);
                MainActivity.reloadBtn.setEnabled(true);
                MainActivity.bulletCount.setText(bullets + "/30");
                MainActivity.bulletCount.invalidate(); //화면새로고침
                isReloading=false;
            }
        }, 2000);
    }

    //필사기
    public void specialShot(){
        specialShotCount--;
        //SpecialshotSprite 구현
        SpecialshotSprite shot = new SpecialshotSprite(context, game, R.drawable.speed_item,
                getRect().right - getRect().left, 0);

        //game -> SpaceInvadersView의 getSprites() : sprite에 shot추가하기
        game.getSprites().add(shot);
    }

    public int getSpecialShotCount(){ return specialShotCount; }
    public boolean isSpecialShooting() { return isSpecialShooting;}
    public void setSpecialShooting(boolean specialShooting) {
        isSpecialShooting=specialShooting; }

    public int getLife() { return life;}

    public void hurt(){
        life--;
        if(life<=0) {
            ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                    (R.drawable.ic_baseline_favorite_border_24));
            // SpaceInvadersView의 endGame() 에서 game종료시키기
            game.endGame();
            return;
        }
        Log.d("hurt", Integer.toString(life));  //생명 확인하기
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                R.drawable.ic_baseline_favorite_border_24);
    }

    //생명 얻었을 때
    public void heal(){
        Log.d("heal", Integer.toString(life));
        if(life + 1  > MAX_HEART){
            game.setScore(game.getScore() +1);
            MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
            return;
        }
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                R.drawable.ic_baseline_favorite_24);
        life++;
    }

    //속도 올리기
    private void speedUp(){
        if(MAX_SPEED >= speed + 0.2f) plusSpeed(0.2f);
        else{
            game.setScore(game.getScore() + 1);
            MainActivity.scoreTv.setText(Integer.toString((game.getScore())));
        }
    }
    //Sprite 의 handleCollision()-> 충돌처리
    @Override
    public void handleCollision(Sprite other) {
        if(other instanceof AlienSprite){
            // Alien 아이템이면
            game.removeSprite(other);
            MainActivity.effectSound((MainActivity.PLAYER_HURT));
            hurt();
        }
        if(other instanceof SpeedItemSprite){
            //스피드 아이템이면
            game.removeSprite(other);
            MainActivity.effectSound((MainActivity.PLAYER_GET_ITEM));
            speedUp();
        }
        if(other instanceof AlienShotSprite){
            //총알 맞으면
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            game.removeSprite(other);
            hurt();
        }
        if(other instanceof PowerItemSprite){
            //파워 아이템을 맞으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            powerUp();
            game.removeSprite(other);
        }
        if(other instanceof HealitemSprite){
            //생명 아이템 맞으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            game.removeSprite(other);
            heal();
        }
    }
    public int getPowerLevel(){ return powerLevel; }
}

