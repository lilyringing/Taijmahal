package com.example.taijmahal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Message;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Chronometer;
import android.widget.Toast;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
	Handler handler;
	TableLayout mainTable;
	Drawable backImage;
	ButtonListener buttonListener;
	Context context;
	Chronometer chronometer;
	ArrayList<Drawable> images;
	int cards[][];
	int rowCount = 4;
	int columeCount = 4;
	int pairCount;
	Card firstCard;
	Card secondCard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new UpdateCardsHandler();
		loadImages();
		setContentView(R.layout.activity_main);
		
		backImage = getResources().getDrawable(R.drawable.empty);
		buttonListener = new ButtonListener();
		mainTable = (TableLayout) findViewById(R.id.MyTableLayout);
		context = mainTable.getContext();
		
		chronometer = (Chronometer) findViewById(R.id.MyChronometer);
		chronometer.setFormat("Game time: %s");
		
		initilizeGame();
	}
	
	private void loadImages(){
		images = new ArrayList<Drawable>();
		
		images.add(getResources().getDrawable(R.drawable.item01));
		images.add(getResources().getDrawable(R.drawable.item02));
		images.add(getResources().getDrawable(R.drawable.item03));
		images.add(getResources().getDrawable(R.drawable.item04));
		images.add(getResources().getDrawable(R.drawable.item05));
		images.add(getResources().getDrawable(R.drawable.item06));
		images.add(getResources().getDrawable(R.drawable.item07));
		images.add(getResources().getDrawable(R.drawable.item08));
		
	}
	
	private void initilizeGame(){
		cards = new int[columeCount][rowCount];
		int items = (rowCount * columeCount) / 2;
		
		mainTable.removeAllViews();
		
		for(int y=0; y < rowCount; y++){
			mainTable.addView(createRow(y));
		}
		
		firstCard = null;
		loadCards();
		pairCount = 0;
		
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
	}
	
	private TableRow createRow(int y){
		TableRow row = new TableRow(context);
		row.setHorizontalGravity(Gravity.CENTER);
		
		for(int x=0; x < columeCount; x++){
			row.addView(createImageButton(x, y));
		}
		return row;
	}
	
	private View createImageButton(int x, int y){
		Button button = new Button(context);
		button.setBackgroundDrawable(backImage);
		button.setId(100* x + y);
		button.setOnClickListener(buttonListener);
		return button;
	}
	
	private void loadCards(){
		try{
			int size = rowCount * columeCount;
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			for(int i=0; i < size; i++){
				list.add(new Integer(i));
			}
			Random r = new Random();
			
			for(int i = size -1; i >= 0; i--){
				int t = 0;
				
				if(i > 0){
					t = r.nextInt(i);
				}
				
				t = list.remove(t).intValue();
				cards[i % columeCount][i / columeCount] = t % (size /2); 
			}
			
		}catch(Exception e){
			Log.e("loadCards()", e + "");
		}
	}
	
	class ButtonListener implements OnClickListener{
		public void onClick(View v){
			//sychronized(lock){
				if(firstCard != null && secondCard != null){
					return;
				}
				int id = v.getId();
				int x = id / 100;
				int y = id % 100;
				turnCard((Button) v, x, y);
			//}
		}
		
		private void turnCard(Button button, int x, int y){
			button.setBackgroundDrawable(images.get(cards[x][y]));
			if(firstCard == null){
				firstCard = new Card(button, x, y);
			}else{
				if(firstCard.x == x && firstCard.y == y){
					return;
				}
				secondCard = new Card(button, x, y);
				
				TimerTask tt = new TimerTask(){
					@Override
					public void run(){
						try{
							//synchronized(lock){
								handler.sendEmptyMessage(0);
							//}
						}catch(Exception e){
							Log.e("E1", e.getMessage());
						}
					}
				};
				
				Timer t = new Timer(false);
				t.schedule(tt, 500);
			}
		}
	}
	
	class UpdateCardsHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			//sychronized(lock){
				checkCards();
			//}
		}
		
		public void checkCards(){
			if(cards[secondCard.x][secondCard.y] == cards[firstCard.x][firstCard.y]){
				firstCard.button.setEnabled(false);
				secondCard.button.setEnabled(false);
				Toast.makeText(getApplicationContext(), "Successfully match!", Toast.LENGTH_SHORT).show();
				pairCount++;
			}else{
				firstCard.button.setBackgroundDrawable(backImage);
				secondCard.button.setBackgroundDrawable(backImage);
				Toast.makeText(getApplicationContext(), "Fail to match!", Toast.LENGTH_SHORT).show();
			}
			firstCard = null;
			secondCard = null;
		}
	}	
}




