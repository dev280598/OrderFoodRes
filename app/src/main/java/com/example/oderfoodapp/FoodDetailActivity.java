package com.example.oderfoodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Database.Database;
import com.example.oderfoodapp.Model.Food;
import com.example.oderfoodapp.Model.Order;
import com.example.oderfoodapp.Model.Rating;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Arrays;

import info.hoang8f.widget.FButton;

public class FoodDetailActivity extends AppCompatActivity implements RatingDialogListener {
    TextView txt_foofname,txt_price,txt_description;
    ImageView imgFood;
    FButton btnShowCmt;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    FloatingActionButton btn_rating;
    CounterFab btn_addcart;
    Button btn_addtoCart;
    RatingBar ratingBar;
    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    Food currentFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTbl = database.getReference("Rating");

        btn_addtoCart = findViewById(R.id.btn_addToCart);
        btn_addcart = findViewById(R.id.btnCart);
        btn_rating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);
        toolbar = findViewById(R.id.toolbar);
        btnShowCmt=findViewById(R.id.btnShowComment);
        btnShowCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetailActivity.this, ShowComment_Activity.class);
                intent.putExtra(Common.INTENT_FOOD_ID, foodId);
                startActivity(intent);
            }
        });



        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });
        btn_addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        "1",
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()));
                btn_addcart.setCount(new Database(getBaseContext()).getCountCart());
                Toast.makeText(FoodDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }

        });


        btn_addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(FoodDetailActivity.this,CartActivity.class);
               startActivity(intent);
            }
        });


        txt_description = findViewById(R.id.food_description);
        txt_foofname = findViewById(R.id.food_name);
        txt_price = findViewById(R.id.food_price);
        imgFood = findViewById(R.id.img_fooddetail);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        if(getIntent() !=null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()){
            if (Common.isConnectedToInternet(getBaseContext()))
            {
                ActionToolBar();
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
            else{
                Toast.makeText(FoodDetailActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0 ,sum =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                    Rating item = postSnapShot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Sumit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Verry Bad","Nod Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetailActivity.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                 Glide.with(getBaseContext()).load(currentFood.getImage()).into(imgFood);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                txt_price.setText(currentFood.getPrice());
                txt_foofname.setText(currentFood.getName());
                txt_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menucart) {
            Intent intent = new Intent(FoodDetailActivity.this, CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(i),
                s);
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                else {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetailActivity.this, "Thank you for submit rating!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        btn_addcart.setCount(new Database(this).getCountCart());
    }
}
