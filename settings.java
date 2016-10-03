package mkawa.okhttp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;


public class settings extends contactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView title = (TextView) findViewById(R.id.userNameEntry);
        EditText userNameEntry = (EditText)findViewById(R.id.userName);
        title.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.GhostWhite));
        userNameEntry.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.GhostWhite));
        title.setTypeface(EasyFonts.ostrichLight(getApplicationContext()));
        userNameEntry.setTypeface(EasyFonts.ostrichBlack(getApplicationContext()));

        }


    public void SaveName(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_STORAGE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }



    public void writeUserNameToVar(View v){
        EditText uN = (EditText)findViewById(R.id.userName);
        String uNs = uN.getText().toString();
        SaveName("USERNAME",uNs);
        Toast.makeText(getApplicationContext(), "Player Name Set to: " + uNs,
                Toast.LENGTH_LONG).show();
        //If Data successfully sent- then clear all fields
        uN.setText("");
    }

    public void emptyPurse(View v){
        TokenPurse.deleteAll(TokenPurse.class);
        Toast.makeText(getApplicationContext(), "Purse has been Emptied",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return true;
    }



}

