package com.example.arnoh.trixinn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private DatabaseReference mMenuRef;

    private List<CategoriesModel> mCategoryList;
    private CategoriesAdapter mAdapter;


    private RecyclerView mMainRecycler;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        FirebaseAuth.getInstance().signInWithEmailAndPassword("moki@gmail.com", "123456");

        mMenuRef = FirebaseDatabase.getInstance().getReference().child("Menu").child("Categories");
        mMenuRef.keepSynced(true);

        mCategoryList = new ArrayList<>();
        mAdapter = new CategoriesAdapter(mCategoryList);

        mMainRecycler = findViewById(R.id.main_menu_recycler);
        mMainRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mMainRecycler.setHasFixedSize(true);
        mMainRecycler.setAdapter(mAdapter);
        mProgress = findViewById(R.id.main_progress);

        mMenuRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                CategoriesModel categories = dataSnapshot.getValue(CategoriesModel.class);

                mCategoryList.add(categories);
                mAdapter.notifyDataSetChanged();

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    //calling the method to check the network connection
    @Override
    protected void onStart() {
        super.onStart();
        if(!checkNetworkConnection(getApplicationContext())) {

            showDialog();

        }

    }


    //alerting the user if application is not connected to wifi
    private void showDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Please Connect To TrixInn Wi-Fi Hotspot To Continue!!!");

        alertDialog.setCancelable(false);
        alertDialog.setMessage("Choose an action");
        //alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialog.setPositiveButton("NO",new Dialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }) ;

        alertDialog.setNegativeButton("OK", new Dialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               //open settings activity
                Intent settings = getPackageManager().getLaunchIntentForPackage("com.android.settings");

                startActivity(settings);
                finish();
            }
        });

        alertDialog.show();
    }



    private boolean checkNetworkConnection(Context context) {

       // boolean test;
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        String ssid =  getSsid(getApplicationContext());
       // Toast.makeText(MainActivity.this, "connected to "+ssid,Toast.LENGTH_LONG).show();

       // Toast.makeText(getApplication(), "connected to  "+ssid , Toast.LENGTH_LONG).show();



        if(isWiFi) {

            return true;
        }else
            return  false;
    }

    private String getSsid(Context context) {

            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (manager.isWifiEnabled()) {
                WifiInfo wifiInfo = manager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        return wifiInfo.getSSID();
                    }
                }
            }
            return null;
        }


    public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>{

        List<CategoriesModel> categoriesList;

        public CategoriesAdapter(List<CategoriesModel> categoriesList) {
            this.categoriesList = categoriesList;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

            CategoryViewHolder viewHolder = new CategoryViewHolder(layoutView);

            return viewHolder;
        }


        @Override
        public void onBindViewHolder(CategoryViewHolder holder, final int position) {

            holder.setImage(MainActivity.this, categoriesList.get(position).getImage());
            holder.setTitle(categoriesList.get(position).getTitle());
            mProgress.setVisibility(View.GONE);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String subMenuKey = categoriesList.get(position).getKey();

                    Intent nextIntent = new Intent(MainActivity.this, SubMenuActivity.class);
                    nextIntent.putExtra("subMenuKey", subMenuKey);
                    startActivity(nextIntent);

                }
            });


        }

        @Override
        public int getItemCount() {
            return categoriesList.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder{

            View mView;
            public CategoryViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            public  void setImage(final Context ctx, final String image){

                final ImageView icon = mView.findViewById(R.id.img_category_icon);

                Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(icon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ctx).load(image).into(icon);

                    }
                });

            }

            public void setTitle(String title){
                TextView tv_title = mView.findViewById(R.id.tv_menu_title);
                tv_title.setText(title);
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            if (CartActivity.sSelectedCartItems.isEmpty()){

                AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(this);
                confirmationAlert.setTitle("Empty Cart")
                        .setMessage("Kindly make your selection from the menus ")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("DISMISS", null).show();

            }else {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
            return true;
        }

        if (id == R.id.action_help){

            startActivity(new Intent(MainActivity.this, HelpActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CartActivity.sSelectedCartItems.clear();

    }
}
