package com.xfhy.snake;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity{
    
	private long mExitTime;
	private MyTile mSnakeView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSnakeView = (MyTile) findViewById(R.id.mytile);
        mSnakeView.setTextView((TextView) findViewById(R.id.text));
    }
    
    
    //按2次返回键退出
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
                if ((System.currentTimeMillis() - mExitTime) > 2000) 
                {
                        Object mHelperUtils;
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                } 
                else 
                {
                        finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
     }
}
