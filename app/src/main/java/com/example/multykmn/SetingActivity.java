package com.example.multykmn;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SetingActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private ArrayList<String> moves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        prepareData();
    }

    private void prepareData() {
        String movesText = readData();
        parseData(movesText);
        addElements();
    }

    private String readData() {
        preferences = getPreferences(MODE_PRIVATE);
        String movesText = preferences.getString("Moves", "");
        if(movesText.equals("")) {
            return addFirstData();
        }
        return movesText;

    }

    private String addFirstData(){
        preferences = getPreferences(MODE_PRIVATE);
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
        LinearLayout linearLayout = findViewById(R.id.LinealArea);
        for (int i = 0; i < moves.size(); i++) {
            linearLayout.addView(addOneChild(moves.get(i), i));
        }

    }

    private ConstraintLayout addOneChild(String text, int id) {
        ConstraintLayout constraintLayout = new ConstraintLayout(this);

        constraintLayout.addView(addCheckBox(id));
        constraintLayout.addView(addEditText(text, id));

        return constraintLayout;
    }

    private CheckBox addCheckBox(int id) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(id);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 0, 20,0);
        checkBox.setLayoutParams(layoutParams);
        checkBox.setButtonTintList(ColorStateList.valueOf(Color.WHITE));

        return checkBox;
    }

    private EditText addEditText(String text, int id) {
        EditText editText = new EditText(this);
        editText.setText(text);
        editText.setId(id);
        editText.setHint("Ход");
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(80, 0, 15,0);
        editText.setLayoutParams(layoutParams);
        editText.setTextColor(Color.WHITE);
        return editText;
    }

    public void onAdd(View view) {
        LinearLayout linearLayout = findViewById(R.id.LinealArea);
        for (int i = 0; i < 2; i++) {
            linearLayout.addView(addOneChild("", moves.size()));
            moves.add("");
        }
    }

    public void onRemove(View view) {
        LinearLayout linearLayout = findViewById(R.id.LinealArea);
        int countNew = runByChilds(linearLayout, false);

        if (countNew <= 3 || countNew%2 == 0 || linearLayout.getChildCount()-countNew == 0) {
            Toast.makeText(this, "Выберите чётное кол-во > 3", Toast.LENGTH_LONG).show();
        } else {
            runByChilds(linearLayout, true);
        }
    }

    private int runByChilds(LinearLayout linearLayout, boolean isRemove) {
        int countChecked = 0, countAll = linearLayout.getChildCount();
        for (int i = 2; i < linearLayout.getChildCount(); i++) {
            ConstraintLayout constraintLayout = (ConstraintLayout)linearLayout.getChildAt(i);
            CheckBox checkBox = (CheckBox)constraintLayout.getChildAt(0);
            if(checkBox.isChecked()) {
                countChecked++;
                if(isRemove ) {
                    moves.remove(i-2);
                    linearLayout.removeViewAt(i--);
                }
            }
        }
        return countAll-countChecked;
    }

    public void onSave(View view) {
        LinearLayout linearLayout = findViewById(R.id.LinealArea);
        if (saveChilds(linearLayout)) {
            Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
            changeMoves();
        } else {
            Toast.makeText(this, "Заполните все поля уникально!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean saveChilds(LinearLayout linearLayout){
        ArrayList<String> newMoves = new ArrayList<>();
        for (int i = 2; i < linearLayout.getChildCount(); i++) {
            ConstraintLayout constraintLayout = (ConstraintLayout)linearLayout.getChildAt(i);
            EditText editText = (EditText) constraintLayout.getChildAt(1);
            String currentText = editText.getText().toString();
            if(currentText.equals("") || newMoves.contains(currentText)) {
                return false;
            } else {
                newMoves.add(currentText);
            }
        }
        moves = newMoves;
        return true;
    }

    private void changeMoves(){
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String stringMoves = "{" + toStringArray(moves) + "}";
        editor.putString("Moves", stringMoves);
        editor.commit();
    }

    private String toStringArray(ArrayList<String> arrayList) {
        String finalString = "";
        for (String value : moves) {
            finalString += value + "@@@@@";
        }
        return finalString.substring(0, finalString.length()-5);
    }

    public void onBack(View view){
        finish();
    }

}