package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomBreakdown3 extends AppCompatActivity {
    private LinearLayout layoutIndividualAmounts;
    private TextView textViewTotalAmount;
    private TextView textViewNumPeople;
    private Button buttonCalculateCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_breakdown3);

        layoutIndividualAmounts = findViewById(R.id.layoutIndividualAmounts);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewNumPeople = findViewById(R.id.textViewNumPeople);
        buttonCalculateCustom = findViewById(R.id.buttonCalculateCustom);
        buttonCalculateCustom = findViewById(R.id.buttonCalculateCustom);
        Button buttonReset = findViewById(R.id.buttonReset); // Add this line

        // Retrieve data from intent
        Intent intent = getIntent();
        String totalAmount = intent.getStringExtra("totalAmount");
        String numPeople = intent.getStringExtra("numPeople");

        // Set text views
        textViewTotalAmount.setText("Total Amount: RM " + totalAmount);
        textViewNumPeople.setText("Number of People: " + numPeople);

        // Create input fields based on the number of people
        int numPeopleInt = Integer.parseInt(numPeople);
        createInputFields(numPeopleInt);

        buttonCalculateCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplayResult(numPeopleInt);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetInputFields(); // Call the new method to reset input fields
            }
        });
    }

    private void createInputFields(int numFields) {
        layoutIndividualAmounts.removeAllViews(); // Clear any existing views

        for (int i = 0; i < numFields; i++) {
            EditText editText = new EditText(this);
            editText.setHint("Person " + (i + 1) + " Ratio");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            layoutIndividualAmounts.addView(editText);
        }
    }

    private void calculateAndDisplayResult(int numPeople) {
        List<Double> ratios = new ArrayList<>();

        // Collect individual ratios from input fields
        for (int i = 0; i < numPeople; i++) {
            EditText editText = (EditText) layoutIndividualAmounts.getChildAt(i);
            String inputText = editText.getText().toString().trim();

            if (!inputText.isEmpty()) {
                double ratio = Double.parseDouble(inputText);
                ratios.add(ratio);
            }
        }

        // Calculate individual amounts based on ratios and total bill
        Intent intent = getIntent();
        String totalAmount = intent.getStringExtra("totalAmount");
        double totalBill = Double.parseDouble(totalAmount);

        List<Double> individualAmounts = calculateIndividualAmounts(ratios, totalBill);

        // Display the calculated individual amounts using a popup dialog
        showCalculatedAmounts(individualAmounts,ratios);
    }

    private List<Double> calculateIndividualAmounts(List<Double> ratios, double totalBill) {
        List<Double> individualAmounts = new ArrayList<>();
        double totalRatio = 0;

        // Calculate the total ratio
        for (double ratio : ratios) {
            totalRatio += ratio;
        }

        // Calculate individual amounts based on ratios and total bill
        for (double ratio : ratios) {
            double individualAmount = (ratio / totalRatio) * totalBill;
            individualAmounts.add(individualAmount);
        }
        return individualAmounts;
    }

    private void showCalculatedAmounts(List<Double> individualAmounts, List<Double> ratios) {
        String totalAmountText = textViewTotalAmount.getText().toString();
        String numericTotalAmount = totalAmountText.substring(totalAmountText.indexOf("RM") + 3).trim();
        double totalBill = Double.parseDouble(numericTotalAmount);

        String numPeopleText = textViewNumPeople.getText().toString();
        String numericNumPeople = numPeopleText.substring(numPeopleText.indexOf(":") + 1).trim();
        int numPeople = Integer.parseInt(numericNumPeople);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result");

        StringBuilder resultTextBuilder = new StringBuilder();
        for (int i = 0; i < individualAmounts.size(); i++) {
            resultTextBuilder.append("Person ").append(i + 1).append(": RM ")
                    .append(decimalFormat.format(individualAmounts.get(i))).append("\n")
                    .append(" (").append(ratios.get(i) * 1).append(")").append("\n");
        }
        final String resultText = resultTextBuilder.toString();

        builder.setMessage(resultText);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);
                startActivity(Intent.createChooser(shareIntent, "Share Result"));
            }
        });
        builder.setNegativeButton("Close", null);
        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveResultToStorage(totalBill, numPeople, resultText);
                Toast.makeText(CustomBreakdown3.this, "Result saved", Toast.LENGTH_SHORT).show();
                // Navigate to SavedResultsActivity
                Intent intent = new Intent(CustomBreakdown3.this, SavedResultsActivity.class);
                startActivity(intent);
            }
        });

        builder.create().show();
    }

    private void saveResultToStorage(double totalBill, int numPeople,String result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        String dataToSave = "Date and Time: " + currentDateAndTime +
                "\nTotal bill: RM" + totalBill +
                "\nNumber of people: " + numPeople +
                "\n" + result;

        SharedPreferences sharedPreferences = getSharedPreferences("SavedResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0);
        count++;
        editor.putString("result_" + count, dataToSave);
        editor.putInt("count", count);
        editor.apply();
    }

    private void resetInputFields() {
        for (int i = 0; i < layoutIndividualAmounts.getChildCount(); i++) {
            EditText editText = (EditText) layoutIndividualAmounts.getChildAt(i);
            editText.setText(""); // Clear the text
        }
    }
}
