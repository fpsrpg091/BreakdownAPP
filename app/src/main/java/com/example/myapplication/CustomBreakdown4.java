package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomBreakdown4 extends AppCompatActivity {

    private TextView textViewTotalAmount;
    private TextView textViewNumPeople;
    private LinearLayout layoutIndividualAmounts;
    private Button buttonShare;
    private Button buttonSave;
    private Button buttonReset;

    private double totalAmount;
    private int numPeople;
    private List<EditText> editIndividualAmounts;
    private double initialEqualAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_breakdown4);

        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewNumPeople = findViewById(R.id.textViewNumPeople);
        layoutIndividualAmounts = findViewById(R.id.layoutIndividualAmounts);
        buttonShare = findViewById(R.id.buttonShare);
        buttonSave = findViewById(R.id.buttonSave);
        buttonReset = findViewById(R.id.buttonReset);

        editIndividualAmounts = new ArrayList<>();

        // Get data from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            totalAmount = Double.parseDouble(extras.getString("totalAmount"));
            numPeople = Integer.parseInt(extras.getString("numPeople"));

            String formattedTotalAmount = String.format("Total Amount: RM %.2f", totalAmount);
            textViewTotalAmount.setText(formattedTotalAmount);
            textViewNumPeople.setText("Number of People: " + numPeople);

            // Calculate initial equal amount
            initialEqualAmount = totalAmount / numPeople;

            // Create and add EditText fields for individual amounts
            for (int i = 1; i <= numPeople; i++) {
                EditText editText = new EditText(this);
                editText.setHint("Person " + i + " Amount");
                editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setText(String.format("%.2f", initialEqualAmount)); // Set initial equal amount with 2 decimal places
                layoutIndividualAmounts.addView(editText);
                editIndividualAmounts.add(editText);

                // Set up text change listener for each EditText
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Calculate and display remaining amount
                        calculateAndDisplayRemainingAmount();
                    }
                });
            }
        }

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double remainingAmount = calculateRemainingAmount();
                if (remainingAmount == 0) {
                    // Prepare the message to share
                    StringBuilder message = new StringBuilder("Custom Breakdown:\n\n");
                    message.append("Total Bill: RM ").append(String.format("%.2f", totalAmount)).append("\n");
                    message.append("Number of People: ").append(numPeople).append("\n\n");

                    for (int i = 0; i < editIndividualAmounts.size(); i++) {
                        EditText editAmount = editIndividualAmounts.get(i);
                        String individualAmountText = editAmount.getText().toString();
                        message.append("Person ").append(i + 1).append(": RM ").append(individualAmountText).append("\n");
                    }

                    // Create an Intent to share the message
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Custom Breakdown");
                    intent.putExtra(Intent.EXTRA_TEXT, message.toString());

                    // Start the sharing activity
                    startActivity(Intent.createChooser(intent, "Share Custom Breakdown"));
                } else {
                    showToast("Remaining amount must be zero to share");
                }
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double remainingAmount = calculateRemainingAmount();
                if (remainingAmount == 0) {
                    // Prepare the result to save
                    StringBuilder resultBuilder = new StringBuilder();
                    resultBuilder.append("Total Amount: RM ").append(String.format("%.2f", totalAmount)).append("\n");
                    resultBuilder.append("Number of People: ").append(numPeople).append("\n");

                    for (int i = 0; i < editIndividualAmounts.size(); i++) {
                        EditText editAmount = editIndividualAmounts.get(i);
                        String individualAmountText = editAmount.getText().toString();
                        resultBuilder.append("Person ").append(i + 1).append(": RM ").append(individualAmountText).append("\n");
                    }

                    // Save the result using SharedPreferences
                    saveResultToStorage(totalAmount, numPeople, resultBuilder.toString());

                    showToast("Result saved successfully");

                    Intent intent = new Intent(CustomBreakdown4.this, SavedResultsActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Remaining amount must be zero to save");
                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAmounts();
            }
        });
    }

    private double calculateRemainingAmount() {
        double enteredTotal = 0;

        // Calculate the sum of entered individual amounts
        for (EditText editAmount : editIndividualAmounts) {
            String individualAmountText = editAmount.getText().toString();
            if (!individualAmountText.isEmpty()) {
                double individualAmount = Double.parseDouble(individualAmountText);
                enteredTotal += individualAmount;
            }
        }

        return totalAmount - enteredTotal;
    }

    private void calculateAndDisplayRemainingAmount() {
        double remainingAmount = calculateRemainingAmount();
        TextView textViewRemainingAmount = findViewById(R.id.textViewRemainingAmount);
        String formattedRemainingAmount = String.format("Remaining Amount: RM %.2f", remainingAmount);
        textViewRemainingAmount.setText(formattedRemainingAmount);
    }

    private void resetAmounts() {
        for (EditText editAmount : editIndividualAmounts) {
            editAmount.setText(String.format("%.2f", initialEqualAmount));
        }

        calculateAndDisplayRemainingAmount();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveResultToStorage(double totalBill, int numPeople, String result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        String dataToSave = "Date and Time: " + currentDateAndTime +
                "\n" + result;

        SharedPreferences sharedPreferences = getSharedPreferences("SavedResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0);
        count++;
        editor.putString("result_" + count, dataToSave);
        editor.putInt("count", count);
        editor.apply();
    }
}