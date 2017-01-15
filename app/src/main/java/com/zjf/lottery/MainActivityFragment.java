package com.zjf.lottery;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private int timer_period;
    private int timer_period_min = 5;
    private int timer_period_max = 2000;
    private double first_prize_timer_factor = 2;
    private double second_prize_timer_factor = 1.2;
    private double third_prize_timer_factor = 0.4;
    private boolean running;
    private NameList name_list;
    private java.util.Timer timer;
    private TextView prevprev;
    private TextView prev;
    private TextView current;
    private TextView next;
    private TextView nextnext;
    private TextView result;
    private RadioButton radio_first;
    private RadioButton radio_second;
    private RadioButton radio_third;
    private Button btn;
    private Handler handler;
    private MyTimerTask task;
    private int timer_msgid = 1;
    private double timer_delta = 1.2;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).RegisterFragment(this);
        name_list = new NameList();
        prevprev = (TextView) getView().findViewById(R.id.text_prevprev);
        prev = (TextView) getView().findViewById(R.id.text_prev);
        current = (TextView) getView().findViewById(R.id.text_current);
        next = (TextView) getView().findViewById(R.id.text_next);
        nextnext = (TextView) getView().findViewById(R.id.text_nextnext);
        result = (TextView) getView().findViewById(R.id.text_result);
        radio_first = (RadioButton) getView().findViewById(R.id.radio_first);
        radio_first.setChecked(true);
        radio_second = (RadioButton) getView().findViewById(R.id.radio_second);
        radio_second.setChecked(false);
        radio_third = (RadioButton) getView().findViewById(R.id.radio_third);
        radio_third.setChecked(false);
        btn = (Button) getView().findViewById(R.id.button_rock);
        btn.setText(R.string.button_rock_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    timer = new java.util.Timer();
                    timer_period = timer_period_min;
                    task = new MyTimerTask();
                    timer.schedule(task, 0, timer_period);
                    running = true;
                    btn.setText(R.string.button_rock_stop);
                    current.setBackgroundResource(R.drawable.border);
                    radio_first.setEnabled(false);
                    radio_second.setEnabled(false);
                    radio_third.setEnabled(false);
                } else {
                    running = false;
                    btn.setText(R.string.button_rock_start);
                    btn.setEnabled(false);
                }
            }
        });
        handler = new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what == timer_msgid) {
                    OnTimer();
                }
                super.handleMessage(msg);
            }

        };
        UpdateTextView();
    }
    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            Message message = new Message();
            message.what = timer_msgid;
            handler.sendMessage(message);
        }
    };

    void UpdateTextView() {
        ArrayList<NameList.Name> ls = name_list.GetNameList(5);
        if (ls.size() > 0) prevprev.setText(ls.get(0).GetValue());
        if (ls.size() > 1) prev.setText(ls.get(1).GetValue());
        if (ls.size() > 2) current.setText(ls.get(2).GetValue());
        if (ls.size() > 3) next.setText(ls.get(3).GetValue());
        if (ls.size() > 4) nextnext.setText(ls.get(4).GetValue());
    }
    void OnTimer() {
        try {
            if (running) {
                name_list.MoveForward(1);
                UpdateTextView();
            } else {
                double time_level = 1;

                if (radio_first.isChecked()) {
                    time_level = first_prize_timer_factor;
                } else if (radio_second.isChecked()) {
                    time_level = second_prize_timer_factor;
                } else if (radio_third.isChecked()) {
                    time_level = third_prize_timer_factor;
                }
                if (timer_period * timer_delta > timer_period_max * time_level) {
                    String winner_name = current.getText().toString();
                    Snackbar.make(getView(), "得奖者:"+ winner_name, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    timer.cancel();
                    btn.setEnabled(true);
                    radio_first.setEnabled(true);
                    radio_second.setEnabled(true);
                    radio_third.setEnabled(true);
                    current.setBackgroundResource(R.drawable.highlighted_border);
                    name_list.RemoveName(winner_name);
                    if (radio_first.isChecked()) {
                        name_list.AddFirstWinner(winner_name);
                    } else if (radio_second.isChecked()) {
                        name_list.AddSecondWinner(winner_name);
                    } else if (radio_third.isChecked()) {
                        name_list.AddThirdWinner(winner_name);
                    }
                } else {
                    name_list.MoveForward(1);
                    UpdateTextView();
                    timer.cancel();
                    timer_period *= timer_delta;
                    timer = new java.util.Timer();
                    task = new MyTimerTask();
                    timer.schedule(task, timer_period, timer_period);
                }
            }
        } catch (Exception e) {
            Log.e("zjf", e.getMessage());
        }
    }

    public void ShowResult(boolean flag) {
        int result_visible = flag ? View.VISIBLE : View.INVISIBLE;
        int rock_visible = flag ? View.INVISIBLE : View.VISIBLE;
        result.setVisibility(result_visible);
        prev.setVisibility(rock_visible);
        prevprev.setVisibility(rock_visible);
        next.setVisibility(rock_visible);
        nextnext.setVisibility(rock_visible);
        current.setVisibility(rock_visible);
        btn.setVisibility(rock_visible);
        radio_first.setVisibility(rock_visible);
        radio_second.setVisibility(rock_visible);
        radio_third.setVisibility(rock_visible);
        if (flag) {
            String result_text = new String();
            ArrayList<String> ls = name_list.GetFirstWinner();
            result_text += "一等奖一名(¥3000):\n";
            for (String s : ls) {
                result_text += s;
                result_text += "\n";
            }
            ls = name_list.GetSecondWinner();
            result_text += "二等奖二名(¥500):\n";
            for (String s : ls) {
                result_text += s;
                result_text += "\n";
            }
            ls = name_list.GetThirdWinner();
            result_text += "三等奖十名(¥100):\n";
            for (String s : ls) {
                result_text += s;
                result_text += "\n";
            }
            result.setText(result_text);
        }
    }
    public void OnBackPressed() {
        if (result.getVisibility() == View.VISIBLE) {
            ShowResult(false);
        }
    }
}