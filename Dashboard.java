package mkawa.okhttp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

public class Dashboard extends Activity {
    private String playerName;
    private String playerTeam;
    private int textColor;
    private Drawable backgroundDrawable;
    public static FetchSettings getSettings;
    public static float categoryPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //retrieve settings
        try {
            getSettings.requestSettings();
            categoryPoints = getSettings.catgPoints;

        } catch (Exception e) {
            e.printStackTrace();
        }

        //handle app startup for first time users
        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_STORAGE",MODE_PRIVATE);
        if(sharedPreferences.getString("USERNAME",null) == null){
            Intent startUserNameActivity = new Intent(getApplicationContext(), UserNameEntry.class);
            startActivity(startUserNameActivity);
        } else if (sharedPreferences.getString("TEAM",null) == null){
            Intent teamSelect = new Intent(getApplicationContext(), SelectTeam.class);
            startActivity(teamSelect);
        } else {
            playerName = sharedPreferences.getString("USERNAME","");
            playerTeam = sharedPreferences.getString("TEAM","");
        }
        final RelativeLayout dashboardButtonLayout = (RelativeLayout) findViewById(R.id.dashboardButtons);

        dashboardButtonLayout.post(new Runnable(){
            public void run(){
                int h = dashboardButtonLayout.getHeight();
                ImageView settings = (ImageView) findViewById(R.id.action_settings);
                ImageView leaderBoard = (ImageView) findViewById(R.id.leaderBoard);
                ImageView market = (ImageView) findViewById(R.id.market);
                ImageView beerSearch = (ImageView) findViewById(R.id.searchBeer);
                ImageView userStats = (ImageView) findViewById(R.id.userStats);
                ImageView tokens = (ImageView) findViewById(R.id.tokens);

                System.out.println(h);

                for(int i = h; i == 0; i--){
                    if(i % 3 == 0){
                        System.out.println("hello");
                        h = i;
                        break;
                    }
                    System.out.println("wtf");
                }

                System.out.println(h/3);

                settings.requestLayout();
                settings.getLayoutParams().height = h/3;
                //settings.setMinimumHeight(h/3);
                leaderBoard.requestLayout();
                leaderBoard.getLayoutParams().height = h/3;
                //leaderBoard.setMinimumHeight(h/3);
                market.requestLayout();
                market.getLayoutParams().height = h/3;
                //market.setMinimumHeight(h/3);
                beerSearch.requestLayout();
                beerSearch.getLayoutParams().height = h/3;
                //beerSearch.setMinimumHeight(h/3);
                userStats.requestLayout();
                userStats.getLayoutParams().height = h/3;
                //userStats.setMinimumHeight(h/3);
                tokens.requestLayout();
                tokens.getLayoutParams().height = h/3;
                //tokens.setMinimumHeight(h/3);

                System.out.println(settings.getHeight());
                System.out.println(leaderBoard.getHeight());
                System.out.println(market.getHeight());
                System.out.println(beerSearch.getHeight());
                System.out.println(userStats.getHeight());
                System.out.println(tokens.getHeight());
            }
        });

        initializeVariables();
        System.out.println("category points = " + categoryPoints);


    }//END ONCREATE

    private void initializeVariables(){
        TextView teamName = (TextView) findViewById(R.id.teamNameText);
        teamName.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));
        teamName.setText(playerTeam);

        ImageView teamLogo = (ImageView) findViewById(R.id.teamLogo);
        RelativeLayout teamLayout = (RelativeLayout) findViewById(R.id.teamLayout);
        TextView userNameTV = (TextView) findViewById(R.id.playerNameText);



        switch(playerTeam){
            case "RATCHET RATS":
                teamLogo.setImageResource(R.mipmap.rats);
                textColor = ContextCompat.getColor(getApplicationContext(),R.color.RatsDark);
                backgroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.rats);
                break;
            case "LIT LEMURS":
                teamLogo.setImageResource(R.mipmap.lemurs);
                textColor = ContextCompat.getColor(getApplicationContext(),R.color.LemursLight);
                backgroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lemurs);
                break;
            case "FADED FOXES":
                teamLogo.setImageResource(R.mipmap.foxes);
                textColor = ContextCompat.getColor(getApplicationContext(),R.color.FoxesLight);
                backgroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.foxes);
                break;
            case "SLIZARD LIZARDS":
                teamLogo.setImageResource(R.mipmap.lizards);
                textColor = ContextCompat.getColor(getApplicationContext(),R.color.LizardsLight);
                backgroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lizards);
                break;
        }

        teamName.setTextColor(textColor);
        teamLayout.setBackground(backgroundDrawable);
        userNameTV.setText(playerName);
        userNameTV.setTextColor(textColor);
        userNameTV.setTypeface(EasyFonts.ostrichRegular(getApplicationContext()));

        /*
        settings.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        leaderBoard.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        market.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        beerSearch.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        userStats.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        */

    }

    public void selectDestination(View v){
        Intent activitySelect;
        switch(v.getId()) {
            case R.id.action_settings:
                activitySelect = new Intent(getApplicationContext(), settings.class);
                break;
            case R.id.market:
                activitySelect = new Intent(getApplicationContext(), Market.class);
                break;
            case R.id.userStats:
                activitySelect = new Intent(getApplicationContext(), UserStatstics.class);
                break;
            case R.id.leaderBoard:
                activitySelect = new Intent(getApplicationContext(), LeaderBoard.class);
                break;
            case R.id.searchBeer:
                activitySelect = new Intent(getApplicationContext(), beerSearch.class);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }

        startActivity(activitySelect);
    }


}
