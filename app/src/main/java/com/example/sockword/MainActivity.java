package com.example.sockword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.example.greendao.entity.greendao.CET4Entity;
import com.example.greendao.entity.greendao.CET4EntityDao;
import com.example.greendao.entity.greendao.DaoMaster;
import com.example.greendao.entity.greendao.DaoSession;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SynthesizerListener, RadioGroup.OnCheckedChangeListener {
    //用来显示单词和音标的
    private TextView timeText,dateText,wordText,englishText;
    private ImageView playvioce;
    private String mMonth,mDate,mWay,mHours,mMinute;//用来显示时间
    private SpeechSynthesizer speechSynthesizer;//合成对象
    //锁屏
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private RadioGroup radioGroup;
    private RadioButton radio_one,radio_two,radio_three;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor = null;
    int j=0;   //记录答题数量
    List<Integer> list;//判断题的数目
    List<CET4Entity>datas;   //用于从数据库读出相应的词库
    int k;
    /**
     * 手指按下的点为x1,y1
     * 手指离开屏幕的点是x2,y2
     */
    float x1=0,y1=0;
    float x2=0,y2=0;
    private SQLiteDatabase db; //数据库
    private DaoMaster mDaoMaster,dbMaster;  //管理者
    private DaoSession mDaoSession,dbSession; //和数据库进行对话
    //对应的表，由Java代码生成的，对数据库内相应的表、操作使用此对象
    private CET4EntityDao questionDao,dbDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();
    }
    //初始化定义好的控件
    public void init()
    {
        //初始化轻量级数据库
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();      //数据库编辑器
        list = new ArrayList<Integer>();//初始化list
        //添加一个10以内的随机数，随机数据库获取的单词
        Random r = new Random();
        int i;
        while(list.size()<10)
        {
            i = r.nextInt(20);
            if(!list.contains(i))
            {
                list.add(i);
            }
        }
        /**
         * 得到键盘锁管理对象
         */
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");
        //初始化，只需要调用一次
        AssetsDatabaseManager.initManager(this);
        //获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("word.db");
        //对数据库进行操作
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();
        //参数为context，数据库名字，CUsorFactory；
        //这个DevOpenHelper继承自SQLiteOpenHelper
        DaoMaster.DevOpenHelper helper = new DaoMaster.
                DevOpenHelper(this,"wrong.db",null);
        //初始化数据库
        db = helper.getWritableDatabase();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();
        //控件初始化
        //用于显示分钟绑定id
        timeText = (TextView) findViewById(R.id.time_text);
        //用于显示日期绑定id
        dateText = (TextView) findViewById(R.id.date_text);
        //用于显示单词绑定id
        wordText = (TextView) findViewById(R.id.word_text);
        //显示音标绑定id
        englishText = (TextView) findViewById(R.id.english_text);
        //用于播放单词的按钮绑定id
        playvioce = (ImageView) findViewById(R.id.play_vioce);
        //给播放单词按钮进行监听
        playvioce.setOnClickListener(this);
        //给加载单词三个选项绑定id
        radioGroup = (RadioGroup) findViewById(R.id.groups);
        //给第一个选项绑定id
        radio_one = (RadioButton) findViewById(R.id.choose_button_one);
        //给第二个选项绑定id
        radio_two = (RadioButton) findViewById(R.id.choose_button_two);
        //给第三个选项绑定id
        radio_three = (RadioButton) findViewById(R.id.choose_button_three);

        //给加载单词三个选项设置监听事件
        radioGroup.setOnCheckedChangeListener(this);

        setParam();//初始化播放语音
        //appid换成自己申请的，播放语音
        SpeechUser.getUser().login(MainActivity.this, null, null,
                "appid=5606ca73", listener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //获得系统时间，并显示出来
        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        //系统当中，月份为0-11，因此加一
        mDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        //小时
        if(calendar.get(Calendar.HOUR) < 10){
            mHours = "0" + calendar.get(Calendar.HOUR);
        }else{
            mHours = String.valueOf(calendar.get(Calendar.HOUR));
        }
        //分钟

        if(calendar.get(Calendar.MINUTE)<10){
            mMinute = "0"+calendar.get(Calendar.MINUTE);
        }else{
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }
        //星期
        if("1".equals(mWay))
        {
            mWay = "天";
        }else if("2".equals(mWay))
        {
            mWay = "一";
        }
        else if("3".equals(mWay))
        {
            mWay = "二";
        }
        else if("4".equals(mWay))
        {
            mWay = "三";
        }
        else if("5".equals(mWay))
        {
            mWay = "四";
        }
        else if("6".equals(mWay))
        {
            mWay = "五";
        }
        else if("7".equals(mWay))
        {
            mWay = "六";
        }
        timeText.setText(mHours + ":" + mMinute);
        dateText.setText(mMonth + "月" + mDate +"日"+"  "+"星期"+mWay);
        getDBData();
        BaseApplication.addDestroyActivity(this,"mainActivity");
    }

    private void saveWrongData() {
        String word = datas.get(k).getWord();         //获取答错这道题的单词
        String english = datas.get(k).getEnglish();  //获取答错这道题的音标
        String china = datas.get(k).getChina();       //获取答错这道题的汉语意思
        String sign = datas.get(k).getSign();         //获取答错这道题的标记
        CET4Entity data = new CET4Entity(Long.valueOf(dbDao.count()),
                word, english, china, sign);
        dbDao.insertOrReplace(data);                   //把这些字段存到数据库
    }



    /**
     * 设置选项的不同颜色
     */
    private void btnGetText(String msg,RadioButton btn)
    {
        //选中按钮和我们数据库一致，也就是选对的时候
        if(msg.equals(datas.get(k).getChina()))
        {
            wordText.setTextColor(Color.GREEN);
            englishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
        }
        //选错的时候
        else {
            wordText.setTextColor(Color.RED);
            englishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);
            saveWrongData();
            int wrong = sharedPreferences.getInt("wrong",0);
            editor.putInt("wrong",wrong+1);
            editor.putString("wrongId", "," + datas.get(j).getId());
            editor.commit();
        }
    }
    @Override
    public void onClick(View v) {
            if (v.getId()== R.id.play_vioce){
                String text = wordText.getText().toString();
                speechSynthesizer.startSpeaking(text,this);
        }
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }
    private SpeechListener listener = new SpeechListener() {
        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onData(byte[] bytes) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }
    };
    //初始化语音播报
    public void setParam()
    {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED,"50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME,"50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH,"50");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        radioGroup.setClickable(false);
            if(checkedId== R.id.choose_button_one){
                String msg = radio_one.getText().toString().substring(3);
                btnGetText(msg,radio_one);
                }
        if(checkedId== R.id.choose_button_two){
                String msg1 = radio_two.getText().toString().substring(3);
                btnGetText(msg1,radio_two);}

        if(checkedId== R.id.choose_button_three){
                String msg2 = radio_three.getText().toString().substring(3);
                btnGetText(msg2,radio_three);
               }
        }

        //滑动下一题执行方法，改变文本颜色
    private void setTextColor()
    {
        radio_one.setChecked(false);
        radio_two.setChecked(false);
        radio_three.setChecked(false);
        //设置为白色
        radio_one.setTextColor(Color.parseColor("#FFFFFF"));
        radio_two.setTextColor(Color.parseColor("#FFFFFF"));
        radio_three.setTextColor(Color.parseColor("#FFFFFF"));
        wordText.setTextColor(Color.parseColor("#FFFFFF"));  //单词设置为白色
        englishText.setTextColor(Color.parseColor("#FFFFFF"));  //音标设置为白色
    }
    private void unlock()
    {
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);//添加类别
        startActivity(intent1);
        kl.disableKeyguard();//解锁
        finish();
    }
    //读取数据库文件
    private void setChina(List<CET4Entity> datas,int j)
    {
        //生成随机数，解锁单词
        Random r = new Random();
        List<Integer>listInt = new ArrayList<>();
        int i;
        while(listInt.size()<4)
        {
            i = r.nextInt(20);
            if(!listInt.contains(i))
            {
                listInt.add(i);
            }
        }
        //以下是给单词设置三个选项，设置单词选项是有规律的
        //三个选项分别是正确的，正确的前一个，后一个
        //将三个解释设置到单词的选项上，以下为实现逻辑
        if(listInt.get(0)<7)
        {
            radio_one.setText("A:"+datas.get(k).getChina());
            if(k-1>=0)
            {
                radio_two.setText("B"+datas.get(k-1).getChina());
            }else{
                radio_two.setText("B:"+datas.get(k+2).getChina());
            }
            if(k+1<20)
            {
                radio_three.setText("C"+datas.get(k+1).getChina());
            }else{
                radio_three.setText("C:"+datas.get(k-1).getChina());
            }
        } else if (listInt.get(0)<14) {
            radio_two.setText("B:"+datas.get(k).getChina());
            if(k-1>=0)
            {
                radio_one.setText("A"+datas.get(k-1).getChina());
            }else{
                radio_one.setText("A:"+datas.get(k+2).getChina());
            }
            if(k+1<20)
            {
                radio_three.setText("C"+datas.get(k+1).getChina());
            }else{
                radio_three.setText("C:"+datas.get(k-1).getChina());
            }
        }else {
            radio_three.setText("C: " + datas.get(k).getChina());
            if (k - 1 >= 0) {
                radio_two.setText("B: " + datas.get(k - 1).getChina());
            } else {
                radio_two.setText("B: " + datas.get(k + 2).getChina());
            }
            if (k + 1 < 20) {
                radio_one.setText("A: " + datas.get(k + 1).getChina());
            } else {
                radio_one.setText("A: " + datas.get(k - 1).getChina());
            }
        }

        }

    private void getDBData()
    {
        datas = questionDao.queryBuilder().list();//把词库中的单词读取出来
        k = list.get(j);
        wordText.setText(datas.get(k).getWord());//设置单词
        englishText.setText(datas.get(k).getEnglish());//设置音标
        setChina(datas,k);   //设置三个选项
    }

    //手势滑动事件

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            //手指按下的时候的坐标
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            //手指离开的时候的坐标
            x2 = event.getX();
            y2 = event.getY();
            if((y1-y2)>200)  //向上滑
            {
                int num = sharedPreferences.getInt("alreadyMasterd",0)+1;
                editor.putInt("alreadyMasterd",num);
                editor.commit();
                Toast.makeText(this,"已掌握",Toast.LENGTH_SHORT).show();
                getNextData();
            }else if((y2-y1)>200)  //向下滑
            {
                Toast.makeText(this,"待加功能....",Toast.LENGTH_SHORT).show();
            }
            else if((x1-x2)>200)  //向左滑
            {
                getNextData();
            }
            else if((x2-x1)>200)  //向右滑
            {
                unlock();
            }
        }
        return super.onTouchEvent(event);
    }
    private void getNextData()
    {
        j++;  //当前已做题的数目
        int i = sharedPreferences.getInt("allNum",2);  //默认解锁数目为两道
        if(i>j)       //判断设定的解锁题目与当前已做题数目的关系
        {
            getDBData();
            setTextColor();
            int num = sharedPreferences.getInt("alreadyStudy",0)+1;
            editor.putInt("alreadyStudy",num);
            editor.commit();
        }else {
            unlock();
        }
    }
}