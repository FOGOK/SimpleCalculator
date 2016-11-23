package com.fogok.simplecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by FOGOK on 23.11.2016 15:54.
 * Если ты это читаешь, то знай, что этот код хуже
 * кожи разлагающегося бомжа лежащего на гнилой
 * лавочке возле остановки автобуса номер 985
 */
public class MainActivity extends AppCompatActivity {

    private EditText expressionEditText;
    private TextView answerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWidgets();
    }
    private void setWidgets(){
        expressionEditText = (EditText) findViewById(R.id.inputExpression);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
    }

    protected void calculate(View v){
        final String answer = CalculatorCore.calculateExpression(this, expressionEditText.getText().toString(), findViewById(R.id.mainLayout));
        final String hintAndAnswer = getString(R.string.answerInExpression).concat(answer);
        answerTextView.setText(hintAndAnswer);
    }

    protected void addCharToExpressionEditText(View v){             //добавляем к тексту в поле для выражения знак или точку
        final int caretPosition = expressionEditText.getSelectionEnd();
        final String expressionText = expressionEditText.getText().toString();
        final String leftTextInExpressionEditText = expressionText.substring(0, caretPosition);
        final String rightTextInExpressionEditText = expressionText.toString().substring(caretPosition, expressionText.length());
        String addingChar = "";
        switch (v.getId()){
            case R.id.plusBtn:
                addingChar = "+";
                break;
            case R.id.minusBtn:
                addingChar = "-";
                break;
            case R.id.multiplyBtn:
                addingChar = "*";
                break;
            case R.id.divideBtn:
                addingChar = "/";
                break;
            case R.id.dotBtn:
                addingChar = ".";
                break;
            case R.id.percentBtn:
                addingChar = "%";
                break;
            case R.id.parenthessOpenBtn:
                addingChar = "(";
                break;
            case R.id.parenthessCloseBtn:
                addingChar = ")";
                break;
        }
        expressionEditText.setText(leftTextInExpressionEditText.concat(addingChar).concat(rightTextInExpressionEditText));
        expressionEditText.setSelection(caretPosition + 1);
    }



    //nativeMethods

    private final static String answerTextKey = "answrTxt";
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        answerTextView.setText(savedInstanceState.getString(answerTextKey));
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(answerTextKey, answerTextView.getText().toString());
    }

    ///
}
