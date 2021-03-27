package com.agenda;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarioAdapter extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK = 0;

    private Context mContext;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> itens;

    public CalendarioAdapter(Context c, Calendar monthCalendar) {
        month = monthCalendar;
        selectedDate = (Calendar)monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.itens = new ArrayList<String>();
        refreshDays();
    }

    public void setItens(ArrayList<String> itens) {
        for(int i = 0;i != itens.size();i++){
            if(itens.get(i).length()==1) {
                itens.set(i, "0" + itens.get(i));
            }
        }
        this.itens = itens;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView dayView;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.adapter_item_calendario, null);
        }

        dayView = (TextView)v.findViewById(R.id.date);

        if(days[position].equals("")) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
        }
        else {

            if(month.get(Calendar.YEAR)== selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)== selectedDate.get(Calendar.MONTH) && days[position].equals(""+selectedDate.get(Calendar.DAY_OF_MONTH))) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.light_yellow));
            }
            else {
                v.setBackgroundColor(Color.WHITE);
            }
        }
        dayView.setText(days[position]);

        String date = days[position];

        if(date.length()==1) {
            date = "0" + date;
        }

        String monthStr = "" + (month.get(Calendar.MONTH)+1);

        if(monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        ImageView iw = (ImageView)v.findViewById(R.id.date_icon);
        if(date.length()>0 && itens!=null && itens.contains(date)) {
            iw.setVisibility(View.VISIBLE);
        }
        else {
            iw.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    public void refreshDays() {

       /* itens.clear();*/

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = (int)month.get(Calendar.DAY_OF_WEEK);

        if(firstDay==1){
            days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }

        int j = FIRST_DAY_OF_WEEK;

        if(firstDay>1) {
            for(j=0;j<firstDay-FIRST_DAY_OF_WEEK;j++) {
                days[j] = "";
            }
        }
        else {
            for(j=0;j<FIRST_DAY_OF_WEEK*6;j++) {
                days[j] = "";
            }
            j=FIRST_DAY_OF_WEEK*6+1;
        }

        int dayNumber = 1;
        for(int i=j-1;i<days.length;i++) {
            days[i] = ""+dayNumber;
            dayNumber++;
        }
    }

    public String[] days;
}
