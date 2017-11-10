package com.sqq.yin2048.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.sqq.yin2048.R;
import com.sqq.yin2048.view.ItemView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridLayout gl_game;
    private TextView tv_curr_max, tv_history_max;
    private Button btn_restart, btn_select_column, btn_intro;


    private final static String LEVEL_4 = "4";
    private final static String LEVEL_5 = "5";
    private final static String LEVEL_6 = "6";
    private String level = LEVEL_4;


    private Animation animScale;
    private int columnCount = 4;
    private ItemView[][] views;
    private int itemWidth;
    private int divider;
    private int margin;
    private List<Point> lists;
    private float downX, downY;
    private int lastNum = -1;
    private List<Integer> nums;
    private int maxScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gl_game = (GridLayout) findViewById(R.id.gl_game);
        tv_curr_max = (TextView) findViewById(R.id.tv_curr_max);
        tv_history_max = (TextView) findViewById(R.id.tv_history_max);
        btn_intro = (Button) findViewById(R.id.btn_intro);
        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_select_column = (Button) findViewById(R.id.btn_select_column);


        btn_intro.setOnClickListener(clicklis);
        btn_restart.setOnClickListener(clicklis);
        btn_select_column.setOnClickListener(clicklis);

        init();
        initGame();

    }

    private View.OnClickListener clicklis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_restart:
                    restart();
                    break;
                case R.id.btn_select_column:
                    selectColumn();
                    break;
                case R.id.btn_intro:
                    showInfo();
                    break;
            }
        }
    };

    private void showInfo() {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"关于作者", "关于游戏"}, listener)
                .show()
                .setCanceledOnTouchOutside(false);
    }
    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        private Intent intent;
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    intent = new Intent(MainActivity.this,AboutAuthorActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(MainActivity.this,AboutGameActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void init() {
        divider = getResources().getDimensionPixelSize(R.dimen.dimens1);
        margin = getResources().getDimensionPixelOffset(R.dimen.dimens10);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        itemWidth = (screenWidth - 2 * margin - (columnCount + 1) * divider) / columnCount;
        lists = new LinkedList<>();
        nums = new LinkedList<>();

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_create_num);
    }

    private void initGame() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        itemWidth = (screenWidth - 2 * margin - (columnCount + 1) * divider) / columnCount;
        tv_history_max.setText(String.format("历史最高分:%d", getMaxScore()));
        gl_game.removeAllViews();
        gl_game.setColumnCount(columnCount);
        views = new ItemView[columnCount][columnCount];
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                views[i][j] = new ItemView(this);
                views[i][j].setNum(0);
                views[i][j].setPoint(i, j);
                views[i][j].setSize(itemWidth, divider, columnCount);
                gl_game.addView(views[i][j]);
            }
        }
        addNum(2);
    }

    private void checkNull() {
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (views[i][j].getNum() == 0) {
                    lists.add(views[i][j].getPoint());
                }
            }
        }
    }

    private void createNum() {
        int index = (int) (Math.random() * lists.size());
        Point point = lists.get(index);
        int num = Math.random() > 0.1 ? 2 : 4;
        views[point.y][point.x].setNum(num);
        views[point.y][point.x].startAnimation(animScale);
        lists.remove(index);
    }

    private void addNum(int count) {
        lists.clear();
        checkNull();
        if (lists.size() > 0) {
            for (int i = 0; i < count; i++) {
                createNum();
            }
        }
    }

    /**
     * 触摸事件的处理
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_UP:
                float dx = x - downX;
                float dy = y - downY;
                if (Math.abs(dx) < 50 || Math.abs(dy) < 50) {
                    return super.onTouchEvent(event);
                }
                if (canMove()) {
                    if (move(getMoveOrientation(dx, dy))) {
                        addNum(1);
                        tv_curr_max.setText(String.format("当前最高分:%d", maxScore));
                    }
                } else {
                    gameOver();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void gameOver() {
        String msg = "";
        if (maxScore < 256) {
            msg = "亲，你太弱了";
        } else if (maxScore < 512) {
            msg = "你晋升为青铜";
        } else if (maxScore < 1024) {
            msg = "你晋升为黄金";
        } else if (maxScore < 2048) {
            msg = "你晋升为铂金";
        } else if (maxScore < 4096) {
            msg = "你晋升为钻石";
        } else if (maxScore < 8192) {
            msg = "你飞升为星耀";
        } else {
            msg = "你已成为王者";
        }
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setNegativeButton("重新开始", dialogClicks)
                .setNeutralButton("退出游戏", dialogClicks)
                .show()
                .setCanceledOnTouchOutside(false);
        saveScore();
    }

    private void restart() {
        saveScore();
        tv_history_max.setText(String.format("历史最高分:%d", getMaxScore()));
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                views[i][j].setNum(0);
            }
        }
        addNum(2);
    }

    private void selectColumn() {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"4*4", "5*5", "6*6"}, dialogClicks)
                .show()
                .setCanceledOnTouchOutside(false);
    }

    private DialogInterface.OnClickListener dialogClicks = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    restart();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    finish();
                    break;
                case 0:
                    columnCount = 4;
                    level = LEVEL_4;
                    initGame();
                    break;
                case 1:
                    columnCount = 5;
                    level = LEVEL_5;
                    initGame();
                    break;
                case 2:
                    columnCount = 6;
                    level = LEVEL_6;
                    initGame();
                    break;
            }
        }
    };

    /**
     * 判断是否还可以继续移动，即游戏是否结束
     *
     * @return
     */
    private boolean canMove() {
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int num = views[i][j].getNum();
                if (num == 0) {
                    return true;
                }
                if ((i - 1) > 0 && num == views[i - 1][j].getNum()) {
                    return true;
                }
                if ((i + 1) < columnCount && num == views[i + 1][j].getNum()) {
                    return true;
                }
                if ((j - 1) >= 0 && num == views[i][j - 1].getNum()) {
                    return true;
                }
                if ((j + 1) < columnCount && num == views[i][j + 1].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    private char getMoveOrientation(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            if (x > 0) {
                return 'r';
            } else {
                return 'l';
            }
        } else {
            if (y > 0) {
                return 'b';
            } else {
                return 't';
            }
        }
    }

    private boolean move(char orientation) {
        switch (orientation) {
            case 'l':
                return moveToLeft();
            case 'r':
                return moveToRight();
            case 't':
                return moveToTop();
            case 'b':
                return moveToBottom();
            default:
                return false;
        }
    }

    private boolean moveToBottom() {
        boolean flag = false;
        int zeroIndex = -1;
        for (int i = 0; i < columnCount; i++) {
            for (int j = columnCount - 1; j >= 0; j--) {
                int num = views[j][i].getNum();
                if (num != 0) {
                    if (zeroIndex != -1 && j < zeroIndex) {
                        flag = true;
                    }
                    if (lastNum == -1) {
                        lastNum = num;
                    } else {
                        if (lastNum == num) {
                            flag = true;
                            nums.add(num * 2);
                            lastNum = -1;
                        } else {
                            nums.add(lastNum);
                            lastNum = num;
                        }
                    }
                } else {
                    zeroIndex = j;
                    /*if (lastNum != -1) {
                        nums.add(lastNum);
                        lastNum = -1;
                    }*/
                }
            }
            if (lastNum != -1) {
                nums.add(lastNum);
            }
            for (int j = 0; j < columnCount; j++) {
                if ((columnCount - 1 - j) < nums.size()) {
                    int num = nums.get(columnCount - 1 - j);
                    if (maxScore < num) {
                        maxScore = num;
                    }
                    views[j][i].setNum(num);
                } else {
                    views[j][i].setNum(0);
                }
            }
            nums.clear();
            lastNum = -1;
            zeroIndex = -1;
        }
        return flag;
    }

    private boolean moveToTop() {
        boolean flag = false;
        int zeroIndex = -1;
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int num = views[j][i].getNum();
                if (num != 0) {
                    if (zeroIndex != -1 && j > zeroIndex) {
                        flag = true;
                    }
                    if (lastNum == -1) {
                        lastNum = num;
                    } else {
                        if (lastNum == num) {
                            flag = true;
                            nums.add(num * 2);
                            lastNum = -1;
                        } else {
                            nums.add(lastNum);
                            lastNum = num;
                        }
                    }
                } else {
                    zeroIndex = j;
                    /*if (lastNum != -1) {
                        nums.add(lastNum);
                        lastNum = -1;
                    }*/
                }
            }
            if (lastNum != -1) {
                nums.add(lastNum);
            }
            for (int j = 0; j < columnCount; j++) {
                if (j < nums.size()) {
                    int num = nums.get(j);
                    if (maxScore < num) {
                        maxScore = num;
                    }
                    views[j][i].setNum(num);
                } else {
                    views[j][i].setNum(0);
                }
            }
            nums.clear();
            lastNum = -1;
            zeroIndex = -1;
        }
        return flag;
    }

    private boolean moveToRight() {
        boolean flag = false;
        int zeroIndex = -1;
        for (int i = 0; i < columnCount; i++) {
            for (int j = columnCount - 1; j >= 0; j--) {
                int num = views[i][j].getNum();
                if (num != 0) {
                    if (zeroIndex != -1 && j < zeroIndex) {
                        flag = true;
                    }
                    if (lastNum == -1) {
                        lastNum = num;
                    } else {
                        if (lastNum == num) {
                            flag = true;
                            nums.add(num * 2);
                            lastNum = -1;
                        } else {
                            nums.add(lastNum);
                            lastNum = num;
                        }
                    }
                } else {
                    zeroIndex = j;
                    /*if (lastNum != -1) {
                        nums.add(lastNum);
                        lastNum = -1;
                    }*/
                }
            }
            if (lastNum != -1) {
                nums.add(lastNum);
            }
            for (int j = 0; j < columnCount; j++) {
                if ((columnCount - 1 - j) < nums.size()) {
                    int num = nums.get(columnCount - 1 - j);
                    if (maxScore < num) {
                        maxScore = num;
                    }
                    views[i][j].setNum(num);
                } else {
                    views[i][j].setNum(0);
                }
            }
            nums.clear();
            lastNum = -1;
            zeroIndex = -1;
        }
        return flag;
    }

    private boolean moveToLeft() {
        boolean flag = false;
        int zeroIndex = -1;
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int num = views[i][j].getNum();
                if (num != 0) {
                    if (zeroIndex != -1 && j > zeroIndex) {
                        flag = true;
                    }
                    if (lastNum == -1) {
                        lastNum = num;
                    } else {
                        if (lastNum == num) {
                            flag = true;
                            nums.add(num * 2);
                            lastNum = -1;
                        } else {
                            nums.add(lastNum);
                            lastNum = num;
                        }
                    }
                } else {
                    zeroIndex = j;
                    /*if (lastNum != -1) {
                        nums.add(lastNum);
                        lastNum = -1;
                    }*/
                }
            }
            if (lastNum != -1) {
                nums.add(lastNum);
            }
            for (int j = 0; j < columnCount; j++) {
                if (j < nums.size()) {
                    int num = nums.get(j);
                    if (maxScore < num) {
                        maxScore = num;
                    }
                    views[i][j].setNum(num);
                } else {
                    views[i][j].setNum(0);
                }
            }
            nums.clear();
            lastNum = -1;
            zeroIndex = -1;
        }
        return flag;
    }

    private void saveScore() {
        int history = getMaxScore();
        if (history < maxScore) {
            SharedPreferences.Editor e = getSharedPreferences("level", MODE_PRIVATE).edit();
            e.putInt(level, maxScore);
            e.commit();
        }
    }

    private int getMaxScore() {
        return getSharedPreferences("level", MODE_PRIVATE).getInt(level, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveScore();
    }
}
