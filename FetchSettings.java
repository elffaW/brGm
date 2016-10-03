package mkawa.okhttp;

import android.app.Application;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.orm.SugarRecord;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mattkawahara on 8/24/16.
 */
public class FetchSettings extends SugarRecord {

    Float drinkLevel;
    Float ozLevel;
    Float abvLevel;
    Float ibuLevel;
    Float catgPoints;

    public FetchSettings() {
    }

    public FetchSettings(float drinkLevel, float ozLevel, float abvLevel, float ibuLevel, float catgPoints){
        this.drinkLevel = drinkLevel;
        this.ozLevel = ozLevel;
        this.abvLevel = abvLevel;
        this.ibuLevel = ibuLevel;
        this.catgPoints = catgPoints;
    }


    final String url = "https://spreadsheets.google.com/feeds/list/1K9xB3ZivGYa5S-1SdyEWNUUNN8tu1-9_NH-xyA9if-8/4/public/full?alt=json";

    //Pull Data From Spreadsheet
    OkHttpClient client = new OkHttpClient();

    void requestSettings() throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

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
                 float drinkLvl = 0;
                 float ozLvl = 0;
                 float abvLvl = 0;
                 float ibuLvl = 0;
                 float catPoints = 0;

                //Iterate to get all results and fill ArrayList Containers at "data" level
                for (JsonElement sheetsResponse : elemArr) {

                    //drink setting
                    JsonElement drink = sheetsResponse.getAsJsonObject().get("gsx$drinks").getAsJsonObject().get("$t");
                    drinkLvl = drink.getAsFloat();

                    //oz setting
                    JsonElement oz = sheetsResponse.getAsJsonObject().get("gsx$oz").getAsJsonObject().get("$t");
                    ozLvl = oz.getAsFloat();

                    //abv setting
                    JsonElement abv = sheetsResponse.getAsJsonObject().get("gsx$aoz").getAsJsonObject().get("$t");
                    abvLvl = abv.getAsFloat();

                    //ibu setting
                    JsonElement ibu = sheetsResponse.getAsJsonObject().get("gsx$ibu").getAsJsonObject().get("$t");
                    ibuLvl = ibu.getAsFloat();

                    //category point setting
                    JsonElement catPoint = sheetsResponse.getAsJsonObject().get("gsx$catpoints").getAsJsonObject().get("$t");
                    catPoints = catPoint.getAsFloat();

                }

                FetchSettings settings = new FetchSettings(drinkLvl, ozLvl, abvLvl, ibuLvl, catPoints);
                settings.save();


            }
        });
    }



}
