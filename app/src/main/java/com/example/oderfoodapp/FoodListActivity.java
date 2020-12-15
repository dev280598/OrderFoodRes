package com.example.oderfoodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Database.Database;
import com.example.oderfoodapp.Interface.ItemClickListener;
import com.example.oderfoodapp.Model.Category;
import com.example.oderfoodapp.Model.Food;
import com.example.oderfoodapp.Model.Order;
import com.example.oderfoodapp.ViewHolder.FoodViewHolder;
import com.example.oderfoodapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDB;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        toolbar = findViewById(R.id.toolbar);

        database  = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        localDB = new Database(this);

        recyclerView = findViewById(R.id.rc_food);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_item_from_left);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        if (getIntent() !=null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId!=null)
        {
            if (Common.isConnectedToInternet(getBaseContext())) {
                ActionToolBar();
                loadListFood(categoryId);
            }
            else{
                Toast.makeText(FoodListActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");

        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList){
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {
        Query searchByName = foodList.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, int position, @NonNull Food model) {
                foodViewHolder.txtFoodName.setText(model.getName());
                 Glide.with(getBaseContext()).load(model.getImage()).into(foodViewHolder.FoodImageView);
                final Food local = model;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodListActivity.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(FoodListActivity.this,FoodDetailActivity.class);
                        i.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });
            }


        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                            Food item = postSnapShot.getValue(Food.class);
                            suggestList.add(item.getName());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        Query foodbyId = foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodbyId,Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int position, @NonNull final Food model) {
                foodViewHolder.txtFoodName.setText(model.getName());
                foodViewHolder.txtFoodPrice.setText(model.getPrice());
                 Glide.with(getBaseContext()).load(model.getImage()).into(foodViewHolder.FoodImageView);

                 foodViewHolder.img_cart.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         new Database(getBaseContext()).addToCart(new Order(
                                 adapter.getRef(position).getKey(),
                                 model.getName(),
                                 "1",
                                 model.getPrice(),
                                 model.getDiscount(),
                                 model.getImage()

                         ));
                         Toast.makeText(FoodListActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                     }
                 });

                if (localDB.isFavorites(adapter.getRef(position).getKey()))
                    foodViewHolder.fav_img.setImageResource(R.drawable.fav_red);

                foodViewHolder.fav_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!localDB.isFavorites(adapter.getRef(position).getKey()))
                        {
                            localDB.addFavorites(adapter.getRef(position).getKey());
                            foodViewHolder.fav_img.setImageResource(R.drawable.fav_red);
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+"was added to Favorites", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            localDB.removeFavorites(adapter.getRef(position).getKey());
                            foodViewHolder.fav_img.setImageResource(R.drawable.fav_border);
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+"was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final Food local = model;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodListActivity.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(FoodListActivity.this,FoodDetailActivity.class);
                        i.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });
            }


        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();
    }
}
