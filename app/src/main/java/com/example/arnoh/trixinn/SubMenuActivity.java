package com.example.arnoh.trixinn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubMenuActivity extends AppCompatActivity {

    private DatabaseReference mSubMenuRef;
    private RecyclerView mSubmenuRv;

    private List<SubMenu> subMenuList = new ArrayList<>();
    private SubMenuAdapter mSubMenuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);

        String key = getIntent().getStringExtra("subMenuKey");

        mSubMenuRef = FirebaseDatabase.getInstance().getReference().child("Menu").child("SubMenu").child(key);
        mSubMenuRef.keepSynced(true);

        mSubMenuAdapter = new SubMenuAdapter(subMenuList);

        mSubmenuRv = findViewById(R.id.rv_submenu);
        mSubmenuRv.setHasFixedSize(true);
        mSubmenuRv.setLayoutManager(new LinearLayoutManager(this));
        mSubmenuRv.setAdapter(mSubMenuAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        subMenuList.clear();

        mSubMenuRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                SubMenu subMenu = dataSnapshot.getValue(SubMenu.class);

                subMenuList.add(subMenu);
                mSubMenuAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class SubMenuAdapter extends RecyclerView.Adapter<SubMenuAdapter.SubMenuViewHolder>{

        List<SubMenu> foodList;

        public SubMenuAdapter(List<SubMenu> foodList){

            this.foodList = foodList;

        }

        @Override
        public SubMenuAdapter.SubMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_menu, parent, false);

            SubMenuViewHolder viewHolder = new SubMenuViewHolder(layoutView);
            viewHolder.setIsRecyclable(false);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SubMenuAdapter.SubMenuViewHolder holder, int position) {

            final SubMenu foods = foodList.get(position);

            if (CartActivity.sSelectedCartItems.size() != 0){

                Iterator<Cart> iterator = CartActivity.sSelectedCartItems.iterator();

                while (iterator.hasNext()){

                    String name = iterator.next().getName();

                    if (name.equals(foodList.get(position).getName())){

                        holder.mSelected.setVisibility(View.VISIBLE);

                    }

                }

            }

            holder.mFoodPriceTv.setText(""+foods.getPrice());
            holder.mFoodNameTv.setText(foods.getName());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent detailsIntent = new Intent(SubMenuActivity.this, ItemDetailsActivity.class);
                    detailsIntent.putExtra("name", foods.getName());
                    detailsIntent.putExtra("price", foods.getPrice());
                    startActivity(detailsIntent);

                }
            });


        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        public class SubMenuViewHolder extends RecyclerView.ViewHolder{

            View mView;

            private TextView mFoodNameTv, mFoodPriceTv;
            private ImageView mSelected;

            public SubMenuViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mFoodNameTv = itemView.findViewById(R.id.tv_food_name);
                mFoodPriceTv = itemView.findViewById(R.id.tv_food_price);
                mSelected = itemView.findViewById(R.id.iv_selected);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_cart) {

            if (CartActivity.sSelectedCartItems.isEmpty()){

                AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(this);
                confirmationAlert.setTitle("Empty Cart")
                        .setMessage("Kindly make your selection from the menus ")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("DISMISS", null).show();

            }else {
                startActivity(new Intent(SubMenuActivity.this, CartActivity.class));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
