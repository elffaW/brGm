package mkawa.okhttp;

import mkawa.okhttp.contactActivity.sendShitParams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.vstechlab.easyfonts.EasyFonts;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TradeShare extends Activity {

    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    //input element ids found from the live form page
    public static final String NAME_KEY="entry.468624322";
    public static final String TOKEN_KEY="entry.1339641009";
    public static final String DRINK_POINT_KEY="entry.1674081600";
    public static final String OZ_POINT_KEY="entry.1995814502";
    public static final String ABV_POINT_KEY="entry.1922509005";
    public static final String IBU_POINT_KEY="entry.444340806";
    public static final String TEAM_KEY = "entry.2118900696";

    protected static String clickToken = "";
    protected static float clickVal;
    protected static String URL = "https://docs.google.com/forms/d/1xVzwPQyQdasuy2wSjJnQ1v6Vn9o_I9czcCHN42-9qYc/formResponse";
    protected static String playerName = "";
    protected static String playerTeam = "";
    protected static int pointSelection = 0;
    protected static String drinkPointsAdd;
    protected static String ozPointsAdd;
    protected static String abvPointsAdd;
    protected static String ibuPointsAdd;
    protected static int genPointsAdd = 0;
    protected static float keepStock = 0;
    protected static int shares = 0;
    protected static int drinkPoints;
    protected static int ozPoints;
    protected static int abvPoints;
    protected static int ibuPoints;
    protected static PlayerStats player;
    protected static float playerDrinkPoints;
    protected static float playerOzPoints;
    protected static float playerAbvPoints;
    protected static float playerIbuPoints;
    protected static float defaultDrinkPoints;
    protected static float defaultOzPoints;
    protected static float defaultAbvPoints;
    protected static float defaultIbuPoints;


    private SeekBar seekBar;
    private Button tradeButton;
    private TextView stockLeft;
    private TextView drinkCounter;
    private TextView ozCounter;
    private TextView abvCounter;
    private TextView ibuCounter;
    private TextView pointsText;
    private DecoView tokenDeco;
    private DecoView pointDeco;
    private SeriesItem stockSeries;
    private SeriesItem drinkSeries;
    private SeriesItem ozSeries;
    private SeriesItem abvSeries;
    private SeriesItem ibuSeries;
    private int drinkSeriesIndex;
    private int ounceSeriesIndex;
    private int abvSeriesIndex;
    private int ibuSeriesIndex;
    private int stockSeriesIndex;
    private float maxCatPoints = 0;
    private int maxPoints;
    private double maxMarketVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_share);

        initializeVariables();

        FetchSettings settings = FetchSettings.findById(FetchSettings.class, 1);
        maxCatPoints = settings.catgPoints;

        //set trade button to unClickable by default
        tradeButton.setClickable(false);
        final int button = getResources().getIdentifier("inactive_button", "drawable", getPackageName());
        tradeButton.setBackgroundResource(button);

        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_STORAGE",MODE_PRIVATE);
        playerName = sharedPreferences.getString("USERNAME","");
        playerTeam = sharedPreferences.getString("TEAM", "");

        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTrade);
        ImageView mainTokenPic = (ImageView) findViewById(R.id.mainTokenView);

        //retrieve information from beerSearch.java
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            clickToken = extras.getString("tokenType").replaceAll("\"", "");
            clickVal = extras.getFloat("curVal");
        }
        int token = getResources().getIdentifier(clickToken, "mipmap", getPackageName());
        mainTokenPic.setImageResource(token);


        //determine # of shares
        List<TokenPurse> tokens;

        long nTokens = TokenPurse.count(TokenPurse.class,null,null);
        tokens = TokenPurse.listAll(TokenPurse.class);
        for (int i = 0; i < nTokens; i++){
            if(tokens.get(i).tokenName.equals(clickToken)){
                shares =  (int)(tokens.get(i).tokenShare);
                break;
            }
        }

        //set values

        //determine max number of points player can get
        maxMarketVal = shares * clickVal;
        maxPoints = (int)Math.floor(shares * clickVal);
        keepStock = (float)Math.floor((maxMarketVal / clickVal)*10)/10;

        /*
        seekBar.setMax(maxPoints);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = maxPoints;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
                progress = progressValue;
                genPointsAdd = progress;
                keepStock = (float)Math.floor(((maxMarketVal - progress) / clickVal)*10)/10;

                String plPt = " PT";
                if (progress>1){
                    plPt = " PTS";
                }
                pointsText.setText("PURCHASE " + progress + plPt);

                if(progress>= 1){
                    tradeButton.setClickable(true);
                    int executeButton = getResources().getIdentifier("roundedbutton", "drawable", getPackageName());
                    tradeButton.setBackgroundResource(executeButton);
                } else {
                    tradeButton.setClickable(false);
                    tradeButton.setBackgroundResource(button);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                genPointsAdd = progress;
                keepStock = (float)Math.floor(((maxMarketVal - progress) / clickVal)*100)/10;

                String plPt = " PT";
                if (progress>1){
                    plPt = " PTS";
                }
                pointsText.setText("PURCHASE " + progress + plPt);

                if(progress>= 1){
                    tradeButton.setClickable(true);
                    int executeButton = getResources().getIdentifier("roundedbutton", "drawable", getPackageName());
                    tradeButton.setBackgroundResource(executeButton);
                } else {
                    tradeButton.setClickable(false);
                    tradeButton.setBackgroundResource(button);
                }
            }
        });
        */

        tokenArc();
        try {
            callUserStats(playerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("maxPoints = " + maxPoints);
        //pointsArc();


    } //END ON CREATE

    public void executeTrade (View v){
        //code here to submit token shares
        //determine which entry to send based on which radio is selected

        drinkPointsAdd = "0";
        ozPointsAdd = "0";
        abvPointsAdd = "0";
        ibuPointsAdd = "0";

        if (pointSelection == 0){
            //send to drink point category
            drinkPointsAdd = String.valueOf(genPointsAdd);
        } else if (pointSelection == 1 ){
            //send to oz point category
            ozPointsAdd = String.valueOf(genPointsAdd);
        } else if (pointSelection == 2){
            //send to abv point category
            abvPointsAdd = String.valueOf(genPointsAdd);
        } else {
            //send tp ibu point category
            ibuPointsAdd = String.valueOf(genPointsAdd);
        }

        //Create an object for PostDataTask AsyncTask
        contactActivity.sendShitParams params = new contactActivity().new sendShitParams(true,
                null,
                playerName,
                null,
                URL,
                null,
                null,
                null,
                null,
                clickToken,
                drinkPointsAdd,
                ozPointsAdd,
                abvPointsAdd,
                ibuPointsAdd,
                keepStock,
                playerTeam);

        //Create an object for PostDataTask AsyncTask
        PostDataTask postDataTask = new PostDataTask();

        postDataTask.execute(params);
    }


    //AsyncTask to send data as a http POST request
    private class PostDataTask extends AsyncTask<sendShitParams, Void, Boolean> {

        @Override
        protected Boolean doInBackground(sendShitParams... params) {
            Boolean result = params[0].test;
            String name = params[0].playerName;
            String postBody="";
            String URL = params[0].postUrl;
            String tradeToken = params[0].tradeToken;
            String drinkP = params[0].drinkPoints;
            String ozP = params[0].ozPoints;
            String abvP = params[0].abvPoints;
            String ibuP = params[0].ibuPoints;
            float keepStock = params[0].keepStock;
            String team = params[0].teamName;


            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = NAME_KEY+"=" + URLEncoder.encode(name,"UTF-8") +
                        "&" + TOKEN_KEY + "=" + URLEncoder.encode(tradeToken,"UTF-8") +
                        "&" + DRINK_POINT_KEY + "=" + URLEncoder.encode(drinkP,"UTF-8") +
                        "&" + OZ_POINT_KEY + "=" + URLEncoder.encode(ozP,"UTF-8") +
                        "&" + ABV_POINT_KEY + "=" + URLEncoder.encode(abvP,"UTF-8") +
                        "&" + IBU_POINT_KEY + "=" + URLEncoder.encode(ibuP,"UTF-8") +
                        "&" + TEAM_KEY + "=" + URLEncoder.encode(team,"UTF-8");
            } catch (UnsupportedEncodingException ex) {

                result = false;
            }


            try{
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            }catch (IOException exception){
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean w){
            //Print Success or failure message accordingly
            final Boolean test = w;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),test?"Trade Executed":"There was some error in sending message. Please try again after some time.",Toast.LENGTH_LONG).show();

                    List<TokenPurse> tokens;
                    long nTokens = TokenPurse.count(TokenPurse.class,null,null);
                    tokens = TokenPurse.listAll(TokenPurse.class);
                    for (int i = 0; i < nTokens; i++){
                        if(tokens.get(i).tokenName.equals(clickToken)){
                            tokens.get(i).tokenShare = keepStock;
                            tokens.get(i).save();
                            break;
                        }
                    }

                    Intent allDone = new Intent(getApplicationContext(), LeaderBoard.class);
                    startActivity(allDone);
                }
            });
        }
    }

    private void initializeVariables() {
        tradeButton = (Button) findViewById(R.id.tradeButton);
        drinkCounter = (TextView) findViewById(R.id.drinkCounter);
        ozCounter = (TextView) findViewById(R.id.ozCounter);
        abvCounter = (TextView) findViewById(R.id.abvCounter);
        ibuCounter = (TextView) findViewById(R.id.ibuCounter);
        stockLeft = (TextView) findViewById(R.id.stockLeft);

        tokenDeco = (DecoView) findViewById(R.id.dynamicArcChart);
        pointDeco = (DecoView) findViewById(R.id.pointArcChart);

        drinkCounter.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        ozCounter.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        abvCounter.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        ibuCounter.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        drinkCounter.setText("0");
        ozCounter.setText("0");
        abvCounter.setText("0");
        ibuCounter.setText("0");
    }

    public void resetVal(View v) {
        playerDrinkPoints = defaultDrinkPoints;
        playerOzPoints = defaultOzPoints;
        playerAbvPoints = defaultAbvPoints;
        playerIbuPoints = defaultIbuPoints;

        tradeButton.setClickable(false);

        keepStock = shares;

        drinkCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray));
        ozCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray));
        abvCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray));
        ibuCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray));

        drinkCounter.setText("0");
        ozCounter.setText("0");
        abvCounter.setText("0");
        ibuCounter.setText("0");

        tokenDeco.addEvent(new DecoEvent.Builder(shares).setIndex(stockSeriesIndex).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerDrinkPoints).setIndex(drinkSeriesIndex).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerOzPoints).setIndex(ounceSeriesIndex).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerAbvPoints).setIndex(abvSeriesIndex).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerIbuPoints).setIndex(ibuSeriesIndex).build());
    }

    public void increaseVal(View v) {

        if(keepStock * clickVal >= 1){
            switch (v.getId()){
                case R.id.drinkClick:
                    if (playerDrinkPoints < maxCatPoints){
                        drinkCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.Goldenrod));
                        drinkPoints = Integer.valueOf(drinkCounter.getText().toString().replaceAll("\"", "")) + 1;
                        drinkCounter.setText(String.valueOf(drinkPoints));
                        playerDrinkPoints = playerDrinkPoints + 1;
                        keepStock  = (float)Math.floor(((keepStock*clickVal - 1) / clickVal)*10)/10;
                    }
                    break;
                case R.id.ozClick:
                    if (playerOzPoints < maxCatPoints) {
                        ozCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.Goldenrod));
                        ozPoints = Integer.valueOf(ozCounter.getText().toString().replaceAll("\"", "")) + 1;
                        playerOzPoints = playerOzPoints + 1;
                        ozCounter.setText(String.valueOf(ozPoints));
                        keepStock  = (float)Math.floor(((keepStock*clickVal - 1) / clickVal)*10)/10;
                    }
                    break;
                case R.id.abvClick:
                    if (playerAbvPoints < maxCatPoints) {
                        abvCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.Goldenrod));
                        abvPoints = Integer.valueOf(abvCounter.getText().toString().replaceAll("\"", "")) + 1;
                        playerAbvPoints = playerAbvPoints + 1;
                        abvCounter.setText(String.valueOf(abvPoints));
                        keepStock  = (float)Math.floor(((keepStock*clickVal - 1) / clickVal)*10)/10;
                    }
                    break;
                case R.id.ibuClick:
                    if (playerIbuPoints < maxCatPoints) {
                        ibuCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.Goldenrod));
                        ibuPoints = Integer.valueOf(ibuCounter.getText().toString().replaceAll("\"", "")) + 1;
                        ibuCounter.setText(String.valueOf(ibuPoints));
                        playerIbuPoints = playerIbuPoints + 1;
                        keepStock  = (float)Math.floor(((keepStock*clickVal - 1) / clickVal)*10)/10;
                    }
                    break;
            }

            tokenDeco.addEvent(new DecoEvent.Builder(keepStock).setIndex(stockSeriesIndex).build());
            pointDeco.addEvent(new DecoEvent.Builder(playerDrinkPoints).setIndex(drinkSeriesIndex).build());
            pointDeco.addEvent(new DecoEvent.Builder(playerOzPoints).setIndex(ounceSeriesIndex).build());
            pointDeco.addEvent(new DecoEvent.Builder(playerAbvPoints).setIndex(abvSeriesIndex).build());
            pointDeco.addEvent(new DecoEvent.Builder(playerIbuPoints).setIndex(ibuSeriesIndex).build());

            tradeButton.setClickable(true);

        }
    }



    private void tokenArc(){
        // Create background track
        tokenDeco.addSeries(new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray))
                .setRange(0, shares, shares)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());

        //Create data series track
        stockSeries = new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.Yellow))
                .setRange(0, shares, 0)
                .setLineWidth(32f)
                .setSpinDuration(1000)
                .setInterpolator(new LinearInterpolator())
                .build();

        stockSeriesIndex = tokenDeco.addSeries(stockSeries);

        tokenDeco.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(500)
                .setDuration(500)
                .build());

        tokenDeco.addEvent(new DecoEvent.Builder(shares).setIndex(stockSeriesIndex).setDelay(500).build());

        //set token listener
        stockSeries.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                stockLeft.setText((String.format(Locale.getDefault(),"%.1f",currentPosition)));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

    }

    private void pointsArc(){

        // Create background track
        pointDeco.addSeries(new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.SlateGray))
                .setRange(0, maxCatPoints, maxCatPoints)
                .setInitialVisibility(false)
                .setCapRounded(false)
                .setLineWidth(128f)
                .setInset(new PointF(48f,48f))
                .build());

        pointDeco.configureAngles(180,270);

        //Create Drink data series track
        drinkSeries = new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.Yellow))
                .setRange(0, maxCatPoints, 0)
                .setLineWidth(32f)
                .setSpinDuration(5000)
                .setCapRounded(false)
                .setInterpolator(new BounceInterpolator())
                .setInitialVisibility(false)
                .build();
        //Create Ounce data series track
        ozSeries = new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.Blue))
                .setRange(0, maxCatPoints, 0)
                .setLineWidth(32f)
                .setSpinDuration(5000)
                .setCapRounded(false)
                .setInterpolator(new BounceInterpolator())
                .setInitialVisibility(false)
                .setInset(new PointF(32f,32f))
                .build();
        //Create Abv data series track
        abvSeries = new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.Black))
                .setRange(0, maxCatPoints, 0)
                .setLineWidth(32f)
                .setSpinDuration(5000)
                .setCapRounded(false)
                .setInterpolator(new BounceInterpolator())
                .setInitialVisibility(false)
                .setInset(new PointF(64f,64f))
                .build();
        //Create Ounce data series track
        ibuSeries = new SeriesItem.Builder(ContextCompat.getColor(getApplicationContext(),R.color.NavajoWhite))
                .setRange(0, maxCatPoints, 0)
                .setLineWidth(32f)
                .setSpinDuration(5000)
                .setCapRounded(false)
                .setInterpolator(new BounceInterpolator())
                .setInitialVisibility(false)
                .setInset(new PointF(96f,96f))
                .build();

        drinkSeriesIndex = pointDeco.addSeries(drinkSeries);
        ounceSeriesIndex = pointDeco.addSeries(ozSeries);
        abvSeriesIndex = pointDeco.addSeries(abvSeries);
        ibuSeriesIndex = pointDeco.addSeries(ibuSeries);

        pointDeco.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(500)
                .setDuration(500)
                .build());

        pointDeco.addEvent(new DecoEvent.Builder(playerDrinkPoints).setIndex(drinkSeriesIndex).setDelay(1250).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerOzPoints).setIndex(ounceSeriesIndex).setDelay(1000).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerAbvPoints).setIndex(abvSeriesIndex).setDelay(750).build());
        pointDeco.addEvent(new DecoEvent.Builder(playerIbuPoints).setIndex(ibuSeriesIndex).setDelay(500).build());

    }

    public void callUserStats(final String userName) throws Exception {
        Request request = new Request.Builder()
                .url("https://spreadsheets.google.com/feeds/list/1K9xB3ZivGYa5S-1SdyEWNUUNN8tu1-9_NH-xyA9if-8/2/public/full?alt=json")
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
                for (int i = 0; i < sortCont.size(); i++) {
                    if (sortCont.get(i).player.equals(userName.toUpperCase())) {
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerDrinkPoints = user.getDrinkPts();
                        playerOzPoints = user.getOzPts();
                        playerAbvPoints = user.getAbvPts();
                        playerIbuPoints = user.getIbuPts();
                        defaultDrinkPoints = user.getDrinkPts();
                        defaultOzPoints = user.getOzPts();
                        defaultAbvPoints = user.getAbvPts();
                        defaultIbuPoints = user.getIbuPts();
                        pointsArc();
                    }
                });

            }
        });
    }
}
