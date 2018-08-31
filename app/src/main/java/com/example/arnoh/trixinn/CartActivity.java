package com.example.arnoh.trixinn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    public static List<Cart> sSelectedCartItems = new ArrayList<>();
    public static CartAdapter sCartAdapter;
    private RecyclerView mCartRv;
    private Button mCheckoutBtn;

    public static TextView mTotalAmountTv;
    private double mTotalAmount = 0;

    private String[] mTablesArray = {"T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10"};
    private List<String> mTablesList;
    private String mSelectedTable = null;
    private String mCustomerName = null;

    private DatabaseReference mOrdersRef;
    ProgressDialog mSendingProgress;

    Map ordersMap = new HashMap();

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sCartAdapter = new CartAdapter(sSelectedCartItems, this);

        mOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        mCheckoutBtn = findViewById(R.id.btn_checkout);
        mCartRv = findViewById(R.id.rv_cart_list);
        mCartRv.setHasFixedSize(true);
        mCartRv.setLayoutManager(new LinearLayoutManager(this));
        mCartRv.setAdapter(sCartAdapter);

        mSendingProgress = new ProgressDialog(this);
        mSendingProgress.setMessage("Submitting Order...");
        mSendingProgress.setCancelable(false);

        mTotalAmountTv = findViewById(R.id.tv_cart_total_amount);

        for (Cart cartItem : sSelectedCartItems){

            mTotalAmount += cartItem.getPrice();

        }

        mTablesList = new ArrayList<>();

        for (String table : mTablesArray){

            mTablesList.add(table);

        }

        mTotalAmountTv .setText("Total Amount to be Paid: KSh" + mTotalAmount);

        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText nameInput = new EditText(CartActivity.this);
                nameInput.setHint("Name");
                nameInput.setInputType(TextUtils.CAP_MODE_WORDS);

                Spinner tableSpinner = new Spinner(CartActivity.this);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_spinner_item, mTablesList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                tableSpinner.setAdapter(spinnerAdapter);
                tableSpinner.setOnItemSelectedListener(new SpinnerClickListener());

                AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(CartActivity.this);
                confirmationAlert.setView(tableSpinner);

                confirmationAlert.setMessage("Select Table")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                AlertDialog.Builder nameAlert = new AlertDialog.Builder(CartActivity.this);
                                nameAlert.setView(nameInput);

                                nameAlert.setMessage("Enter Your Name")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                mSendingProgress.show();

                                                if (!TextUtils.isEmpty(nameInput.getText())){
                                                    mCustomerName = nameInput.getText().toString();
                                                    sendOrder(mSelectedTable, mCustomerName);

                                                }else {
                                                    nameInput.setError("Please enter your name");
                                                }

                                            }})
                                        .setNegativeButton("Cancel", null).show();

                            }})
                        .setNegativeButton("Cancel", null).show();

            }
        });

    }

    private void sendOrder(String mSelectedTable, String mCustomerName) {

        final String orderKey = mOrdersRef.push().getKey();

        Map orderDetailsMap = new HashMap();
        orderDetailsMap.put("tableNo", mSelectedTable);
        orderDetailsMap.put("orderKey", orderKey);
        orderDetailsMap.put("customerName", mCustomerName);
        orderDetailsMap.put("orderTime", ServerValue.TIMESTAMP);



        FirebaseDatabase.getInstance().getReference().child("Orders").child("OrderDetails").child(orderKey)
                .setValue(orderDetailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    for (Cart cart : sSelectedCartItems){

                        ordersMap.put("foodName", cart.getName());
                        ordersMap.put("totalCost", cart.getPrice());
                        ordersMap.put("quantity", cart.getQuantity());

                        FirebaseDatabase.getInstance().getReference().child("Orders").child("OrderItems").child(orderKey).push()
                                .setValue(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    mSendingProgress.dismiss();

                                    Toast.makeText(CartActivity.this, "Order Sent Successfully", Toast.LENGTH_SHORT).show();
                                    sSelectedCartItems.clear();
                                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                                    finish();


                                }

                            }
                        });

                    }

                }

            }
        });



    }

    private class SpinnerClickListener implements AdapterView.OnItemSelectedListener{


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            mSelectedTable = mTablesList.get(i);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_clear_cart) {

            AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(CartActivity.this);
            confirmationAlert.setTitle("Clear Cart")
                    .setMessage("Do you really want clear the cart? ")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            CartActivity.sSelectedCartItems.clear();
                            CartActivity.sCartAdapter.notifyDataSetChanged();
                            Toast.makeText(CartActivity.this, "Cart Cleared Successfully", Toast.LENGTH_SHORT).show();

                           Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(mainIntent);
                           finish();

                        }})
                    .setNegativeButton("NO", null).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
