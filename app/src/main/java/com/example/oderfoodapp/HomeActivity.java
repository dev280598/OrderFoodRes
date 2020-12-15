package com.example.oderfoodapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Database.Database;
import com.example.oderfoodapp.Interface.ItemClickListener;
import com.example.oderfoodapp.Model.Banner;
import com.example.oderfoodapp.Model.Category;
import com.example.oderfoodapp.Model.Food;
import com.example.oderfoodapp.ViewHolder.FoodViewHolder;
import com.example.oderfoodapp.ViewHolder.MenuPopularViewHolder;
import com.example.oderfoodapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import edmt.dev.edmtslider.Animations.DescriptionAnimation;
import edmt.dev.edmtslider.SliderLayout;
import edmt.dev.edmtslider.SliderTypes.BaseSliderView;
import edmt.dev.edmtslider.SliderTypes.TextSliderView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;
    RecyclerView recycler_menu,recycler_popular;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    FirebaseRecyclerAdapter<Category,MenuPopularViewHolder> adapter_popular;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    //Slider
    HashMap<String, String> image_list;
    SliderLayout sliderLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu2();
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu2();
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Init FIrebase
        database = FirebaseDatabase.getInstance();
         category = database.getReference("MostPopular");

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter_popular = new FirebaseRecyclerAdapter<Category, MenuPopularViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuPopularViewHolder viewHolder, final int position, @NonNull Category model) {
                viewHolder.txt_category_name.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.popular_imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int adapterPosition, boolean isLongClick) {

                    }

                });
            }

            @NonNull
            @Override
            public MenuPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_popular_categories_item, parent ,false);
                return new MenuPopularViewHolder(itemView);
            }
        };
//
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FirebaseRecyclerOptions<Category> options_category = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options_category) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int adapterPosition, boolean isLongClick) {
                        Intent foodList = new Intent(HomeActivity.this, FoodListActivity.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }

                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent ,false);
                return new MenuViewHolder(itemView);
            }
        };

        Paper.init(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for User
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullname);
        txtFullName.setText(Common.currentUser.getName());

        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_popular = (RecyclerView) findViewById(R.id.recycler_popular);

        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_item_from_left);
        recycler_menu.setLayoutAnimation(controller);

        //Menu_Popular
        recycler_popular.setHasFixedSize(true);
        recycler_popular.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.HORIZONTAL,false));
        recycler_popular.setLayoutAnimation(controller);

        setupSlider();
    }
    private void setupSlider() {

        image_list = new HashMap<>();
        final SliderLayout sliderLayout = (SliderLayout) findViewById(R.id.slider);



        final DatabaseReference banners = database.getReference("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Banner banner = postSnapShot.getValue(Banner.class);
                    image_list.put(banner.getName()+"@@@"+banner.getId(), banner.getImage());
                }
                for (String key : image_list.keySet())
                {
                    String[] keySplit = key.split("@@@");
                    String nameofFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getApplicationContext());
                    textSliderView
                            .description(nameofFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(HomeActivity.this, FoodDetailActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", idOfFood);

                    sliderLayout.addSlider(textSliderView);
                    banners.removeEventListener(this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
       sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
       sliderLayout.setCustomAnimation(new DescriptionAnimation());
       sliderLayout.setDuration(4000);

    }
    private void loadMenu() {


        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }
    private void loadMenu2() {
        adapter_popular.startListening();
        recycler_popular.setAdapter(adapter_popular);
        swipeRefreshLayout.setRefreshing(false);
        recycler_popular.getAdapter().notifyDataSetChanged();
        recycler_popular.scheduleLayoutAnimation();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter_popular.stopListening();
        //sliderLayout.stopAutoCycle();
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.refesh)
            loadMenu();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_order) {
            Intent orderIntent = new Intent(HomeActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_change_pwd) {
            //Delete Remember user
            showChangePasswordDialog();
        } else if (id == R.id.nav_home_address) {
            //Delete Remember user
            showHomeAddressDialog();
        } else if (id == R.id.nav_signout) {
            //Delete Remember user
            Paper.book().destroy();

            Intent signIn = new Intent (HomeActivity.this, SignInActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information ");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText edtPassword = (MaterialEditText)layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = (MaterialEditText)layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = (MaterialEditText)layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();

                if(edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {
                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password",edtNewPassword.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(HomeActivity.this, "Password was update", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "New Password doesnt match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    private void showHomeAddressDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Home Address");
        alertDialog.setMessage("Please fill all information ");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final MaterialEditText edtHomeAddress = (MaterialEditText)layout_home.findViewById(R.id.edtHomeAddress  );

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(HomeActivity.this, "Update Home Successfull", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null && adapter_popular!=null){
            adapter.startListening();
            adapter_popular.startListening();
        }
        fab.setCount(new Database(this).getCountCart());

    }
}