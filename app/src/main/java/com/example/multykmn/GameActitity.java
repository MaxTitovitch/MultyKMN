package com.example.multykmn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GameActitity extends AppCompatActivity {

    private SharedPreferences preferences;

    private ArrayList<String> moves = new ArrayList<>();

    private SecureRandom random = new SecureRandom();

    private String key = "";

    private String hashHMAC = "";

    private int androidMove = 0;

    private final int gray = Color.parseColor("#aaaaaa");
    private final int green = Color.parseColor("#00ff00");
    private final int red = Color.parseColor("#ff0000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        prepareData();
        doMove();
        ((EditText)findViewById(R.id.EditHMAC)).setText(hashHMAC);
    }


    private void prepareData() {
        String movesText = readData();
        parseData(movesText);
        addElements();
    }

    private String readData() {
        preferences = getSharedPreferences("Meta", MODE_PRIVATE);
        String movesText = preferences.getString("Moves", "");
        if(movesText.equals("")) {
            return addFirstData();
        }
        return movesText;

    }

    private String addFirstData(){
        preferences = getSharedPreferences("Meta", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String stringMoves = "{Камень@@@@@Ножницы@@@@@Бумага}";
        editor.putString("Moves", stringMoves);
        editor.commit();
        return stringMoves;
    }

    private void parseData(String movesText) {
        movesText = movesText.substring(1).substring(0, movesText.length() - 2);
        moves = new ArrayList<>(Arrays.asList(movesText.split("@@@@@")));
    }

    private void addElements() {
        RadioGroup radioGroup = findViewById(R.id.MovesRadio);
        for (int i = 0; i < moves.size(); i++) {
            radioGroup.addView(addRadioBox(moves.get(i), i), i);
        }
    }


    private RadioButton addRadioBox(String text, int id) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(id);
        radioButton.setText(text);
        radioButton.setTextColor(Color.parseColor("#aaaaaa"));
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(40, 0, 20,0);
        radioButton.setLayoutParams(layoutParams);
        if(id == 0) radioButton.setChecked(true);
        radioButton.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
        return radioButton;
    }

    public void doMove() {
        key = generateKey();
        androidMove = generateMove();
        hashHMAC = getHash(key, moves.get(androidMove));
    }

    private String generateKey(){
        byte bytes[] = new byte[32];
        random.nextBytes(bytes);
        StringBuilder stringBuilder = new StringBuilder();
        for (byte oneByte : bytes) {
            stringBuilder.append(String.format("%02x", oneByte));
        }
        return stringBuilder.toString();
    }

    private int generateMove() {
        return random.nextInt(moves.size());
    }

    private String getHash(String keyString, String value) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA384");
            Mac mac = Mac.getInstance("HmacSHA384");
            mac.init(key);
            byte[] bytes = mac.doFinal(value.getBytes("ASCII"));
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (Exception e) { }
        return digest;
    }

    public void onCheck(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://beautifytools.com/hmac-generator.php"));
        startActivity(browserIntent);
    }

    public void onPlay(View view){
        String userMove = moves.get(getUserMove());
        String android = moves.get(androidMove);
        int result = CalculatorResult.getInstance().getResult(getUserMove(), androidMove, moves.size());
        switch (result) {
            case 0: changeElements(userMove, android, gray, gray, "НИЧЬЯ!"); break;
            case 1: changeElements(userMove, android, green, red, "ПОБЕДА!"); break;
            case 2: changeElements(userMove, android, red, green, "ПОРОЖЕНИЕ!"); break;
        }
    }

    private int getUserMove(){
        RadioGroup radioGroup = findViewById(R.id.MovesRadio);
        return radioGroup.getCheckedRadioButtonId();
    }

//    private int getResult (int userId, int androidId, int moves){
//        int halfQuantity = (moves- 1) / 2, size = moves;
//        if(userId == androidId) return 0;
//        if(userId > halfQuantity) {
//            return doUserMoreHalf(userId, androidId, halfQuantity);
//        } else if(userId < halfQuantity){
//            return doUserLessHalf(userId, androidId, halfQuantity, size);
//        } else {
//            if(userId > androidId) return 2;
//            else return 1;
//        }
//    }
//
//    private int doUserMoreHalf(int userId, int androidId, int halfQuantity){
//        int moving =  userId - halfQuantity;
//        userId -= moving;
//        androidId -= moving;
//        if(userId < androidId) return 1;
//        else {
//            if(androidId >= 0) return 2;
//            else return 1;
//        }
//    }
//
//    private int doUserLessHalf(int userId, int androidId, int halfQuantity, int size){
//        int moving = halfQuantity - userId;
//        userId += moving;
//        androidId += moving;
//        if(userId > androidId) return 2;
//        else {
//            if(androidId >= size) return 2;
//            else return 1;
//        }
//    }

    private void changeElements(String userValue, String androidValue, int userColor, int androidColor, String result) {
        changeEdit((TextView)findViewById(R.id.UserMove), userValue, userColor);
        changeEdit((TextView)findViewById(R.id.AndroidMove), androidValue, androidColor);
        changeEdit((TextView)findViewById(R.id.GameResult), result, userColor);
        ((EditText)findViewById(R.id.EditHMAC)).setText(hashHMAC);
        ((EditText)findViewById(R.id.EditKey)).setText(key);
        (findViewById(R.id.BReset)).setEnabled(true);
        (findViewById(R.id.BPlay)).setEnabled(false);
    }

    private void changeEdit(TextView textView, String value, int color) {
        textView.setText(value);
        textView.setTextColor(color);
    }

    public void onReset(View view) {
        doMove();
        changeEdit((TextView)findViewById(R.id.UserMove), "[Ваш ход]", gray);
        changeEdit((TextView)findViewById(R.id.AndroidMove), "[Ход Андроида]", gray);
        changeEdit((TextView)findViewById(R.id.GameResult), "[Результат]", gray);
        ((EditText)findViewById(R.id.EditHMAC)).setText(hashHMAC);
        ((EditText)findViewById(R.id.EditKey)).setText("");
        (findViewById(R.id.BReset)).setEnabled(false);
        (findViewById(R.id.BPlay)).setEnabled(true);
    }
}
