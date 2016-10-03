package mkawa.okhttp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vstechlab.easyfonts.EasyFonts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserStatstics extends baseActivity {
    private TextView dPtsTV;
    private TextView ozPtsTV;
    private TextView abvPtsTV;
    private TextView ibuPtsTV;
    private TextView header;
    private GridLayout statsGrid;

    private TextView header1;
    private TextView header2;
    private TextView header3;
    private TextView header4;
    private TextView r1c1;
    private TextView r1c2;
    private TextView r1c3;
    private TextView r1c4;
    private TextView r2c1;
    private TextView r2c2;
    private TextView r2c3;
    private TextView r2c4;
    private TextView r3c1;
    private TextView r3c2;
    private TextView r3c3;
    private TextView r3c4;
    private TextView r4c1;
    private TextView r4c2;
    private TextView r4c3;
    private TextView r4c4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statstics);

        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_STORAGE",MODE_PRIVATE);
        final String playerUserName = sharedPreferences.getString("USERNAME","");
        final String query = "https://spreadsheets.google.com/feeds/list/1K9xB3ZivGYa5S-1SdyEWNUUNN8tu1-9_NH-xyA9if-8/2/public/full?alt=json";

        initializeVariables();

        //Method for retrieving Data on worker thread
        try {
            requestUserStats(query, playerUserName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//END ON CREATE

    void requestUserStats(String url, final String userName) throws Exception{
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Set up Web Client for data source
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) throw new IOException("unexpected code " + response);

                //get Web response and store into variable
                final String responseData = response.body().string();


                //Parse JSON
                JsonParser jsonParser = new JsonParser();
                JsonElement elem = jsonParser.parse(responseData);

                //Set JsonElement to JsonArray for looping
                JsonArray elemArr = elem.getAsJsonObject().get("feed").getAsJsonObject().get("entry").getAsJsonArray();

                //Set up ArrayList Containers
                final ArrayList<PlayerGroup> sortCont = new ArrayList<>();

                //Iterate to get all results and fill ArrayList Containers at "data" level
                for (JsonElement sheetsResponse : elemArr) {

                    PlayerGroup group = new PlayerGroup();

                    //Player Name Fill Container
                    JsonElement nameField = sheetsResponse.getAsJsonObject().get("gsx$name").getAsJsonObject().get("$t");
                    group.player = nameField.toString().replaceAll("\"", "");

                    //Ounces Fill Container
                    JsonElement ozField = sheetsResponse.getAsJsonObject().get("gsx$totaloz").getAsJsonObject().get("$t");
                    group.oz = ozField.getAsFloat();

                    //Drink # Fill Container
                    JsonElement drinkField = sheetsResponse.getAsJsonObject().get("gsx$totaldrinks").getAsJsonObject().get("$t");
                    group.drinks = drinkField.getAsFloat();

                    //IBU Fill Container
                    JsonElement ibuField = sheetsResponse.getAsJsonObject().get("gsx$totalibu").getAsJsonObject().get("$t");
                    group.ibu = ibuField.getAsFloat();

                    //ABVoz Fill Container
                    JsonElement abvField = sheetsResponse.getAsJsonObject().get("gsx$totalabvoz").getAsJsonObject().get("$t");
                    group.abv = abvField.getAsFloat();

                    //Drink Points accumulated
                    JsonElement drkPts = sheetsResponse.getAsJsonObject().get("gsx$drinkpoint").getAsJsonObject().get("$t");
                    group.drinkPts = drkPts.getAsFloat();

                    //Ounce Points accumulated
                    JsonElement ozPts = sheetsResponse.getAsJsonObject().get("gsx$ozpoint").getAsJsonObject().get("$t");
                    group.ozPts = ozPts.getAsFloat();

                    //ABV Points accumulated
                    JsonElement abvPts = sheetsResponse.getAsJsonObject().get("gsx$abvpoint").getAsJsonObject().get("$t");
                    group.abvPts = abvPts.getAsFloat();

                    //IBU Points accumulated
                    JsonElement ibuPts = sheetsResponse.getAsJsonObject().get("gsx$ibupoint").getAsJsonObject().get("$t");
                    group.ibuPts = ibuPts.getAsFloat();

                    //add to master container
                    sortCont.add(group);

                }
                //extract user info out of container
                final PlayerStats user = new PlayerStats();
                for (int i = 0; i < sortCont.size();i++){
                    if(sortCont.get(i).player.equals(userName.toUpperCase())){
                        user.setDrinks(sortCont.get(i).drinks);
                        user.setOz(sortCont.get(i).oz);
                        user.setAbv(sortCont.get(i).abv);
                        user.setIbu(sortCont.get(i).ibu);
                        user.setDrinkTokens(sortCont.get(i).drinkPts);
                        user.setOzTokens(sortCont.get(i).ozPts);
                        user.setAbvTokens(sortCont.get(i).abvPts);
                        user.setIbuTokens(sortCont.get(i).ibuPts);
                    }
                }

                //set values to other players for rankings
                final ArrayList<PlayerStats> players = new ArrayList<>();
                for (int i = 0; i < sortCont.size(); i++){
                    System.out.println(sortCont.get(i).player);
                    if(!sortCont.get(i).player.equals("")){
                        PlayerStats player = new PlayerStats();
                        player.setName(sortCont.get(i).player);
                        player.setDrinks(sortCont.get(i).drinks);
                        player.setOz(sortCont.get(i).oz);
                        player.setAbv(sortCont.get(i).abv);
                        player.setIbu(sortCont.get(i).ibu);
                        player.setDrinkTokens(sortCont.get(i).drinkPts);
                        player.setOzTokens(sortCont.get(i).ozPts);
                        player.setAbvTokens(sortCont.get(i).abvPts);
                        player.setIbuTokens(sortCont.get(i).ibuPts);
                        players.add(player);
                    }
                }

                final int nPlayers = players.size();

                //Sort players based on drinks and set Rank
                Comparator<PlayerStats> drinkComp = new Comparator<PlayerStats>() {
                    @Override
                    public int compare(PlayerStats drink1, PlayerStats drink2) {
                        return Double.compare(drink1.getDrinks(), drink2.getDrinks());
                    }
                };
                Collections.sort(players, drinkComp);
                Collections.reverse(players);
                for (int i = 0; i < players.size(); i++){
                    if(userName.toUpperCase().equals(players.get(i).getName())){
                        user.setDrinkRank(i+1);
                        break;
                    }
                }

                //Sort players based on ounces and set Rank
                Comparator<PlayerStats> ozComp = new Comparator<PlayerStats>() {
                    @Override
                    public int compare(PlayerStats ounce1, PlayerStats ounce2) {
                        return Double.compare(ounce1.getOz(), ounce2.getOz());
                    }
                };
                Collections.sort(players, ozComp);
                Collections.reverse(players);
                for (int i = 0; i < players.size(); i++){
                    if(userName.toUpperCase().equals(players.get(i).getName())){
                        user.setOzRank(i+1);
                        break;
                    }
                }

                //Sort players based on abv and set Rank
                Comparator<PlayerStats> abvComp = new Comparator<PlayerStats>() {
                    @Override
                    public int compare(PlayerStats abv1, PlayerStats abv2) {
                        return Double.compare(abv1.getAbv(), abv2.getAbv());
                    }
                };
                Collections.sort(players, abvComp);
                Collections.reverse(players);
                for (int i = 0; i < players.size(); i++){
                    if(userName.toUpperCase().equals(players.get(i).getName())){
                        user.setAbvRank(i+1);
                        break;
                    }
                }

                //Sort players based on ibu and set Rank
                Comparator<PlayerStats> ibuComp = new Comparator<PlayerStats>() {
                    @Override
                    public int compare(PlayerStats ibu1, PlayerStats ibu2) {
                        return Double.compare(ibu1.getIbu(), ibu2.getIbu());
                    }
                };
                Collections.sort(players, ibuComp);
                Collections.reverse(players);
                for (int i = 0; i < players.size(); i++){
                    if(userName.toUpperCase().equals(players.get(i).getName())){
                        user.setIbuRank(i+1);
                        break;
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Set Header
                        header.setText("TOTAL POINTS : " + String.valueOf((int)(user.getDrinkPts() + user.getOzPts() + user.getAbvPts() + user.getIbuPts())));

                        //add values to chart in groups
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        entries.add(new BarEntry(1f , user.getDrinkPts()));
                        entries.add(new BarEntry(3f , user.getOzPts()));
                        entries.add(new BarEntry(5f , user.getAbvPts()));
                        entries.add(new BarEntry(7f , user.getIbuPts()));

                        //create dataSet
                        BarDataSet dataSet = new BarDataSet(entries,"points");
                        dataSet.setColor(ContextCompat.getColor(getApplicationContext(),R.color.Goldenrod));

                        //setup Leader Board
                        BarChart userPoints = new BarChart(getApplicationContext());


                        //define chart data
                        BarData data = new BarData(dataSet);
                        data.setValueFormatter(new MyValueFormatter());
                        data.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.SlateGray));
                        data.setBarWidth(0.75f); // set the width of each bar
                        data.setValueTextSize(25f);

                        userPoints.setData(data);
                        userPoints.setFitBars(true);
                        userPoints.animateY(3000);
                        userPoints.setDescription("");
                        userPoints.getAxisRight().setEnabled(false);
                        userPoints.setViewPortOffsets(0,0,0,0);
                        userPoints.invalidate(); // refresh


                        XAxis xAxis = userPoints.getXAxis();
                        xAxis.setDrawAxisLine(false);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setDrawLabels(false);
                        xAxis.setAxisMinValue(0f);
                        xAxis.setAxisMaxValue(8f);


                        YAxis yAxis = userPoints.getAxisLeft();
                        yAxis.setDrawAxisLine(false);
                        yAxis.setDrawLabels(false);
                        yAxis.setAxisMinValue(0f);
                        yAxis.setAxisMaxValue(11.9f);
                        yAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkGoldenrod));

                        //define layout parameters for chart
                        LinearLayout.LayoutParams pntChrtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                        //define layout for chart to be applied and apply
                        LinearLayout pointsChart = (LinearLayout) findViewById(R.id.pointsChart);
                        pointsChart.addView(userPoints,pntChrtParams);


                        //populate stats grid;
                        //TOTALS
                        r1c2.setText(String.valueOf((int)user.getDrinks()));
                        r2c2.setText(String.valueOf(user.getOz()));
                        r3c2.setText(String.valueOf(user.getAbv()));
                        r4c2.setText(String.valueOf(user.getIbu()));

                        //TO NEXT POINT
                        r1c3.setText(String.valueOf((int)user.getNextDrinkPt()));
                        r2c3.setText(String.valueOf(user.getNextOzPt()));
                        r3c3.setText(String.valueOf(user.getNextAbvPt()));
                        r4c3.setText(String.valueOf(user.getNextIbuPt()));

                        //RANKINGS
                        r1c4.setText(String.valueOf(user.getDrinkRank())+ " of " + nPlayers);
                        r2c4.setText(String.valueOf(user.getOzRank())+ " of " + nPlayers);
                        r3c4.setText(String.valueOf(user.getAbvRank())+ " of " + nPlayers);
                        r4c4.setText(String.valueOf(user.getIbuRank())+ " of " + nPlayers);

                        System.out.println(user.getAbv()%1.5);
                        System.out.println(1.5-user.getAbv()%1.5);
                        System.out.println((1.5-user.getAbv()%1.5)*10);
                        System.out.println(Math.round((1.5-user.getAbv()%1.5)*10));
                        System.out.println((float)Math.round((1.5-user.getAbv()%1.5)*10)/10);




                    }
                });


            }


        });


    }

    private void initializeVariables() {
        dPtsTV = (TextView) findViewById(R.id.dpts);
        ozPtsTV = (TextView) findViewById(R.id.ozPts);
        abvPtsTV = (TextView) findViewById(R.id.abvPts);
        ibuPtsTV = (TextView) findViewById(R.id.ibuPts);
        header = (TextView) findViewById(R.id.totalPointsHeader);
        statsGrid = (GridLayout) findViewById(R.id.statGrid);
        header1 = (TextView) findViewById(R.id.header1);
        header2 = (TextView) findViewById(R.id.header2);
        header3 = (TextView) findViewById(R.id.header3);
        header4 = (TextView) findViewById(R.id.header4);
        r1c1 = (TextView) findViewById(R.id.r1c1);
        r1c2 = (TextView) findViewById(R.id.r1c2);
        r1c3 = (TextView) findViewById(R.id.r1c3);
        r1c4 = (TextView) findViewById(R.id.r1c4);
        r2c1 = (TextView) findViewById(R.id.r2c1);
        r2c2 = (TextView) findViewById(R.id.r2c2);
        r2c3 = (TextView) findViewById(R.id.r2c3);
        r2c4 = (TextView) findViewById(R.id.r2c4);
        r3c1 = (TextView) findViewById(R.id.r3c1);
        r3c2 = (TextView) findViewById(R.id.r3c2);
        r3c3 = (TextView) findViewById(R.id.r3c3);
        r3c4 = (TextView) findViewById(R.id.r3c4);
        r4c1 = (TextView) findViewById(R.id.r4c1);
        r4c2 = (TextView) findViewById(R.id.r4c2);
        r4c3 = (TextView) findViewById(R.id.r4c3);
        r4c4 = (TextView) findViewById(R.id.r4c4);

        header.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.Purple));

        dPtsTV.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        ozPtsTV.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        abvPtsTV.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        ibuPtsTV.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        header.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        header2.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        header3.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        header4.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        r1c1.setTypeface(EasyFonts.ostrichRegular(getApplicationContext()));
        r2c1.setTypeface(EasyFonts.ostrichRegular(getApplicationContext()));
        r3c1.setTypeface(EasyFonts.ostrichRegular(getApplicationContext()));
        r4c1.setTypeface(EasyFonts.ostrichRegular(getApplicationContext()));


    }
}
