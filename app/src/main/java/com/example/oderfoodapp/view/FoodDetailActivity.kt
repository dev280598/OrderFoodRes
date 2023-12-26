package com.example.oderfoodapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.data.local.Database
import com.example.oderfoodapp.databinding.ActivityFoodDetailBinding
import com.example.oderfoodapp.model.Food
import com.example.oderfoodapp.model.Order
import com.example.oderfoodapp.model.Rating
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class FoodDetailActivity : AppCompatActivity() {
    var foodId = ""
    lateinit var database: FirebaseDatabase
    var foods: DatabaseReference? = null
    var ratingTbl: DatabaseReference? = null
    var currentFood: Food? = null
    lateinit var binding: ActivityFoodDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_detail)
        database = FirebaseDatabase.getInstance()
        foods = database.getReference("Foods")
        ratingTbl = database.getReference("Rating")
        binding.btnShowComment.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FoodDetailActivity, ShowComment_Activity::class.java)
            intent.putExtra(Common.INTENT_FOOD_ID, foodId)
            startActivity(intent)
        })
        binding.btnRating.setOnClickListener { showRatingDialog() }
        binding.btnAddToCart.setOnClickListener {
            Database(baseContext).addToCart(
                Order(
                    foodId,
                    currentFood?.name,
                    "1",
                    currentFood?.price,
                    currentFood?.discount,
                    currentFood?.image
                )
            )
            binding.btnCart.count = Database(baseContext).countCart
            Toast.makeText(this@FoodDetailActivity, "Added to cart", Toast.LENGTH_SHORT).show()
        }
        binding.btnCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FoodDetailActivity, CartActivity::class.java)
            startActivity(intent)
        })
        binding.collapsing.setCollapsedTitleTextAppearance(R.style.ExpandedAppbar)
        binding.collapsing.setCollapsedTitleTextAppearance(R.style.CollapseAppbar)
        if (intent != null) foodId = intent.getStringExtra("FoodId").toString()
        if (!foodId.isEmpty()) {
            if (Common.isConnectedToInternet(baseContext)) {
                actionToolBar()
                getDetailFood(foodId)
                getRatingFood(foodId)
            } else {
                Toast.makeText(
                    this@FoodDetailActivity,
                    "Please check your connection!!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
    }

    private fun getRatingFood(foodId: String) {
        val foodRating: Query? = ratingTbl?.orderByChild("foodId")?.equalTo(foodId)
        foodRating?.addValueEventListener(object : ValueEventListener {
            var count = 0
            var sum = 0
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapShot in dataSnapshot.children) {
                    val item: Rating? = postSnapShot.getValue(
                        Rating::class.java
                    )
                    sum += item?.rateValue!!.toInt()
                    count++
                }
                if (count != 0) {
                    val average = (sum / count).toFloat()
                    binding.ratingBar.rating = average
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showRatingDialog() {
//        Builder()
//            .setPositiveButtonText("Sumit")
//            .setNegativeButtonText("Cancel")
//            .setNoteDescriptions(
//                Arrays.asList(
//                    "Verry Bad",
//                    "Nod Good",
//                    "Quite Ok",
//                    "Very Good",
//                    "Excellent"
//                )
//            )
//            .setDefaultRating(1)
//            .setTitle("Rate this food")
//            .setDescription("Please select some stars and give your feedback")
//            .setTitleTextColor(R.color.colorPrimary)
//            .setHint("Please write your comment here...")
//            .setHintTextColor(R.color.colorAccent)
//            .setCommentTextColor(android.R.color.white)
//            .setCommentBackgroundColor(R.color.colorPrimaryDark)
//            .setWindowAnimation(R.style.RatingDialogFadeAnim)
//            .create(this@FoodDetailActivity)
//            .show()
    }

    private fun getDetailFood(foodId: String) {
        foods?.child(foodId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentFood = dataSnapshot.getValue(Food::class.java)
                Glide.with(baseContext).load(currentFood?.image).into(binding.imgFooddetail)
                binding.collapsing.title = currentFood?.name
                binding.foodPrice?.text = currentFood?.price
                binding.foodName?.text = currentFood?.name
                binding.foodDescription?.text = currentFood?.description
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menucart) {
            val intent = Intent(this@FoodDetailActivity, CartActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun onNegativeButtonClicked() {}
    fun onPositiveButtonClicked(i: Int, s: String) {
        val rating = Rating(
            Common.currentUser!!.phone,
            foodId, i.toString(),
            s
        )
        Common.currentUser!!.phone?.let {
            ratingTbl?.child(it)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child(it).exists()) {
                            ratingTbl?.child(it)?.removeValue()
                            ratingTbl?.child(it)?.setValue(rating)
                        } else {
                            ratingTbl?.child(it)?.setValue(rating)
                        }
                        Toast.makeText(
                            this@FoodDetailActivity,
                            "Thank you for submit rating!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    private fun actionToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        binding.btnCart.count = Database(this).countCart
    }
}