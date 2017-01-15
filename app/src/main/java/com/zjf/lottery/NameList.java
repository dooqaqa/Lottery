package com.zjf.lottery;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by zjf on 2017/1/15.
 */

public class NameList {
    public class Name
    {
        public Name(String val) {value = val;}
        public void SetNext(Name element) {next = element;}
        public String GetValue() {return value;}
        public Name GetNext() {return next;}
        private String value=null;
        private Name next=null;
    }
    int first_prize_num = 1;
    int second_prize_num = 2;
    int third_prize_num = 10;
    private ArrayList<String> first_prize_winner = new ArrayList<String>();
    private ArrayList<String> second_prize_winner = new ArrayList<String>();
    private ArrayList<String> third_prize_winner = new ArrayList<String>();
    private Name header = null;
    public NameList() {
        Init();
    }
    public void Init() {
        ArrayList<String> ls = new ArrayList<String>();
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/auto_annual_party", "name_list.json");
            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String result = new String(buffer);

            JSONObject json = new JSONObject(result);
            JSONArray array = json.getJSONArray("name_list");
            for (int i = 0; i < array.length(); i++) {
                ls.add(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ls.isEmpty()) return;

        header = new Name(ls.remove(0));
        Name curr = header;
        while (!ls.isEmpty()){
            curr.SetNext(new Name(ls.remove(0)));
            curr = curr.GetNext();
        }
        curr.SetNext(header);
    }
    public ArrayList<Name> GetNameList(int num) {
        ArrayList<Name> arr = new ArrayList<Name>();
        int count = 0;
        Name n = header.GetNext();
        while (count < num && n != null) {
            arr.add(n);
            n = n.GetNext();
            if (n == header) break;
            ++count;
        }
        return  arr;
    }
    public void MoveForward(int step) {
        for (int i = 0; i < step; ++i) {
            header = header.GetNext();
        }
    }
    public void RemoveName(String name) {
        Name n = header;
        while (n != null) {
            if (n.GetNext().GetValue() == name) {
                n.SetNext(n.GetNext().GetNext());
                break;
            }
            n = n.GetNext();
            if (n == header) break;
        }
    }
    public void AddFirstWinner(String name) {
        if (first_prize_winner.size() < first_prize_num) {
            first_prize_winner.add(name);
        }
    }
    public ArrayList<String> GetFirstWinner() {
        return first_prize_winner;
    }
    public void AddSecondWinner(String name) {
        if (second_prize_winner.size() < second_prize_num) {
            second_prize_winner.add(name);
        }
    }
    public ArrayList<String> GetSecondWinner() {
        return second_prize_winner;
    }
    public void AddThirdWinner(String name) {
        if (third_prize_winner.size() < third_prize_num) {
            third_prize_winner.add(name);
        }
    }
    public ArrayList<String> GetThirdWinner() {
        return third_prize_winner;
    }

}