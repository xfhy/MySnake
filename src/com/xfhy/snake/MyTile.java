package com.xfhy.snake;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 把屏幕划分成一个二维的矩阵,用int[][]来记录每一个方格的状态
 * 计算横,纵向下图形(方格)的个数
 * 计算横纵向的偏移量,用来绘制每个方格时定位
 * 初始化二维数组
 * */

public class MyTile extends View {
	private int size = 30; //图片的大小
    private int xcount;   //定义图片的x上需要放的数量
    private int ycount;   //定义图片的y上需要放的数量
    private int xoffset;  //x方向的偏移量
    private int yoffset;  //y方向的偏移量
    private int[][] map;  //地图二维数组
    private Bitmap[] pics;//用来存放图片,3张图片
    public static final int GREEN_STAR = 1;  //3种图片类型,定义成常量
    public static final int RED_STAR = 2;
    public static final int YELLOW_STAR = 3;
    private int mm=5;
    
    //-----------方向
    public static final int UP = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;
    public static final int LEFT = 4;
    public int direction;
    public int nextdirection;
    
    private int mMode = READY;  //游戏运行状态
    public static final int PAUSE = 1;   //暂停
    public static final int READY = 2;   //准备
    public static final int RUNING = 3;  //正在运行
    public static final int LOSE = 4;    //结束,停止运行
    
    private TextView textview = null;    //屏幕上显示的文字
    private int score = 0;    //当前分数
    private int speed = 200;
    
    //蛇身
    private ArrayList<Coordinate> snakeList = new ArrayList<Coordinate>();
    
    //苹果
    private ArrayList<Coordinate> appleList = new ArrayList<Coordinate>();
    
    
    
    //复写了2个参数的构造方法
	public MyTile(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//将控件设置成为获取焦点状态,默认是无法获取焦点的,只有设置成true才能获取控件的点击事件
		setFocusable(true);   
		
		setOnTouchListener(new View.OnTouchListener(){

			//设置起始坐标,偏移量
			//实现触屏滑动  可以感知用户想要的方向
			private float startX,startY,offsetX,offsetY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						startX = event.getX();   //按下屏幕时的坐标
						startY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						offsetX = event.getX()-startX;
						offsetY = event.getY()-startY;
						
						//如果X方向上的偏移量比Y方向上的偏移量大,则说明用户需要的是水平方向上的改变
						if( Math.abs(offsetX)>Math.abs(offsetY) )
						{
							if( offsetX<=-5 )
							{
								System.out.println("左");
								if(direction != RIGHT)
								       nextdirection = LEFT;
							}
							else if(offsetX>=5)
							{
								System.out.println("右");
								if(direction != LEFT)
								       nextdirection = RIGHT;
							}
						}
						else
						{
							if( offsetY<=-5 )
							{
								System.out.println("上");
								if(direction != DOWN)
								       nextdirection = UP;
							}
							else if(offsetY>=5)
							{
								System.out.println("下");
								if(direction != UP)
								       nextdirection = DOWN;
							}
						}
						break;
				}
				return true;
			}
			
		});
	}
    
	//复写onDraw()方法
	//这里作用:完成反复绘图
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		
		//绘图
		for(int i=0; i<xcount; i++)
		{
			for(int j=0; j<ycount; j++)
			{
				if(map[i][j]>0)   //大于0时就绘图
				{
					canvas.drawBitmap(pics[map[i][j]],
							xoffset+i*size,yoffset+j*size, paint);
				}
			}
		}
	}

	//onSizeChanged()是在布局发生变化时的回调函数，间接回去调用onMeasure, onLayout函数重新布局
	//如果是刚加入的视图,变更前的值为0    
	//这里作用:完成初始化工作
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		xcount = (int)Math.floor(w/size);   //x方向需要的图片数量,向下取整.
		ycount = (int)Math.floor(h/size);
		
		xoffset = (w - size*xcount) / 2;  //宽度减去总图片的宽度/2
		yoffset = (h - size*ycount) / 2;
		
		map = new int[xcount][ycount];
		initGame();      //初始化游戏,在刚进游戏时
	}
    
	
	
	//地图数组赋值   图片下标,该点坐标x,y
	public void setTile(int picIndex,int x,int y)
	{
		map[x][y] = picIndex;
	}
	
	//将3副图片加载进来,加载到pics图片数组里
	public void loadPic(int key,Drawable drawable)
	{
		/*
		 * 1、Drawable就是一个可画的对象，其可能是一张位图（BitmapDrawable），也可能是一个图形（ShapeDrawable），还有可能是一个图层（LayerDrawable），我们根据画图的需求，创建相应的可画对象
			2、Canvas画布，绘图的目的区域，用于绘图
			3、Bitmap位图，用于图的处理
			4、Matrix矩阵
		 * */
		// 建立对应 bitmap,并设置颜色格式
		Bitmap bitmap = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);   // 建立对应 bitmap 的画布
		drawable.setBounds(0,0,size,size);   //它是指定一个矩形区域，然后通过drawable(Canvas)画的时候，就只在这个矩形区域内画图。
		drawable.draw(canvas);   // 把 drawable 内容画到画布中 
		pics[key] = bitmap;      //设置图片数组的该位置是哪个位图
	}
	
	//初始化游戏 
	public void initGame()
	{
		appleList.clear();    //移除此列表中的所有元素。
		snakeList.clear();    //移除此列表中的所有元素。
		speed = 800;          //初始速度
		score = 0;            //初始分数
		
		//将图片加载到图片数组里
		Resources r = getResources();  //从资源中获得
		pics = new Bitmap[4];
		loadPic(GREEN_STAR,r.getDrawable(R.drawable.greenstar));
		loadPic(RED_STAR,r.getDrawable(R.drawable.redstar));
		loadPic(YELLOW_STAR,r.getDrawable(R.drawable.yellowstar));
		
		//设置蛇的初始位置
		snakeList.add(new Coordinate(5, 7));
		snakeList.add(new Coordinate(4, 7));
		snakeList.add(new Coordinate(3, 7));
		snakeList.add(new Coordinate(2, 7));
		direction=RIGHT;   //设置蛇的初始方向
		nextdirection=RIGHT;
		addRandomApple();   //产生随机苹果
		update();
	}
	
	//组建墙
	/*
	 * 完成墙绘制之前需要加载游戏中用的图片,红色(蛇头),黄色(蛇身),绿色(墙,苹果)
	 * 为地图位置赋值
	 * */
	public void buildWall(){
		for(int i=0; i<xcount; i++)
		{
		   	setTile(GREEN_STAR,i,0);         //最上面那堵墙,把这些坐标设置成墙
		   	setTile(GREEN_STAR,i,ycount-1);  //最下面
		}
		for(int j=0; j<ycount; j++)
		{
			setTile(GREEN_STAR,0,j);        //左
			setTile(GREEN_STAR,xcount-1,j);
		}
	}
	
	//需要重复绘制图形,所以需要用到线程,Android里需要用到Handler
	//常用的方法是利用Handler来实现UI线程的更新的
	private MyHandler handler = new MyHandler();
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			update();
			
			/*
			 * invalidate()是用来刷新View的，必须是在UI线程中进行工作。比如在修改某个view的显示时，
			 * 调用invalidate()才能看到重新绘制的界面。
			 * invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉。
			 * */
			MyTile.this.invalidate();   //可以实现重绘
		}
		public void sleep(int delay)
		{
			this.removeMessages(0);
			
			//sendMessageDelayed 是将某个需要处理的消息事件发送给handler来处理，
			//并且在此之前按你传入的参数延迟一定的时间。
            //你需要在handler中去处理你要发送的消息。
			sendMessageDelayed(obtainMessage(0), speed);
		}
	}
	
	/*---
	 * 蛇的移动考虑:
	 * 实现不断的重绘
	 * 蛇的位置变化,形成走动
	 * 蛇的长度不断变化,用ArrayList
	 * 要记录蛇的位置需要用到一个坐标类Coordinate
	 *    private ArrayList<Coordinate>snakeTrail = new ArrayList<Coordinate>();
	 * */
	
	//更新
	public void update(){
		clearTile();   //清空map地图数组
  		buildWall();   //组建墙
		updateSnake();
		updateApple();
		handler.sleep(speed);   //延迟
	}
	
	//更新蛇的身体
	public void updateSnake()
	{
		textview.setText("您当前分数是:"+score);
		//获得蛇头
		Coordinate header = snakeList.get(0);
		Coordinate newHeader = new Coordinate(1,1);   //新的蛇头
		direction = nextdirection;
		switch(direction)
		{
		    case RIGHT:
		    	newHeader = new Coordinate(header.x+1,header.y);   //这是往右移
		    	break;
		    case LEFT:
		    	newHeader = new Coordinate(header.x-1,header.y);   //这是往右移
		    	break;
		    case UP:
		    	newHeader = new Coordinate(header.x,header.y-1);   //这是往右移
		    	break;
		    case DOWN:
		    	newHeader = new Coordinate(header.x,header.y+1);   //这是往右移
		    	break;
		}
		
		//是否撞墙
		if(newHeader.x<1||newHeader.y<1||newHeader.x>xcount-2||newHeader.y>ycount-2)
		{
			setMode(LOSE);
			return ;
		}
		
		//检测蛇是否吃到自己
		for(Coordinate c: snakeList)
		{
			if(c.x==newHeader.x && c.y==newHeader.y)
			{
				setMode(LOSE);
				return ;
			}
		}
		
		
		boolean growSnake=false;
		//蛇头吃到苹果
		if(newHeader.x==appleList.get(0).x && newHeader.y==appleList.get(0).y)  
		{
			score++;   //分数+1
			appleList.remove(0);
			addRandomApple();
			speed *= 0.9;   //速度越来越快
			growSnake=true;
		}
		
		//将新的蛇头添加到蛇的ArrayList<Coordinate>中
		snakeList.add(0,newHeader);
		if(!growSnake)   //如果没吃到果子则去掉最后一节蛇,吃到了则不清理最后一节
		{
			//将原来的蛇的尾部除掉
			snakeList.remove(snakeList.size()-1);
		}
		
		//index是蛇的具体哪一节
		int index = 0;
		for (Coordinate c : snakeList) //迭代取出ArrayList<Coordinate>中的数据
		{
			if (index == 0)   //蛇头,绘制黄色
			{
				  setTile(YELLOW_STAR, c.x, c.y);
			} 
			else              //蛇身,绘制红色
			{
				setTile(RED_STAR, c.x, c.y);
			}
			index++;          //位置往蛇的后面移动
		}
		
		
	}
	
	public void setTextView(TextView newView) {  
        textview = newView;
    }
	
	//设置游戏运行状态
	public void setMode(int newMode)
	{ 
		int oldMode = mMode;
		mMode = newMode;
		
		if(newMode==LOSE)
		{
			textview.setText("您的分数是");
			clearTile();   //清除地图
			handler.removeCallbacks(null);    //停止hander
			initGame();   //初始化游戏
		}
	}
	
	//将map数组(地图)清零
	public void clearTile(){
		for(int i=0; i<xcount; i++)
		{
		    for(int j=0; j<ycount; j++)
		    {
		    	setTile(0,i,j);
		    }
		}
	}
	
	//表示蛇身的坐标类
	private class Coordinate{
		public int x;
		public int y;
		public Coordinate(int x,int y)
		{
			super();
			this.x = x;
			this.y = y;
		}
		
		//冲突检测,蛇是否吃到苹果
		public boolean equals(Coordinate other)
		{
			if( this.x == other.x && this.y == other.y )
			{
				return true;
			}
			return false;
		}
	}

	//获取用户输入的按键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		//用户选择的方向,改变direction方向
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_UP:   //用户按下   上   键
				if(direction != DOWN)
			       nextdirection = UP;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(direction != LEFT)
					   nextdirection = RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if(direction != UP)
					   nextdirection = DOWN;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if(direction != RIGHT)
					   nextdirection = LEFT;
				break;
			case KeyEvent.KEYCODE_BACK:    //按返回键直接退出
				System.exit(0);
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//随机产生苹果
	private void addRandomApple()
	{
		Coordinate newcoor = new Coordinate(0,0);
		Random random = new Random();
		boolean found = false;
		while(!found)
		{
			newcoor.x = 1+random.nextInt(xcount-2);
			newcoor.y = 1+random.nextInt(ycount-2);
			for(Coordinate c: snakeList)
			{
				if(newcoor.x == c.x && newcoor.y == c.y)  //产生的苹果坐标与蛇身冲突
				{
					found = false;
					break;
				}
			}
			found = true;
		}
		appleList.add(newcoor);
		updateApple();
	}
	
	//给苹果绘图
	private void updateApple()
	{
		setTile(YELLOW_STAR,appleList.get(0).x,appleList.get(0).y);
	}
}
