package com.fogok.simplecalculator;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import java.util.ArrayList;

/**
 * Created by FOGOK on 23.11.2016 15:54.
 * Если ты это читаешь, то знай, что этот код хуже
 * кожи разлагающегося бомжа лежащего на гнилой
 * лавочке возле остановки автобуса номер 985
 */
public class CalculatorCore {

    private static View layoutToSnackBar;
    private static Context appContext;
    private static String expressionText;
    private static StringBuilder answer = new StringBuilder(100);

    public static String calculateExpression(Context _appContext, String _expressionText, View _layoutToSnackBar){
        appContext = _appContext;
        expressionText = _expressionText;
        layoutToSnackBar = _layoutToSnackBar;
        answer.delete(0, answer.length());

        try {
            if (isCorrectParenthess() && isCorrectSigns() && !isEmpty()){
                answer.append(expressionText);
                answer.append(" = ");

                while (expressionText.contains("("))
                    findMostSubExpressionAndCalculateTo();

                answer.append(calculateExpressionWithoutBrackets(expressionText));
                Snackbar.make(layoutToSnackBar, appContext.getText(R.string.complete), Snackbar.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Snackbar.make(layoutToSnackBar, appContext.getText(R.string.unknowError), Snackbar.LENGTH_LONG).show();
        }

        return answer.toString();
    }

    private static void findMostSubExpressionAndCalculateTo(){ //найти самое вложенное выражение в скобках, и решить его
        int startPosExpr = 0;
        int endPosExpr = 0;
        final char[] textChars = expressionText.toCharArray();

        for (int i = 0; i < expressionText.length(); i++){
            if (textChars[i] == '(')
                startPosExpr = i;
            else if (textChars[i] == ')'){
                endPosExpr = i + 1;
                break;
            }
        }
        final String findedString = expressionText.substring(startPosExpr, endPosExpr);
        final String findedStringNoBrackets = findedString.substring(1, findedString.length() - 1);

        final float answerInSmallExpression = calculateExpressionWithoutBrackets(findedStringNoBrackets);
        expressionText = expressionText.replace(findedString, String.valueOf(answerInSmallExpression));
        answer.append(expressionText);
        answer.append(" = ");

    }


    private static float calculateExpressionWithoutBrackets(String expressionText){       //сосчитать выражение без скобок
        setNumbersAndSignsToArrays(expressionText);

        // signsQ:     [-]   [+]  [*]       // 2 массива, один со знаками, другой с номерами
        // numbersQ: [4] [5.5] [10] [3]


        while (signsQ.size() != 0) {        //считать до тех пор, пока всё не будет сосчитано
            int isMainPrioritySign = -1; /// если var равна -1, значит в выражении нет знака главного приоритета,
            // иначе он есть, и находится под указанным индексом

            for (int i = 0; i < signsQ.size(); i++) {
                if (isMainPrioriySign(signsQ.get(i))){
                    isMainPrioritySign = i;
                }
            }


            // signsQ:     [-]   [+]  [*]
            // numbersQ: [4] [5.5] [10] [3]

            float resultVal = 0f;
            if (isMainPrioritySign == -1){
                switch (signsQ.get(0)){
                    case '+':
                        resultVal = numbersQ.get(0) + numbersQ.get(1);
                        break;
                    case '-':
                        resultVal = numbersQ.get(0) - numbersQ.get(1);
                        break;
                }
                signsQ.remove(0);
                numbersQ.set(0, resultVal);
                numbersQ.remove(1);

            }else{
                switch (signsQ.get(isMainPrioritySign)){
                    case '*':
                        resultVal = numbersQ.get(isMainPrioritySign) * numbersQ.get(isMainPrioritySign + 1);
                        break;
                    case '/':
                        resultVal = numbersQ.get(isMainPrioritySign) / numbersQ.get(isMainPrioritySign + 1);
                        break;
                    case '%':   //процент от числа
                        resultVal = numbersQ.get(isMainPrioritySign) * (numbersQ.get(isMainPrioritySign + 1) / 100f);
                        break;
                }
                signsQ.remove(isMainPrioritySign);
                numbersQ.set(isMainPrioritySign, resultVal);
                numbersQ.remove(isMainPrioritySign + 1);
            }

            // signsQ:     [-]   [+]
            // numbersQ: [4] [5.5] [30]
        }


        return numbersQ.get(0);
    }


    private static ArrayList<Character> signsQ = new ArrayList<Character>(50);
    private static ArrayList<Float> numbersQ = new ArrayList<Float>(50);
    private static void setNumbersAndSignsToArrays(String expressionText){
        final char[] textChars = expressionText.toCharArray();
        signsQ.clear();
        numbersQ.clear();
        StringBuilder stringBuilder = new StringBuilder(50);

        // 4-5.5+10
        boolean addNumber = false;
        for (int i = 0; i < expressionText.length(); i++){
            final char element = textChars[i];

            if (isSign(element) && element != '.'){
                if (addNumber){
                    numbersQ.add(Float.parseFloat(stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                    addNumber = false;
                }
                signsQ.add(element);
            }else if (i == expressionText.length() - 1){
                stringBuilder.append(element);
                numbersQ.add(Float.parseFloat(stringBuilder.toString()));
            }else{
                stringBuilder.append(element);
                addNumber = true;
            }
        }
    }


    //всё, что касается определения корректности расположения скобок
    private static boolean isCorrectParenthess(){    //проверяем, все ли скобки образуют пары
        int openCloseParanthess = 0;
        for (char element : expressionText.toCharArray()){
            if (element == '(')
                openCloseParanthess++;
            else if (element == ')')
                openCloseParanthess--;
        }

        if (openCloseParanthess == 0)
            return true;

        putError(Error.IncorectCountOfParenthess);
        return false;
    }
    ///

    private static boolean isEmpty(){
        if (expressionText.equals("")){
            putError(Error.Empty);
            return true;
        }
        return false;
    }

    //всё, что касается определения корректности расположения знаков
    private static boolean isCorrectSigns(){        //проверяем, правильно ли расставлены знаки
        final char[] textChars = expressionText.toCharArray();
        boolean isNullSigns = true;
        for (int i = 0; i < expressionText.length(); i++) {
            final char prevChar = i == 0 ? '(' : textChars[i - 1];
            final char currChar = textChars[i];
            final char nextChar = i == expressionText.length() - 1 ? ')' : textChars[i + 1];


            if (isSign(currChar)){
                if (!(isNumber(prevChar) || isNumber(nextChar) || (prevChar == ')' && currChar != '.') || (nextChar == '(' && currChar != '.'))
                        ){
                    putError(Error.IncorrectSigns);
                    return false;
                }

            }
        }



        return isNullSigns;
    }

    private static char[] signs = new char[] {'+', '-', '/', '*', '%', '.'};     ///знаки
    private static boolean isSign(char c){
        for (char element : signs){
            if (c == element)
                return true;
        }
        return false;
    }

    private static char[] mainPrioritySigns = new char[] {'/', '*', '%'};     ///знаки главного приоритета
    private static boolean isMainPrioriySign(char c){
        for (char element : mainPrioritySigns){
            if (c == element)
                return true;
        }
        return false;
    }

    private static char[] numbers = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static boolean isNumber(char c){
        for (char element : numbers){
            if (c == element)
                return true;
        }
        return false;
    }
    ////





    //всё, что связано с ошибками
    private enum Error{
        IncorectCountOfParenthess, IncorrectSigns, Empty
    }

    private static void putError(Error exprError){
        String message = "";
        switch (exprError){
            case IncorectCountOfParenthess:
                message = appContext.getString(R.string.incorectCountOfParenthess);
                break;
            case IncorrectSigns:
                message = appContext.getString(R.string.incorectSigns);
                break;
            case Empty:
                message = appContext.getString(R.string.emptyString);
                break;
        }
        Snackbar.make(layoutToSnackBar, message, Snackbar.LENGTH_LONG).show();
    }
    ///

}
