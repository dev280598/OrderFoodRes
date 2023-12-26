package com.example.oderfoodapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.data.local.Database
import com.example.oderfoodapp.databinding.ActivityFoodListBinding
import com.example.oderfoodapp.`interface`.ItemClickListener
import com.example.oderfoodapp.model.Food
import com.example.oderfoodapp.model.Order
import com.example.oderfoodapp.view.view_holder.FoodViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import java.util.Locale

class FoodListActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private var foodList: DatabaseReference? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var categoryId: String? = ""
    var adapter: FirebaseRecyclerAdapter<Food, FoodViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<Food, FoodViewHolder>? = null
    var suggestList: MutableList<String> = ArrayList()
    var localDB: Database? = null
    private lateinit var binding: ActivityFoodListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_list)
        database = FirebaseDatabase.getInstance()
        foodList = database.getReference("Foods")
        localDB = Database(this)
        binding.rcFood.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.rcFood.layoutManager = layoutManager
        val layoutAnimationController: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(
                binding.rcFood.getContext(),
                R.anim.layout_item_from_left
            )
        binding.rcFood.layoutAnimation = layoutAnimationController
        if (intent != null) categoryId = intent.getStringExtra("CategoryId")
        if (categoryId!!.isNotEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(baseContext)) {
                actionToolBar()
                loadListFood(categoryId!!)
            } else {
                Toast.makeText(
                    this@FoodListActivity,
                    "Please check your connection!!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
        binding.searchBar.setHint("Enter your food")
        loadSuggest()
        binding.searchBar.lastSuggestions = suggestList
        binding.searchBar.setCardViewElevation(10)
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val suggest: MutableList<String> = ArrayList()
                for (search in suggestList) {
                    if (search.lowercase(Locale.getDefault())
                            .contains(binding.searchBar.text.toLowerCase())
                    ) suggest.add(search)
                }
                binding.searchBar.lastSuggestions = suggest
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.searchBar.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) binding.rcFood.adapter = adapter
            }

            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text)
            }

            override fun onButtonClicked(buttonCode: Int) {}
        })
    }

    private fun startSearch(text: CharSequence) {
        val searchByName: Query = foodList?.orderByChild("name")?.equalTo(text.toString())!!
        val foodOptions: FirebaseRecyclerOptions<Food> = FirebaseRecyclerOptions.Builder<Food>()
            .setQuery(searchByName, Food::class.java)
            .build()
        searchAdapter = object : FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.food_item, parent, false)
                return FoodViewHolder(itemView)
            }

            override fun onBindViewHolder(
                foodViewHolder: FoodViewHolder,
                position: Int,
                model: Food
            ) {
                foodViewHolder.txtFoodName.text = model.name
                Glide.with(baseContext).load(model.image).into(foodViewHolder.FoodImageView)
                val local: Food = model
                foodViewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
                        Toast.makeText(this@FoodListActivity, "" + local.name, Toast.LENGTH_SHORT)
                            .show()
                        val i = Intent(this@FoodListActivity, FoodDetailActivity::class.java)
                        i.putExtra("FoodId", searchAdapter?.getRef(position)?.key)
                        startActivity(i)
                    }
                })
            }
        }
        searchAdapter?.startListening()
        binding.rcFood.adapter = searchAdapter
    }

    private fun loadSuggest() {
        foodList?.orderByChild("menuId")?.equalTo(categoryId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapShot in dataSnapshot.children) {
                        val item: Food? = postSnapShot.getValue(Food::class.java)
                        item?.name?.let { suggestList.add(it) }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun loadListFood(categoryId: String) {
        val foodbyId: Query? = foodList?.orderByChild("menuId")?.equalTo(categoryId)
        val options: FirebaseRecyclerOptions<Food> = FirebaseRecyclerOptions.Builder<Food>()
            .setQuery(foodbyId!!, Food::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.food_item, parent, false)
                return FoodViewHolder(itemView)
            }

            override fun onBindViewHolder(
                foodViewHolder: FoodViewHolder,
                position: Int,
                model: Food
            ) {
                foodViewHolder.txtFoodName.text = model.name
                foodViewHolder.txtFoodPrice.text = model.price
                Glide.with(baseContext).load(model.image).into(foodViewHolder.FoodImageView)
                foodViewHolder.img_cart.setOnClickListener {
                    Database(baseContext).addToCart(
                        Order(
                            adapter?.getRef(position)?.key,
                            model.name,
                            "1",
                            model.price,
                            model.discount,
                            model.image
                        )
                    )
                    Toast.makeText(this@FoodListActivity, "Added to cart", Toast.LENGTH_SHORT)
                        .show()
                }
                if (localDB?.isFavorites(
                        adapter?.getRef(position)?.key
                    ) == true
                ) foodViewHolder.fav_img.setImageResource(R.drawable.fav_red)
                foodViewHolder.fav_img.setOnClickListener {
                    if (!localDB?.isFavorites(adapter?.getRef(position)?.key)!!) {
                        localDB?.addFavorites(adapter?.getRef(position)?.key)
                        foodViewHolder.fav_img.setImageResource(R.drawable.fav_red)
                        Toast.makeText(
                            this@FoodListActivity,
                            "" + model.name + "was added to Favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        localDB?.removeFavorites(adapter?.getRef(position)?.key)
                        foodViewHolder.fav_img.setImageResource(R.drawable.fav_border)
                        Toast.makeText(
                            this@FoodListActivity,
                            "" + model.name + "was removed from Favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val local: Food = model
                foodViewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
                        Toast.makeText(this@FoodListActivity, "" + local.name, Toast.LENGTH_SHORT)
                            .show()
                        val i = Intent(this@FoodListActivity, FoodDetailActivity::class.java)
                        i.putExtra("FoodId", adapter?.getRef(position)?.key)
                        startActivity(i)
                    }
                })
            }
        }
        adapter?.startListening()
        binding.rcFood.adapter = adapter
    }

    private fun actionToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) adapter?.startListening()
    }
}