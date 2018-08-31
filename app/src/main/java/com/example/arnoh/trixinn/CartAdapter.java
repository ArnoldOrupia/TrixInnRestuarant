package com.example.arnoh.trixinn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ARNOH on 5/9/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    List<Cart> mCartList;
    Context context;

    public CartAdapter(List<Cart> cartList, Context context){

        this.mCartList = cartList;
        this.context = context;

    }

    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);

        CartViewHolder viewHolder = new CartViewHolder(layoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CartAdapter.CartViewHolder holder, final int position) {

        final Cart selectedFood = mCartList.get(position);

        holder.mNameTv.setText(selectedFood.getName());
        holder.mPriceTv.setText("Price: "+selectedFood.getPrice());
        holder.mQuantityTv.setText("Quantity: "+ selectedFood.getQuantity());
        holder.mRemoveIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(context);
                confirmationAlert.setTitle("Remove From Cart")
                        .setMessage("Do you really want remove "+ selectedFood.getName())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                CartActivity.sSelectedCartItems.remove(position);
                                CartActivity.sCartAdapter.notifyDataSetChanged();
                                Toast.makeText(context, selectedFood.getName()+" Removed Successfully", Toast.LENGTH_SHORT).show();

                                double mTotalAmount = 0;

                                for (Cart cartItem : CartActivity.sSelectedCartItems){

                                    mTotalAmount += cartItem.getPrice();

                                }

                                CartActivity.mTotalAmountTv.setText("Total Amount to be Paid: KSh" + mTotalAmount);

                            }})
                        .setNegativeButton("NO", null).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        View mView;

        private TextView mNameTv, mPriceTv, mQuantityTv;
        private ImageView mRemoveIv;

        public CartViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mNameTv = itemView.findViewById(R.id.tv_cart_food_name);
            mPriceTv = itemView.findViewById(R.id.tv_cart_food_price);
            mQuantityTv = itemView.findViewById(R.id.tv_cart_quantity);
            mRemoveIv = itemView.findViewById(R.id.iv_remove_item);
        }
    }
}
