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
 * ����Ļ���ֳ�һ����ά�ľ���,��int[][]����¼ÿһ�������״̬
 * �����,������ͼ��(����)�ĸ���
 * ����������ƫ����,��������ÿ������ʱ��λ
 * ��ʼ����ά����
 * */

public class MyTile extends View {
	private int size = 30; //ͼƬ�Ĵ�С
    private int xcount;   //����ͼƬ��x����Ҫ�ŵ�����
    private int ycount;   //����ͼƬ��y����Ҫ�ŵ�����
    private int xoffset;  //x�����ƫ����
    private int yoffset;  //y�����ƫ����
    private int[][] map;  //��ͼ��ά����
    private Bitmap[] pics;//�������ͼƬ,3��ͼƬ
    public static final int GREEN_STAR = 1;  //3��ͼƬ����,����ɳ���
    public static final int RED_STAR = 2;
    public static final int YELLOW_STAR = 3;
    private int mm=5;
    
    //-----------����
    public static final int UP = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;
    public static final int LEFT = 4;
    public int direction;
    public int nextdirection;
    
    private int mMode = READY;  //��Ϸ����״̬
    public static final int PAUSE = 1;   //��ͣ
    public static final int READY = 2;   //׼��
    public static final int RUNING = 3;  //��������
    public static final int LOSE = 4;    //����,ֹͣ����
    
    private TextView textview = null;    //��Ļ����ʾ������
    private int score = 0;    //��ǰ����
    private int speed = 200;
    
    //����
    private ArrayList<Coordinate> snakeList = new ArrayList<Coordinate>();
    
    //ƻ��
    private ArrayList<Coordinate> appleList = new ArrayList<Coordinate>();
    
    
    
    //��д��2�������Ĺ��췽��
	public MyTile(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//���ؼ����ó�Ϊ��ȡ����״̬,Ĭ�����޷���ȡ�����,ֻ�����ó�true���ܻ�ȡ�ؼ��ĵ���¼�
		setFocusable(true);   
		
		setOnTouchListener(new View.OnTouchListener(){

			//������ʼ����,ƫ����
			//ʵ�ִ�������  ���Ը�֪�û���Ҫ�ķ���
			private float startX,startY,offsetX,offsetY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						startX = event.getX();   //������Ļʱ������
						startY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						offsetX = event.getX()-startX;
						offsetY = event.getY()-startY;
						
						//���X�����ϵ�ƫ������Y�����ϵ�ƫ������,��˵���û���Ҫ����ˮƽ�����ϵĸı�
						if( Math.abs(offsetX)>Math.abs(offsetY) )
						{
							if( offsetX<=-5 )
							{
								System.out.println("��");
								if(direction != RIGHT)
								       nextdirection = LEFT;
							}
							else if(offsetX>=5)
							{
								System.out.println("��");
								if(direction != LEFT)
								       nextdirection = RIGHT;
							}
						}
						else
						{
							if( offsetY<=-5 )
							{
								System.out.println("��");
								if(direction != DOWN)
								       nextdirection = UP;
							}
							else if(offsetY>=5)
							{
								System.out.println("��");
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
    
	//��дonDraw()����
	//��������:��ɷ�����ͼ
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		
		//��ͼ
		for(int i=0; i<xcount; i++)
		{
			for(int j=0; j<ycount; j++)
			{
				if(map[i][j]>0)   //����0ʱ�ͻ�ͼ
				{
					canvas.drawBitmap(pics[map[i][j]],
							xoffset+i*size,yoffset+j*size, paint);
				}
			}
		}
	}

	//onSizeChanged()���ڲ��ַ����仯ʱ�Ļص���������ӻ�ȥ����onMeasure, onLayout�������²���
	//����Ǹռ������ͼ,���ǰ��ֵΪ0    
	//��������:��ɳ�ʼ������
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		xcount = (int)Math.floor(w/size);   //x������Ҫ��ͼƬ����,����ȡ��.
		ycount = (int)Math.floor(h/size);
		
		xoffset = (w - size*xcount) / 2;  //��ȼ�ȥ��ͼƬ�Ŀ��/2
		yoffset = (h - size*ycount) / 2;
		
		map = new int[xcount][ycount];
		initGame();      //��ʼ����Ϸ,�ڸս���Ϸʱ
	}
    
	
	
	//��ͼ���鸳ֵ   ͼƬ�±�,�õ�����x,y
	public void setTile(int picIndex,int x,int y)
	{
		map[x][y] = picIndex;
	}
	
	//��3��ͼƬ���ؽ���,���ص�picsͼƬ������
	public void loadPic(int key,Drawable drawable)
	{
		/*
		 * 1��Drawable����һ���ɻ��Ķ����������һ��λͼ��BitmapDrawable����Ҳ������һ��ͼ�Σ�ShapeDrawable�������п�����һ��ͼ�㣨LayerDrawable�������Ǹ��ݻ�ͼ�����󣬴�����Ӧ�Ŀɻ�����
			2��Canvas��������ͼ��Ŀ���������ڻ�ͼ
			3��Bitmapλͼ������ͼ�Ĵ���
			4��Matrix����
		 * */
		// ������Ӧ bitmap,��������ɫ��ʽ
		Bitmap bitmap = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);   // ������Ӧ bitmap �Ļ���
		drawable.setBounds(0,0,size,size);   //����ָ��һ����������Ȼ��ͨ��drawable(Canvas)����ʱ�򣬾�ֻ��������������ڻ�ͼ��
		drawable.draw(canvas);   // �� drawable ���ݻ��������� 
		pics[key] = bitmap;      //����ͼƬ����ĸ�λ�����ĸ�λͼ
	}
	
	//��ʼ����Ϸ 
	public void initGame()
	{
		appleList.clear();    //�Ƴ����б��е�����Ԫ�ء�
		snakeList.clear();    //�Ƴ����б��е�����Ԫ�ء�
		speed = 800;          //��ʼ�ٶ�
		score = 0;            //��ʼ����
		
		//��ͼƬ���ص�ͼƬ������
		Resources r = getResources();  //����Դ�л��
		pics = new Bitmap[4];
		loadPic(GREEN_STAR,r.getDrawable(R.drawable.greenstar));
		loadPic(RED_STAR,r.getDrawable(R.drawable.redstar));
		loadPic(YELLOW_STAR,r.getDrawable(R.drawable.yellowstar));
		
		//�����ߵĳ�ʼλ��
		snakeList.add(new Coordinate(5, 7));
		snakeList.add(new Coordinate(4, 7));
		snakeList.add(new Coordinate(3, 7));
		snakeList.add(new Coordinate(2, 7));
		direction=RIGHT;   //�����ߵĳ�ʼ����
		nextdirection=RIGHT;
		addRandomApple();   //�������ƻ��
		update();
	}
	
	//�齨ǽ
	/*
	 * ���ǽ����֮ǰ��Ҫ������Ϸ���õ�ͼƬ,��ɫ(��ͷ),��ɫ(����),��ɫ(ǽ,ƻ��)
	 * Ϊ��ͼλ�ø�ֵ
	 * */
	public void buildWall(){
		for(int i=0; i<xcount; i++)
		{
		   	setTile(GREEN_STAR,i,0);         //�������Ƕ�ǽ,����Щ�������ó�ǽ
		   	setTile(GREEN_STAR,i,ycount-1);  //������
		}
		for(int j=0; j<ycount; j++)
		{
			setTile(GREEN_STAR,0,j);        //��
			setTile(GREEN_STAR,xcount-1,j);
		}
	}
	
	//��Ҫ�ظ�����ͼ��,������Ҫ�õ��߳�,Android����Ҫ�õ�Handler
	//���õķ���������Handler��ʵ��UI�̵߳ĸ��µ�
	private MyHandler handler = new MyHandler();
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			update();
			
			/*
			 * invalidate()������ˢ��View�ģ���������UI�߳��н��й������������޸�ĳ��view����ʾʱ��
			 * ����invalidate()���ܿ������»��ƵĽ��档
			 * invalidate()�ĵ����ǰ�֮ǰ�ľɵ�view����UI�̶߳�����pop����
			 * */
			MyTile.this.invalidate();   //����ʵ���ػ�
		}
		public void sleep(int delay)
		{
			this.removeMessages(0);
			
			//sendMessageDelayed �ǽ�ĳ����Ҫ�������Ϣ�¼����͸�handler������
			//�����ڴ�֮ǰ���㴫��Ĳ����ӳ�һ����ʱ�䡣
            //����Ҫ��handler��ȥ������Ҫ���͵���Ϣ��
			sendMessageDelayed(obtainMessage(0), speed);
		}
	}
	
	/*---
	 * �ߵ��ƶ�����:
	 * ʵ�ֲ��ϵ��ػ�
	 * �ߵ�λ�ñ仯,�γ��߶�
	 * �ߵĳ��Ȳ��ϱ仯,��ArrayList
	 * Ҫ��¼�ߵ�λ����Ҫ�õ�һ��������Coordinate
	 *    private ArrayList<Coordinate>snakeTrail = new ArrayList<Coordinate>();
	 * */
	
	//����
	public void update(){
		clearTile();   //���map��ͼ����
  		buildWall();   //�齨ǽ
		updateSnake();
		updateApple();
		handler.sleep(speed);   //�ӳ�
	}
	
	//�����ߵ�����
	public void updateSnake()
	{
		textview.setText("����ǰ������:"+score);
		//�����ͷ
		Coordinate header = snakeList.get(0);
		Coordinate newHeader = new Coordinate(1,1);   //�µ���ͷ
		direction = nextdirection;
		switch(direction)
		{
		    case RIGHT:
		    	newHeader = new Coordinate(header.x+1,header.y);   //����������
		    	break;
		    case LEFT:
		    	newHeader = new Coordinate(header.x-1,header.y);   //����������
		    	break;
		    case UP:
		    	newHeader = new Coordinate(header.x,header.y-1);   //����������
		    	break;
		    case DOWN:
		    	newHeader = new Coordinate(header.x,header.y+1);   //����������
		    	break;
		}
		
		//�Ƿ�ײǽ
		if(newHeader.x<1||newHeader.y<1||newHeader.x>xcount-2||newHeader.y>ycount-2)
		{
			setMode(LOSE);
			return ;
		}
		
		//������Ƿ�Ե��Լ�
		for(Coordinate c: snakeList)
		{
			if(c.x==newHeader.x && c.y==newHeader.y)
			{
				setMode(LOSE);
				return ;
			}
		}
		
		
		boolean growSnake=false;
		//��ͷ�Ե�ƻ��
		if(newHeader.x==appleList.get(0).x && newHeader.y==appleList.get(0).y)  
		{
			score++;   //����+1
			appleList.remove(0);
			addRandomApple();
			speed *= 0.9;   //�ٶ�Խ��Խ��
			growSnake=true;
		}
		
		//���µ���ͷ��ӵ��ߵ�ArrayList<Coordinate>��
		snakeList.add(0,newHeader);
		if(!growSnake)   //���û�Ե�������ȥ�����һ����,�Ե������������һ��
		{
			//��ԭ�����ߵ�β������
			snakeList.remove(snakeList.size()-1);
		}
		
		//index���ߵľ�����һ��
		int index = 0;
		for (Coordinate c : snakeList) //����ȡ��ArrayList<Coordinate>�е�����
		{
			if (index == 0)   //��ͷ,���ƻ�ɫ
			{
				  setTile(YELLOW_STAR, c.x, c.y);
			} 
			else              //����,���ƺ�ɫ
			{
				setTile(RED_STAR, c.x, c.y);
			}
			index++;          //λ�����ߵĺ����ƶ�
		}
		
		
	}
	
	public void setTextView(TextView newView) {  
        textview = newView;
    }
	
	//������Ϸ����״̬
	public void setMode(int newMode)
	{ 
		int oldMode = mMode;
		mMode = newMode;
		
		if(newMode==LOSE)
		{
			textview.setText("���ķ�����");
			clearTile();   //�����ͼ
			handler.removeCallbacks(null);    //ֹͣhander
			initGame();   //��ʼ����Ϸ
		}
	}
	
	//��map����(��ͼ)����
	public void clearTile(){
		for(int i=0; i<xcount; i++)
		{
		    for(int j=0; j<ycount; j++)
		    {
		    	setTile(0,i,j);
		    }
		}
	}
	
	//��ʾ�����������
	private class Coordinate{
		public int x;
		public int y;
		public Coordinate(int x,int y)
		{
			super();
			this.x = x;
			this.y = y;
		}
		
		//��ͻ���,���Ƿ�Ե�ƻ��
		public boolean equals(Coordinate other)
		{
			if( this.x == other.x && this.y == other.y )
			{
				return true;
			}
			return false;
		}
	}

	//��ȡ�û�����İ���
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		//�û�ѡ��ķ���,�ı�direction����
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_UP:   //�û�����   ��   ��
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
			case KeyEvent.KEYCODE_BACK:    //�����ؼ�ֱ���˳�
				System.exit(0);
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//�������ƻ��
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
				if(newcoor.x == c.x && newcoor.y == c.y)  //������ƻ�������������ͻ
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
	
	//��ƻ����ͼ
	private void updateApple()
	{
		setTile(YELLOW_STAR,appleList.get(0).x,appleList.get(0).y);
	}
}
