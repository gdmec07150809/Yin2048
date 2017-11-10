package com.sqq.yin2048.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import com.sqq.yin2048.R;

/**
 * Created by Forever_CZK on 2017/11/10;
 */
@SuppressLint("AppCompatCustomView")
public class ItemView extends TextView {

    private int num;
    private int backgrundColor;
    private Point point;
    private GridLayout.LayoutParams params;


    public ItemView(Context context) {
        super(context);
        params = new GridLayout.LayoutParams();

        setGravity(Gravity.CENTER);
        setLayoutParams(params);
    }

    public void setNum(int num) {
        this.num = num;
        switch (num) {
            case 0 :
                backgrundColor = R.color.bg_0;
                break;
            case 2 :
                backgrundColor = R.color.bg_2;
                break;
            case 4 :
                backgrundColor = R.color.bg_4;
                break;
            case 8 :
                backgrundColor = R.color.bg_8;
                break;
            case 16 :
                backgrundColor = R.color.bg_16;
                break;
            case 32 :
                backgrundColor = R.color.bg_32;
                break;
            case 64 :
                backgrundColor = R.color.bg_64;
                break;
            case 128 :
                backgrundColor = R.color.bg_128;
                break;
            case 256 :
                backgrundColor = R.color.bg_256;
                break;
            case 512 :
                backgrundColor = R.color.bg_512;
                break;
            case 1024 :
                backgrundColor = R.color.bg_1024;
                break;
            case 2048 :
                backgrundColor = R.color.bg_2048;
                break;
            case 4096 :
                backgrundColor = R.color.bg_4096;
                break;
            case 8192 :
                backgrundColor = R.color.bg_8192;
                break;
            case 16384 :
                backgrundColor = R.color.bg_16384;
                break;
            default :
                backgrundColor = R.color.bg_default;
                break;
        }
        if (num != 0) {
            setText(String.valueOf(num));
        } else {
            setText("");
        }
        setBackgroundResource(backgrundColor);
    }

    public int getNum() {
        return num;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(int row, int column) {
        if (point == null) {
            point = new Point();
        }
        point.x = column;
        point.y = row;
        params.columnSpec = GridLayout.spec(column);
        params.rowSpec = GridLayout.spec(row);
    }

    public void setSize(int size, int divider, int count) {
        params.width = size;
        params.height = size;
        int heDivider = (int) Math.ceil(divider/2f);
        if (point.x == 0) {
            params.leftMargin = divider;
            params.rightMargin = heDivider;
        } else if (point.x == count-1) {
            params.leftMargin = heDivider;
            params.rightMargin = divider;
        } else {
            params.leftMargin = heDivider;
            params.rightMargin = heDivider;
        }

        if (point.y == 0) {
            params.bottomMargin = divider;
            params.topMargin = heDivider;
        } else if (point.y == count-1) {
            params.bottomMargin = heDivider;
            params.topMargin = divider;
        } else {
            params.topMargin = heDivider;
            params.bottomMargin = heDivider;
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, size/3);
    }

}
