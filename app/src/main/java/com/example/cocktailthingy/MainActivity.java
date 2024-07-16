package com.example.cocktailthingy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    LinearLayout graphBox;
    ScrollView layout;
    String TAG = "Logger";
    private LineChart chart;
    String blank;
    TextView greeting, secondaryTextGraph, moodChooseState;
    private RelativeLayout button1, button2, button3, button4, button5;
    private ArrayList<Entry> values;
    YAxis yAxis;
    XAxis xAxis;
    ImageView avatarImg;
    Bitmap avatarImage;
    static LocalDateTime currentTimeDate;
    static LocalDateTime nextTimeDate;
    static LocalDateTime clickTimeDate;
    static LocalDateTime lastWeekTimeDate;
    static LocalDateTime lastTimeDate;
    String stringTimeDate;
    static String[] timeDateArray1;
    static String[] timeDateArray2;
    static ArrayList<String> currentTimeDateArray;
    static ArrayList<String> nextTimeDateArray;
    static ArrayList<String> clickTimeDateArray;
    static ArrayList<String> lastWeekTimeDateArray;
    static ArrayList<String> lastTimeDateArray;
    static String currentTimeDateString;
    static String nextTimeDateString;
    static String clickTimeDateString;
    static String lastTimeDateString;
    static String lastWeekTimeDateString;
    static String lastClickMonth;
    static String lastWeekMonth;
    static String lastClickDate;
    static String lastWeekDate;
    String partOfDay;
    String nextTimeClick;
    String username;
    ArrayList<Entry> retrievedFromPrefs;
    Boolean chooseMood;

    //TODO: make the buttons pushable only once a day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();
        graphBox = findViewById(R.id.graphBox); //Setting up IDs
        layout = findViewById(R.id.layout);
        chart = findViewById(R.id.chart1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        greeting = findViewById(R.id.welcomeText);
        secondaryTextGraph = findViewById(R.id.secondaryTextGraph);
        moodChooseState = findViewById(R.id.moodChooseState);
        lastTimeDateArray = new ArrayList<>();
        lastWeekTimeDateArray = new ArrayList<>();



        currentTimeDate = LocalDateTime.now(); //Getting current time
        nextTimeDate = currentTimeDate.plusDays(1); //Getting current time
        Log.i(TAG, "onCreate: TIME: " + currentTimeDate);
        currentTimeDateArray = getTimeDateArray(currentTimeDate);
        nextTimeClick = LoginActivity.settings.getString("nextTime", "default");
        lastTimeDateString = LoginActivity.settings.getString("clickTime", "default");
        Log.i(TAG, "onCreate: " + lastTimeDateString);
        if(!lastTimeDateString.equals("default")){
            lastTimeDate = LocalDateTime.parse(lastTimeDateString);
            Log.i(TAG, "onCreate: LASTTIMEDATE " + lastTimeDate);
            lastWeekTimeDate = lastTimeDate.minusWeeks(1);
            lastTimeDateArray.addAll(getTimeDateArray(lastTimeDate));

            lastWeekTimeDateArray.addAll(getTimeDateArray(lastWeekTimeDate));
            lastClickMonth = getMonth(Integer.parseInt(lastTimeDateArray.get(1)));
            lastWeekMonth = getMonth(Integer.parseInt(lastWeekTimeDateArray.get(1)));
            lastClickDate = lastTimeDateArray.get(2);
            lastWeekDate = lastWeekTimeDateArray.get(2);
            secondaryTextGraph.setText(lastWeekDate + " " + lastWeekMonth + " - " + lastClickDate + " " + lastClickMonth);
        }
        updateMoodStateString(nextTimeClick);

        //stringTimeDate = timeDate.toString();
        //timeDateArray1 = stringTimeDate.split("T"); //Setting current year, month and so on from timeDate
        //for (int i = 0; i < timeDateArray1.length; i++){
        //    if(i == 0){
        //        timeDateArray2 = timeDateArray1[i].split("-");
        //        currentYear = timeDateArray2[0];
        //        currentMonth = timeDateArray2[1];
        //        currentDay = timeDateArray2[2];
        //    } else if (i == 1){
        //        timeDateArray2 = timeDateArray1[i].split(":");
        //        currentHour = timeDateArray2[0];
        //        currentMinute = timeDateArray2[1];
        //    }
        //}
        Log.i(TAG, "onCreate: YEAR: " + currentTimeDateArray.get(0) + " MONTH: " + currentTimeDateArray.get(1) + "DAY: " + currentTimeDateArray.get(2) + " HOUR: " + currentTimeDateArray.get(3) + " MINUTE: " + currentTimeDateArray.get(4));
        int currentHourInt = Integer.parseInt(currentTimeDateArray.get(3));
        if(currentHourInt >= 5 && currentHourInt < 12){
            partOfDay = "morning";
        } else if(currentHourInt >= 12 && currentHourInt < 17){
            partOfDay = "afternoon";
        } else if(currentHourInt >= 17 && currentHourInt < 21){
            partOfDay = "evening";
        } else if(currentHourInt == 21 || currentHourInt == 22 || currentHourInt == 23 || currentHourInt == 24 || currentHourInt == 0 || currentHourInt == 1 || currentHourInt == 2 || currentHourInt == 3 || currentHourInt == 4){
            partOfDay = "night";
        }
        username = LoginActivity.settings.getString("username", "default");
        greeting.setText("Good " + partOfDay + ", " + username);
        //TODO: load the preferences on start.
        LoginActivity.editor = LoginActivity.settings.edit();
        avatarImg = findViewById(R.id.avatarImg); //Loading the avatar
        Log.i(TAG, "onCreate: " + LoginActivity.settings.getString("avatar", "default"));
        avatarImage = StringToBitMap(LoginActivity.settings.getString("avatar", "default"));
        avatarImg.setImageBitmap(avatarImage);

        yAxis = chart.getAxisLeft();
        blank = "";
        values = new ArrayList<>();
        try {
            retrievedFromPrefs = arrayListFromPrefs("weekValues");
            Log.i(TAG, "onCreate: retrievedFromPrefs: " + true);
            values.addAll(retrievedFromPrefs);
        } catch (Exception e){
            e.printStackTrace();
            values.add(new Entry(1, 1)); //Default values for the first run
            values.add(new Entry(2, 1));
            values.add(new Entry(3, 1));
            values.add(new Entry(4, 1));
            values.add(new Entry(5, 1));
            values.add(new Entry(6, 1));
            values.add(new Entry(7, 1));
        }




        chart.setViewPortOffsets(0, 0, 0, 0); //Graph stuff, this is a really long thing to explain
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        xAxis = chart.getXAxis();
        xAxis.setLabelCount(7);
        yAxis.setAxisMinimum(1);
        yAxis.setAxisMaximum(6);
        yAxis.setGranularityEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setText("");
        chart.setMaxHighlightDistance(300);
        XAxis x = chart.getXAxis();
        x.setEnabled(false);
        YAxis y = chart.getAxisLeft();
        y.setLabelCount(5, true);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        chart.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_container));
        chart.invalidate();
        setData(7, 5);

        button1.setOnClickListener(new View.OnClickListener() { //Each one of the onClickListeners is bound to an emotion button
            @Override
            public void onClick(View view) {
                currentTimeDate = LocalDateTime.now();
                Log.i(TAG, "onClick: NEXT TIME CLICK: " + nextTimeClick);
                try {
                    chooseMood = currentTimeDate.isAfter(LocalDateTime.parse(LoginActivity.settings.getString("nextTime", "default")));
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(chooseMood){
                    Log.i(TAG, "onClick: BUTTON 1 PRESSED");
                    int size = values.size();
                    if (size < 7) {
                        values.add(new Entry(size, 1));
                    } else {
                        // Shift elements to the left, starting from the second element
                        for (int i = 1; i < 7; i++) {
                            values.set(i - 1, new Entry(i, values.get(i).getY()));
                        }
                        // Add the new element at the end
                        values.set(6, new Entry(7, 1));
                    }
                    clickTimeDate = LocalDateTime.now();
                    nextTimeDate = clickTimeDate.plusDays(1);
                    clickTimeDateArray = getTimeDateArray(clickTimeDate);
                    nextTimeDateArray = getTimeDateArray(nextTimeDate);
                    clickTimeDateString = arrayListToLocalDateTimeString(clickTimeDateArray);
                    nextTimeDateString = arrayListToLocalDateTimeString(nextTimeDateArray);
                    Log.i(TAG, "onClick: nextTimeDateString: " + nextTimeDateString);
                    LoginActivity.editor.putString("clickTime", clickTimeDateString);
                    LoginActivity.editor.putString("nextTime", nextTimeDateString);
                    LoginActivity.editor.apply();
                    updateMoodStateString(nextTimeDateString);
                    chart.notifyDataSetChanged();
                    chart.invalidate();
                    setData(values.size(), 5);
                    for (int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onClick: " + values.get(i));
                    }
                    Log.i(TAG, "onClick: ARRAY SIZE" + values.size());
                    for(int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
                    }
                    arrayListToPrefs("weekValues", values);
                    Log.i(TAG, "onDestroy: HAPPENED " + true);
                }


            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeDate = LocalDateTime.now();
                Log.i(TAG, "onClick: NEXT TIME CLICK: " + nextTimeClick);
                try {
                    chooseMood = currentTimeDate.isAfter(LocalDateTime.parse(LoginActivity.settings.getString("nextTime", "default")));
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(chooseMood){
                    Log.i(TAG, "onClick: BUTTON 2 PRESSED");
                    int size = values.size();
                    if (size < 7) {
                        values.add(new Entry(size, 2));
                    } else {
                        // Shift elements to the left, starting from the second element
                        for (int i = 1; i < 7; i++) {
                            values.set(i - 1, new Entry(i, values.get(i).getY()));
                        }
                        // Add the new element at the end
                        values.set(6, new Entry(7, 2));
                    }
                    clickTimeDate = LocalDateTime.now();
                    nextTimeDate = clickTimeDate.plusDays(1);
                    clickTimeDateArray = getTimeDateArray(clickTimeDate);
                    nextTimeDateArray = getTimeDateArray(nextTimeDate);
                    clickTimeDateString = arrayListToLocalDateTimeString(clickTimeDateArray);
                    nextTimeDateString = arrayListToLocalDateTimeString(nextTimeDateArray);
                    Log.i(TAG, "onClick: nextTimeDateString: " + nextTimeDateString);
                    LoginActivity.editor.putString("clickTime", clickTimeDateString);
                    LoginActivity.editor.putString("nextTime", nextTimeDateString);
                    LoginActivity.editor.apply();
                    updateMoodStateString(nextTimeDateString);
                    chart.notifyDataSetChanged();
                    chart.invalidate();
                    chart.notifyDataSetChanged();
                    setData(values.size(), 5);
                    for (int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onClick: " + values.get(i));
                    }
                    for(int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
                    }
                    arrayListToPrefs("weekValues", values);
                    Log.i(TAG, "onDestroy: HAPPENED " + true);
                }

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeDate = LocalDateTime.now();
                Log.i(TAG, "onClick: NEXT TIME CLICK: " + nextTimeClick);
                try {
                    chooseMood = currentTimeDate.isAfter(LocalDateTime.parse(LoginActivity.settings.getString("nextTime", "default")));
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(chooseMood){
                    Log.i(TAG, "onClick: BUTTON 3 PRESSED");
                    int size = values.size();
                    if (size < 7) {
                        values.add(new Entry(size, 3));
                    } else {
                        // Shift elements to the left, starting from the second element
                        for (int i = 1; i < 7; i++) {
                            values.set(i - 1, new Entry(i, values.get(i).getY()));
                        }
                        // Add the new element at the end
                        values.set(6, new Entry(7, 3));
                    }
                    clickTimeDate = LocalDateTime.now();
                    nextTimeDate = clickTimeDate.plusDays(1);
                    clickTimeDateArray = getTimeDateArray(clickTimeDate);
                    nextTimeDateArray = getTimeDateArray(nextTimeDate);
                    clickTimeDateString = arrayListToLocalDateTimeString(clickTimeDateArray);
                    nextTimeDateString = arrayListToLocalDateTimeString(nextTimeDateArray);
                    Log.i(TAG, "onClick: nextTimeDateString: " + nextTimeDateString);
                    LoginActivity.editor.putString("clickTime", clickTimeDateString);
                    LoginActivity.editor.putString("nextTime", nextTimeDateString);
                    LoginActivity.editor.apply();
                    updateMoodStateString(nextTimeDateString);
                    chart.invalidate();
                    setData(values.size(), 5);
                    for (int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onClick: " + values.get(i));
                    }
                    for(int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
                    }
                    arrayListToPrefs("weekValues", values);
                    Log.i(TAG, "onDestroy: HAPPENED " + true);
                }

            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeDate = LocalDateTime.now();
                Log.i(TAG, "onClick: NEXT TIME CLICK: " + nextTimeClick);
                try {
                    chooseMood = currentTimeDate.isAfter(LocalDateTime.parse(LoginActivity.settings.getString("nextTime", "default")));
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(chooseMood){
                    Log.i(TAG, "onClick: BUTTON 4 PRESSED");
                    int size = values.size();
                    if (size < 7) {
                        values.add(new Entry(size, 4));
                    } else {
                        // Shift elements to the left, starting from the second element
                        for (int i = 1; i < 7; i++) {
                            values.set(i - 1, new Entry(i, values.get(i).getY()));
                        }
                        // Add the new element at the end
                        values.set(6, new Entry(7, 4));
                    }
                    clickTimeDate = LocalDateTime.now();
                    nextTimeDate = clickTimeDate.plusDays(1);
                    clickTimeDateArray = getTimeDateArray(clickTimeDate);
                    nextTimeDateArray = getTimeDateArray(nextTimeDate);
                    clickTimeDateString = arrayListToLocalDateTimeString(clickTimeDateArray);
                    nextTimeDateString = arrayListToLocalDateTimeString(nextTimeDateArray);
                    Log.i(TAG, "onClick: nextTimeDateString: " + nextTimeDateString);
                    LoginActivity.editor.putString("clickTime", clickTimeDateString);
                    LoginActivity.editor.putString("nextTime", nextTimeDateString);
                    LoginActivity.editor.apply();
                    updateMoodStateString(nextTimeDateString);
                    chart.invalidate();
                    setData(values.size(), 5);
                    for (int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onClick: " + values.get(i));
                    }
                    for(int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
                    }
                    arrayListToPrefs("weekValues", values);
                    Log.i(TAG, "onDestroy: HAPPENED " + true);
                }

            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeDate = LocalDateTime.now();
                Log.i(TAG, "onClick: NEXT TIME CLICK: " + nextTimeClick);
                try {
                    chooseMood = currentTimeDate.isAfter(LocalDateTime.parse(LoginActivity.settings.getString("nextTime", "default")));
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(chooseMood){
                    Log.i(TAG, "onClick: BUTTON 5 PRESSED");
                    int size = values.size();
                    if (size < 7) {
                        values.add(new Entry(size, 5));
                    } else {
                        // Shift elements to the left, starting from the second element
                        for (int i = 1; i < 7; i++) {
                            values.set(i - 1, new Entry(i, values.get(i).getY()));
                        }
                        // Add the new element at the end
                        values.set(6, new Entry(7, 5));
                    }
                    clickTimeDate = LocalDateTime.now();
                    nextTimeDate = clickTimeDate.plusDays(1);
                    clickTimeDateArray = getTimeDateArray(clickTimeDate);
                    nextTimeDateArray = getTimeDateArray(nextTimeDate);
                    clickTimeDateString = arrayListToLocalDateTimeString(clickTimeDateArray);
                    nextTimeDateString = arrayListToLocalDateTimeString(nextTimeDateArray);
                    Log.i(TAG, "onClick: nextTimeDateString: " + nextTimeDateString);
                    LoginActivity.editor.putString("clickTime", clickTimeDateString);
                    LoginActivity.editor.putString("nextTime", nextTimeDateString);
                    LoginActivity.editor.apply();
                    updateMoodStateString(nextTimeDateString);
                    chart.invalidate();
                    setData(values.size(), 5);
                    for (int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onClick: " + values.get(i));
                    }
                    for(int i = 0; i < values.size(); i++){
                        Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
                    }
                    arrayListToPrefs("weekValues", values);
                    Log.i(TAG, "onDestroy: HAPPENED " + true);
                }

            }
        });


        //layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        //    @Override
        //    public void onGlobalLayout() {
        //        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        //        int result = (layout.getWidth() - graphBox.getWidth()) / 2;
        //        Log.i(TAG, "onCreate: WIDTH RESULT: " + result);
        //        Log.i(TAG, "onCreate: WIDTH LAYOUT: " + layout.getWidth());
        //        Log.i(TAG, "onCreate: WIDTH GRAPH: " + graphBox.getWidth());
        //    }
        //});


    }

    private void setData(int count, float range) { //Graph stuff, again, too long to explain
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.3f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(true);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.rgb(118, 238, 198));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.rgb(118, 238, 198));
            set1.setFillColor(Color.rgb(118, 238, 198));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

    public boolean arrayListToPrefs(String prefsKey, @NonNull ArrayList<Entry> arrayList){ //This method takes an ArrayList and adds it to prefs. Done for the weekly tracking thing.
        String result = new String();
        for(int i = 0; i < arrayList.size(); i++){
            result = result + i + "|" + arrayList.get(i).getX() + "|" + arrayList.get(i).getY() + "###";
            Log.i(TAG, "arrayListToPrefs: " + result);
        }
        Log.i(TAG, "arrayListToPrefs: RESULT" + result);
        Log.i(TAG, "arrayListToPrefs: PREFS KEY " + prefsKey);
        Log.i(TAG, "arrayListToPrefs: LoginActivity.settings are equal to null: " + (LoginActivity.settings == null));
        Log.i(TAG, "arrayListToPrefs: LoginActivity.editor is equal to null: " + (LoginActivity.editor == null));

        try {
            LoginActivity.editor.putString(prefsKey, result);
            LoginActivity.editor.apply();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<Entry> arrayListFromPrefs(String prefsKey){ //Decodes the set Arraylist, taking the prefsKey and returning an arrayList so it can be worked with in the future.
        ArrayList<Entry> resultArray = new ArrayList<>();
        String input = LoginActivity.settings.getString(prefsKey, "default");
        Log.i(TAG, "arrayListFromPrefs: RETRIEVED FROM PREFS: " + input);
        String[] arrayInput = input.split("###");
        for(int i = 0; i < arrayInput.length; i++){
            String preResult = arrayInput[i];
            Log.i(TAG, "arrayListFromPrefs: " + preResult);
            String[] arrayPreResult = preResult.split("\\|");
            float x = Float.parseFloat(arrayPreResult[1]);
            float y = Float.parseFloat(arrayPreResult[2]);
            resultArray.add(new Entry(x, y));
            Log.i(TAG, "arrayListFromPrefs: NEW ARRAY ENTRY: " + resultArray.get(i));
        }
        for(int i = 0; i < resultArray.size(); i++){
            Log.i(TAG, "arrayListFromPrefs: COMPLETED ARRAY: " + resultArray.get(i));
        }
        return resultArray;
    }

   // @Override
   // protected void onStop() { //Saving the tracked values into SharedPreferences
   //     super.onStop();
   //     for(int i = 0; i < values.size(); i++){
   //         Log.i(TAG, "onStop: SAVING VALUES. VALUE " + i + " EQUALS TO " + values.get(i));
   //     }
   //     arrayListToPrefs("weekValues", values);
   //     Log.i(TAG, "onDestroy: HAPPENED " + true);
   // }

    public String BitMapToString(Bitmap bitmap){ //Encodes a bitmap into a string for saving into SharedPreferences.
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){ //Decodes a bitmap from string for extracting it from Shared Preferences.
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String getMonth(int monthNumber){
        String result = "";
        switch(monthNumber){
            case 1: result = "January";
                    break;
            case 2: result = "February";
                break;
            case 3: result = "March";
                break;
            case 4: result = "April";
                break;
            case 5: result = "May";
                break;
            case 6: result = "June";
                break;
            case 7: result = "July";
                break;
            case 8: result = "August";
                break;
            case 9: result = "September";
                break;
            case 10: result = "October";
                break;
            case 11: result = "November";
                break;
            case 12: result = "December";
                break;
        }
        Log.i(TAG, "getMonth: result: " + result);
        return result;
    }

    public ArrayList<String> getTimeDateArray (LocalDateTime localDateTime){ //A simple method to turn a LocalDateTime object into an Arraylist with year as index 0, month as 1, day as 2, hour as 3 and minute as 4 (does not include seconds)
        String stringLocalDateTime = localDateTime.toString();
        ArrayList<String> localDateTimeArrayList = new ArrayList<>();
        String[] localDateTimeArray = stringLocalDateTime.split("T");
        String[] localDateArray = localDateTimeArray[0].split("-");
        for(int i = 0; i < localDateArray.length; i++){
            localDateTimeArrayList.add(localDateArray[i]);
            Log.i(TAG, "getTimeArray: ADDING: " + localDateArray[i]);
        }
        localDateArray = localDateTimeArray[1].split(":");
        for(int i = 0; i < localDateArray.length; i++){
            localDateTimeArrayList.add(localDateArray[i]);
            Log.i(TAG, "getTimeArray: ADDING: " + localDateArray[i]);
        }
        return localDateTimeArrayList;
    }

    public String arrayListToString(ArrayList arrayList){
        String result = "";
        for (int i = 0; i < arrayList.size(); i++){
            result = result + arrayList.get(i) + "--";
            Log.i(TAG, "arrayListToString: RESULT " + result);
        }
        return result;
    }

    public ArrayList stringToArrayList(String string){ //THE SPLIT MUST BE "--"
        String[] resultArray = string.split("--");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < resultArray.length; i++){
            arrayList.add(resultArray[i]);
            Log.i(TAG, "stringToArrayList: ARRAYLIST " + arrayList);
        }
        return arrayList;
    }

    public LocalDateTime arrayListToLocalDateTime(ArrayList arrayList){
        String preResult = "";
        preResult = arrayList.get(0) + "-" + arrayList.get(1) + "-" + arrayList.get(2) + "T" + arrayList.get(3) + ":" + arrayList.get(4) + ":00.469";
        Log.i(TAG, "arrayListToLocalDateTime: PRERESULT " + preResult);
        LocalDateTime result = LocalDateTime.parse(preResult);
        return result;
    }

    public String arrayListToLocalDateTimeString(ArrayList arrayList){
        String preResult = "";
        preResult = arrayList.get(0) + "-" + arrayList.get(1) + "-" + arrayList.get(2) + "T" + arrayList.get(3) + ":" + arrayList.get(4) + ":00.469";
        Log.i(TAG, "arrayListToLocalDateTime: RESULT " + preResult);
        return preResult;
    }

    public void updateMoodStateString(String nextTimeClickString){
        if(!nextTimeClickString.equals("default")){
            LocalDateTime nextTimeClickLDT = LocalDateTime.parse(nextTimeClickString);
            ArrayList<String> nextTimeClickArray = getTimeDateArray(nextTimeClickLDT);
            moodChooseState.setText("You can choose your mood at: " + nextTimeClickArray.get(3) + ":" + nextTimeClickArray.get(4) + " on " + nextTimeClickArray.get(2) + "." + nextTimeClickArray.get(1));
        } else{
            moodChooseState.setText(" ");
        }

    }

}