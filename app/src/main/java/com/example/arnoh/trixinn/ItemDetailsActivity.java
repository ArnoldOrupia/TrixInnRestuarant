package com.example.arnoh.trixinn;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView mNameTv, mPriceTv, mQtyTv, mTotalPriceTv;
    private ImageButton mIncreaseBtn, mDecreaseBtn;
    private Button mAddToCartBtn, mRemoveBtn;

    private int mQuantity = 1;
    private String mName = null;
    private double mPrice = 0;
    private double mTotalPrice = 0;

    private String mContainedName;
    private int mContainedQty = 0, mContainedpos = 0;
    double mContainedPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        final Bundle data = getIntent().getExtras();

        mNameTv = findViewById(R.id.tv_details_food_name);
        mPriceTv = findViewById(R.id.tv_details_price);
        mTotalPriceTv = findViewById(R.id.tv_total_price);
        mQtyTv = findViewById(R.id.tv_qty);
        mIncreaseBtn = findViewById(R.id.ib_increase);
        mDecreaseBtn = findViewById(R.id.ib_decrease);
        mAddToCartBtn = findViewById(R.id.btn_add_to_cart);
        mRemoveBtn = findViewById(R.id.btn_remove_from_cart);

        mName = data.getString("name");
        mPrice = data.getDouble("price");
        mTotalPrice = mPrice;

        mNameTv.setText(mName);
        mPriceTv.setText(""+ mPrice);
        mQtyTv.setText("Quantity: "+ mQuantity);

        mTotalPriceTv.setText("Total Cost: "+ mPrice);

        Cart cart = new Cart(mName, mQuantity, mTotalPrice);

        if (CartActivity.sSelectedCartItems.size() != 0){

            Iterator<Cart> iterator = CartActivity.sSelectedCartItems.iterator();

            while (iterator.hasNext()){

                String name = iterator.next().getName();

                if (name.equals(cart.getName())){

                    mContainedName = name;
                    mContainedPrice = cart.getPrice();
                    mContainedQty = cart.getQuantity();

                 //   mRemoveBtn.setVisibility(View.VISIBLE);
                   // mAddToCartBtn.setText("Update Item");

                }

            }

        }

        mIncreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mQuantity ++;
                mQtyTv.setText("Quantity: "+mQuantity);

                mTotalPrice = mPrice * mQuantity;
                mTotalPriceTv.setText("Total Cost: "+ mTotalPrice);

            }
        });

        mDecreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mQuantity < 1){

                }else {

                    mQuantity --;
                    mQtyTv.setText("Quantity: "+ mQuantity);

                    mTotalPrice = mTotalPrice - mPrice;
                    mTotalPriceTv.setText("Total Cost: "+ mTotalPrice);

                }

            }
        });

        mAddToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Cart selectedItem = new Cart(mName, mQuantity, mTotalPrice);

                if (CartActivity.sSelectedCartItems.size() != 0){

                    Iterator<Cart> iterator = CartActivity.sSelectedCartItems.iterator();

                    while (iterator.hasNext()){

                        String name = iterator.next().getName();

                        if (name.equals(selectedItem.getName())){

                            Toast.makeText(ItemDetailsActivity.this, "This Item is already added to cart", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                }

                CartActivity.sSelectedCartItems.add(selectedItem);
                //mAddToCartBtn.setText("Update Item");
               // mRemoveBtn.setVisibility(View.VISIBLE);

                Toast.makeText(ItemDetailsActivity.this, "Item Added to Cart Successfully", Toast.LENGTH_LONG).show();


                mRemoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if (CartActivity.sSelectedCartItems.size() != 0){

                            Iterator<Cart> iterator = CartActivity.sSelectedCartItems.iterator();

                            while (iterator.hasNext()){

                                mContainedName = iterator.next().getName();

                                if (mName.equals(mContainedName)){

                                    mContainedQty = selectedItem.getQuantity();
                                    mContainedPrice = selectedItem.getPrice();

                                   // pos = CartActivity.sSelectedCartItems.indexOf(new Cart(name, qty, price));

                                }

                            }

                        }

                        Toast.makeText(ItemDetailsActivity.this, "Position: " + mContainedpos + " Price: "+ mContainedPrice + " Quantity: "
                                +mContainedQty, Toast.LENGTH_SHORT).show();

                        //CartActivity.sSelectedCartItems.remove(pos);
                        mRemoveBtn.setVisibility(View.INVISIBLE);
                        mAddToCartBtn.setText("Add to Cart");
                        mQuantity = 1;
                        mTotalPrice = mPrice;

                        mQtyTv.setText("Quantity: "+ mQuantity);
                        mTotalPriceTv.setText("Total Cost: "+ mTotalPrice);

                    }
                });

            }
        });


    }
}
