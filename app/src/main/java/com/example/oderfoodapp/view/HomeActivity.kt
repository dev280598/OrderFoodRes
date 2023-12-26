package com.example.oderfoodapp.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.example.oderfoodapp.R
import com.example.oderfoodapp.data.local.Database
import com.example.oderfoodapp.databinding.ActivityHomeBinding
import com.example.oderfoodapp.databinding.ContentHomeBinding
import com.example.oderfoodapp.databinding.HomeAddressLayoutBinding
import com.example.oderfoodapp.databinding.NavHeaderHomeBinding
import com.example.oderfoodapp.model.Banner
import com.example.oderfoodapp.model.Category
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.view.adapter.MenuAdapter
import com.example.oderfoodapp.view.adapter.MenuPopularAdapter
import com.example.oderfoodapp.view.view_holder.MenuPopularViewHolder
import com.example.oderfoodapp.view.view_holder.MenuViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rengwuxian.materialedittext.MaterialEditText
import dagger.android.support.DaggerAppCompatActivity
import dmax.dialog.SpotsDialog
import edmt.dev.edmtslider.Animations.DescriptionAnimation
import edmt.dev.edmtslider.SliderLayout
import edmt.dev.edmtslider.SliderTypes.BaseSliderView
import edmt.dev.edmtslider.SliderTypes.TextSliderView
import io.paperdb.Paper
import javax.inject.Inject

class HomeActivity @Inject constructor() : DaggerAppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    @Inject
    lateinit var database: FirebaseDatabase

    @Inject
    lateinit var requestManager: RequestManager
    var adapter: FirebaseRecyclerAdapter<Category, MenuViewHolder>? = null
    var adapterPopular: FirebaseRecyclerAdapter<Category, MenuPopularViewHolder>? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var contentHome: ContentHomeBinding

    var imageList: HashMap<String, String> = hashMapOf()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.appbarView.toolbar)
        contentHome = binding.appbarView.contentHome
        contentHome.swipeLayout.setColorSchemeColors(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
        contentHome.swipeLayout.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                if (Common.isConnectedToInternet(this@HomeActivity)) {
                    loadMenuPopular()
                    loadMenu()
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.check_your_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        })
        contentHome.swipeLayout.post(Runnable {
            if (Common.isConnectedToInternet(this)) {
                loadMenuPopular()
                loadMenu()
            } else {
                Toast.makeText(this, getString(R.string.check_your_connection), Toast.LENGTH_SHORT)
                    .show()
                return@Runnable
            }
        })

        //Init Firebase
        val categoryPop = database.getReference("MostPopular")
        val options: FirebaseRecyclerOptions<Category> = FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(categoryPop, Category::class.java)
            .build()
        adapterPopular = MenuPopularAdapter(options)
        Log.e("=====Common","${Common.currentUser?.name}")
        val category = database.getReference("Category")
        val optionsCategory: FirebaseRecyclerOptions<Category> =
            FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category::class.java)
                .build()
        adapter = MenuAdapter(optionsCategory) { position ->
            val foodList = Intent(this@HomeActivity, FoodListActivity::class.java)
            foodList.putExtra("CategoryId", adapter?.getRef(position)?.key)
            startActivity(foodList)
        }

        Paper.init(this)
        binding.appbarView.fab.setOnClickListener {
            val cartIntent = Intent(this@HomeActivity, CartActivity::class.java)
            startActivity(cartIntent)
        }
        binding.appbarView.fab.count = Database(this).countCart
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appbarView.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        //Set Name for User
        val bindingHeader = NavHeaderHomeBinding.inflate(layoutInflater)
        bindingHeader.fullName = Common.currentUser!!.name
        binding.navView.addHeaderView(bindingHeader.root)

        contentHome.recyclerMenu.layoutManager = GridLayoutManager(this, 2)
        val controller: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(
            contentHome.recyclerMenu.context,
            R.anim.layout_item_from_left
        )
        contentHome.recyclerMenu.layoutAnimation = controller

        //Menu_Popular
        contentHome.recyclerPopular.setHasFixedSize(true)
        contentHome.recyclerPopular.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )
        contentHome.recyclerPopular.layoutAnimation = controller
        setupSlider()
    }

    private fun setupSlider() {
        val banners: DatabaseReference = database.getReference("Banner")
        banners.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapShot in dataSnapshot.children) {
                    val banner: Banner? = postSnapShot.getValue(Banner::class.java)
                    if (banner != null) {
                        imageList[banner.name + "@@@" + banner.id] = banner.image.toString()
                    }
                }
                for (key in imageList.keys) {
                    val keySplit =
                        key.split("@@@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val nameOfFood = keySplit[0]
                    val idOfFood = keySplit[1]

                    //Create Slider
                    val textSliderView = TextSliderView(applicationContext)
                    textSliderView
                        .description(nameOfFood)
                        .image(imageList[key])
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener {
                            val intent =
                                Intent(this@HomeActivity, FoodDetailActivity::class.java)
                            intent.putExtras(textSliderView.bundle)
                            startActivity(intent)
                        }
                    textSliderView.bundle(Bundle())
                    textSliderView.bundle.putString("FoodId", idOfFood)
                    contentHome.slider.addSlider(textSliderView)
                    banners.removeEventListener(this)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        contentHome.slider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground)
        contentHome.slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        contentHome.slider.setCustomAnimation(DescriptionAnimation())
        contentHome.slider.setDuration(4000)
    }

    private fun loadMenu() {
        adapter?.startListening()
        contentHome.recyclerMenu.adapter = adapter
        contentHome.swipeLayout.isRefreshing = false
        contentHome.recyclerMenu.adapter?.notifyDataSetChanged()
        contentHome.recyclerMenu.scheduleLayoutAnimation()
    }

    private fun loadMenuPopular() {
        adapterPopular?.startListening()
        contentHome.recyclerPopular.adapter = adapterPopular
        contentHome.swipeLayout.isRefreshing = false
        contentHome.recyclerPopular.adapter?.notifyDataSetChanged()
        contentHome.recyclerPopular.scheduleLayoutAnimation()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
        adapterPopular?.stopListening()
        //sliderLayout.stopAutoCycle();
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refesh) loadMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu -> {
            }

            R.id.nav_cart -> {
                val cartIntent = Intent(this@HomeActivity, CartActivity::class.java)
                startActivity(cartIntent)
            }

            R.id.nav_order -> {
                val orderIntent = Intent(this@HomeActivity, OrderStatusActivity::class.java)
                startActivity(orderIntent)
            }

            R.id.nav_change_pwd -> {
                //Delete Remember user
                showChangePasswordDialog()
            }

            R.id.nav_home_address -> {
                //Delete Remember user
                showHomeAddressDialog()
            }

            R.id.nav_signout -> {
                //Delete Remember user
                Paper.book().destroy()
                val signIn = Intent(this@HomeActivity, SignInActivity::class.java)
                signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(signIn)
            }
        }
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showChangePasswordDialog() {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this@HomeActivity)
        alertDialog.setTitle("Change Password")
        alertDialog.setMessage("Please fill all information ")
        val inflater: LayoutInflater = LayoutInflater.from(this)
        val layout_pwd: View = inflater.inflate(R.layout.change_password_layout, null)
        val edtPassword: MaterialEditText =
            layout_pwd.findViewById<View>(R.id.edtPassword) as MaterialEditText
        val edtNewPassword: MaterialEditText =
            layout_pwd.findViewById<View>(R.id.edtNewPassword) as MaterialEditText
        val edtRepeatPassword: MaterialEditText =
            layout_pwd.findViewById<View>(R.id.edtRepeatPassword) as MaterialEditText
        alertDialog.setView(layout_pwd)
        alertDialog.setPositiveButton(
            "CHANGE"
        ) { _, _ ->
            val waitingDialog: AlertDialog = SpotsDialog(this@HomeActivity)
            waitingDialog.show()
            if (edtPassword.text.toString() == Common.currentUser!!.password) {
                if (edtNewPassword.text.toString() == edtRepeatPassword.text
                        .toString()
                ) {
                    val passwordUpdate: MutableMap<String, Any> = HashMap()
                    passwordUpdate["password"] = edtNewPassword.text.toString()
                    val user: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference("User")
                    Common.currentUser!!.phone?.let {
                        user.child(it)
                            .updateChildren(passwordUpdate)
                            .addOnCompleteListener {
                                waitingDialog.dismiss()
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Password was update",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                } else {
                    waitingDialog.dismiss()
                    Toast.makeText(
                        this@HomeActivity,
                        "New Password doesnt match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                waitingDialog.dismiss()
                Toast.makeText(this@HomeActivity, "Wrong old password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        alertDialog.setNegativeButton(
            "CANCEL"
        ) { dialogInterface, which -> dialogInterface.dismiss() }
        alertDialog.show()
    }

    private fun showHomeAddressDialog() {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this@HomeActivity)
        alertDialog.setTitle("Change Home Address")
        alertDialog.setMessage("Please fill all information ")
        val layoutHomeBinding = HomeAddressLayoutBinding.inflate(LayoutInflater.from(this))
        alertDialog.setView(layoutHomeBinding.root)
        alertDialog.setPositiveButton(
            "UPDATE"
        ) { dialog, _ ->
            dialog.dismiss()
            Common.currentUser!!.homeAddress = layoutHomeBinding.edtHomeAddress.text.toString()
            Common.currentUser!!.phone?.let {
                database.getReference("User")
                    .child(it)
                    .setValue(Common.currentUser)
                    .addOnCompleteListener {
                        Toast.makeText(
                            this@HomeActivity,
                            "Update Home Successfull",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
//        if (adapter != null && adapterPopular != null) {
//            adapter!!.startListening()
//            adapterPopular!!.startListening()
//        }
//        binding.appbarView.fab.count = Database(this).countCart
    }
}