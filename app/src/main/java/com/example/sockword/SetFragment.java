package com.example.sockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;

public class SetFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SwitchButton switchButton;
    private Spinner spinnerDifficulty;
    private Spinner spinnerAllnum;
    private Spinner spinnerNewnum;
    private Spinner spinnerReviewnum;
    private ArrayAdapter<String>adapterDifficulty,adapterAllnum,adapterNewnum,adapterReviewnum;
    String[] difficulty = new String[]{"小学","初中","高中","四级","六级"};
    String[] allnum = new String[]{"2道","4道","6道","8道"};
    String[] newnum = new String[]{"10","30","50","100"};
    String[] reviewnum = new String[]{"10","30","50","100"};
    SharedPreferences.Editor editor = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.set_fragment_layout,null);
       init(view);
        return view;
    }

    private void init(View view) {
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        switchButton = (SwitchButton)view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);
        spinnerDifficulty = (Spinner) view.findViewById(R.id.spinner_difficulty);
        spinnerAllnum = (Spinner) view.findViewById(R.id.spinner_all_number);
        spinnerNewnum = (Spinner) view.findViewById(R.id.spinner_new_number);
        spinnerReviewnum = (Spinner) view.findViewById(R.id.spinner_review_number);
        adapterDifficulty = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item,difficulty);
        spinnerDifficulty.setAdapter(adapterDifficulty);
        setSpinnerItemSelectedByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("difficulty",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adapterAllnum = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item,allnum);
        spinnerAllnum.setAdapter(adapterAllnum);
        setSpinnerItemSelectedByValue(spinnerAllnum,sharedPreferences.getInt("allNum",2)+"道");
        this.spinnerAllnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                int i = Integer.parseInt(msg.substring(0,1));
                editor.putInt("allNum",i);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapterNewnum = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item,newnum);
        spinnerNewnum.setAdapter(adapterNewnum);
        setSpinnerItemSelectedByValue(spinnerNewnum,sharedPreferences.getString("newNum","10"));
        this.spinnerNewnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("newNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapterReviewnum = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item,reviewnum);
        spinnerReviewnum.setAdapter(adapterReviewnum);
        setSpinnerItemSelectedByValue(spinnerReviewnum,sharedPreferences.getString("reviewNum","10"));
        this.spinnerReviewnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("reviewNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        

    }

    public void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        int k = apsAdapter.getCount();
        for (int i = 0;i<k;i++)
        {
            if(value.equals(apsAdapter.getItem(i).toString()))
            {
                spinner.setSelection(i,true);//默认选中项
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        /**
         * 从数据库获取开关按钮的状态
         * */
        if (sharedPreferences.getBoolean("btnTf", false)) {
            switchButton.openSwitch();
        } else {
            switchButton.closeSwitch();
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.switch_btn)
        {
            if(switchButton.isSwitchOpen())
            {
                switchButton.closeSwitch();
                editor.putBoolean("btnTf",false);
            }else {
                switchButton.openSwitch();
                editor.putBoolean("btnTf",true);
            }
            editor.commit();

        }
    }
}
