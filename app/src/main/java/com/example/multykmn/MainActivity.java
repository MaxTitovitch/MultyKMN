package com.example.multykmn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onExit(View veiw) {
        finish();
    }

    public void onGame(View view) {
        Intent intent = new Intent(".GameActitity");
        startActivity(intent);
    }

    public void onSeting(View view) {
        Intent intent = new Intent(".SetingActivity");
        startActivity(intent);
    }

}
