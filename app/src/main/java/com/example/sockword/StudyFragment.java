package com.example.sockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.example.sockword.R;
import com.example.greendao.entity.greendao.DaoMaster;
import com.example.greendao.entity.greendao.DaoSession;
import com.example.greendao.entity.greendao.WisdomEntity;
import com.example.greendao.entity.greendao.WisdomEntityDao;

import java.util.List;
import java.util.Random;

public class StudyFragment extends Fragment {
    private TextView difficultyTv,   //学习的难度
    wisdomEnglish,     //英语名人名句
    wisdomChina,
    alreadyStudyText,  //已经学习的题目
    alreadyMasterdText,  //已经掌握的
    wrongText;       //答错题数
    private SharedPreferences sharedPreferences;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private WisdomEntityDao questionDao;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                              saveInstanceState){
        View view = inflater.inflate(R.layout.study_fragment_layout,null);
        //绑定布局文件
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        difficultyTv = (TextView) view.findViewById(R.id.difficulty_text);
        wisdomEnglish = (TextView) view.findViewById(R.id.wisdom_english);
        wisdomChina = (TextView) view.findViewById(R.id.wisdom_china);
        alreadyStudyText = (TextView) view.findViewById(R.id.already_study);
        alreadyMasterdText = (TextView) view.findViewById(R.id.already_mastered);
        wrongText = (TextView) view.findViewById(R.id.wrong_text);
        AssetsDatabaseManager.initManager(getActivity());//初始化
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        //获取管理对象，因为数据库需要通过管理对象才能够获取
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getWisdomEntityDao();  //获取数据
        return view;
    }
//界面显示的时候，执行的方法
    @Override
    public void onStart() {
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty","四级")+"英语");
        List<WisdomEntity> datas = questionDao.queryBuilder().list();//获取数据集合
        Random random = new Random();
        int i = random.nextInt(10);
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();  //设置文字
    }
    //设置文字
    private void setText()
    {
        alreadyMasterdText.setText(sharedPreferences.getInt("alreadyMasterd",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
}
