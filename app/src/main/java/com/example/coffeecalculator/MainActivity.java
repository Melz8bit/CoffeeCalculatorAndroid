package com.example.coffeecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    final int BASE_POUR_OVER_COFFEE_AMOUNT = 40;
    final int BASE_POUR_OVER_WATER_AMOUNT = 600;
    final int BASE_COLD_BREW_COFFEE_AMOUNT = 100;
    final int BASE_COLD_BREW_WATER_AMOUNT = 980;
    final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    ViewGroup baseValuesLayout;
    ViewGroup calculateLayout;
    ViewGroup resultsLayout;
    Button btnPourOverCoffee;
    Button btnIcedCoffee;
    Button btnCalculate;
    TextView baseCoffeeAmount;
    TextView baseWaterAmount;
    TextView inputValueAmount;
    TextView lblEnterAmount;
    TextView coffeeResult;
    TextView waterResult;
    RadioGroup radioGroup;
    RadioButton selectedCoffeeOrWater;
    InputMethodManager keyboardManager;
    View divider;
    View parentView;
    String brewMethod = "";
    String whatToCalculate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseCoffeeAmount = (TextView) findViewById(R.id.txtBaseCoffeeAmount);
        baseWaterAmount = (TextView) findViewById(R.id.txtBaseWaterAmount);

        baseValuesLayout = (ViewGroup) findViewById(R.id.BaseValuesLayout);
        divider = (View) findViewById(R.id.divider);
        calculateLayout = (ViewGroup) findViewById(R.id.CalculateLayout);
        resultsLayout = (ViewGroup) findViewById(R.id.ResultsLayout);

        btnPourOverCoffee = (Button) findViewById(R.id.btnPourOverCoffee);
        btnIcedCoffee = (Button) findViewById(R.id.btnColdBrewCoffee);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        lblEnterAmount = (TextView) findViewById(R.id.lblEnterAmount);
        inputValueAmount = (TextView) findViewById(R.id.txtEnterCalcAmount);

        coffeeResult = (TextView) findViewById(R.id.txtCoffeeResult);
        waterResult = (TextView) findViewById(R.id.txtWaterResult);

        btnPourOverCoffee.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                brewMethod = "pour_over";
                displayBaseAmounts(brewMethod);
            }
        });

        btnIcedCoffee.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                brewMethod = "cold_brew";
                displayBaseAmounts(brewMethod);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String inputBoxHint = "";
                selectedCoffeeOrWater = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                switch (checkedId) {
                    case R.id.rdioCoffee:
                        whatToCalculate = getResources().getString(R.string.coffee);
                        inputBoxHint = getResources().getString(R.string.enter_calc_amount) + " water (in mL)";
                        break;
                    case R.id.rdioWater:
                        whatToCalculate = getResources().getString(R.string.water);
                        inputBoxHint = getResources().getString(R.string.enter_calc_amount) + " coffee (in g)";
                        break;
                }

                lblEnterAmount.setText(inputBoxHint);
                lblEnterAmount.setVisibility(View.VISIBLE);
                inputValueAmount.setVisibility(View.VISIBLE);
                inputValueAmount.requestFocus();
                btnCalculate.setVisibility(View.VISIBLE);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double userAmount = 0.0;
                String measurement = "";

                // Hide keyboard when calculate button pressed
                keyboardManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboardManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // User doesn't input an amount
                try {
                    userAmount = Double.parseDouble(inputValueAmount.getText().toString());
                }
                catch (NumberFormatException e) {
                    String message = "Amount of coffee/water cannot be blank";

                    parentView = findViewById(R.id.parentLayout);
                    Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.gray))
                            .setTextColor(getResources().getColor(R.color.white))
                            .show();
                }

                if (whatToCalculate.equals("Coffee")) {
                    coffeeResult.setText(decimalFormat.format(calculateCoffee(userAmount, brewMethod)) + " grams");
                    waterResult.setText(userAmount + " milliliters");
                } else if (whatToCalculate.equals("Water")) {
                    waterResult.setText(decimalFormat.format(calculateWater(userAmount, brewMethod)) + " milliliters");
                    coffeeResult.setText(userAmount + " grams");
                }

                resultsLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayBaseAmounts(String brewMethod) {
        if (brewMethod.equals("pour_over")) {
            baseCoffeeAmount.setText(Integer.toString(BASE_POUR_OVER_COFFEE_AMOUNT) + " g");
            baseWaterAmount.setText(Integer.toString(BASE_POUR_OVER_WATER_AMOUNT) + " ml");
        }
        else if (brewMethod.equals("cold_brew")) {
            baseCoffeeAmount.setText(Integer.toString(BASE_COLD_BREW_COFFEE_AMOUNT) + " g");
            baseWaterAmount.setText(Integer.toString(BASE_COLD_BREW_WATER_AMOUNT) + " ml");
        }

        baseValuesLayout.setVisibility(View.VISIBLE);
        calculateLayout.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

    }

    public double calculateCoffee(double waterAmount, String brewMethod) {
        if (brewMethod.equals("pour_over"))
            return (BASE_POUR_OVER_COFFEE_AMOUNT * waterAmount) / BASE_POUR_OVER_WATER_AMOUNT;
        else if (brewMethod.equals("cold_brew"))
            return (BASE_COLD_BREW_COFFEE_AMOUNT * waterAmount) / BASE_COLD_BREW_WATER_AMOUNT;

        return 0.0;
    }

    public double calculateWater(double coffeeAmount, String brewMethod) {
        if (brewMethod.equals("pour_over"))
            return (BASE_POUR_OVER_WATER_AMOUNT * coffeeAmount) / BASE_POUR_OVER_COFFEE_AMOUNT;
        else if (brewMethod.equals("cold_brew"))
            return (BASE_COLD_BREW_WATER_AMOUNT * coffeeAmount) / BASE_COLD_BREW_COFFEE_AMOUNT;

        return 0.0;
    }
}