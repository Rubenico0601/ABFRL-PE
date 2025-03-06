package com.example.ctpecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.variables.Var;
import com.clevertap.android.sdk.variables.callbacks.FetchVariablesCallback;
import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 circularCarousel;
    private ViewPager2 squareCarousel;
    private FrameLayout topBanner;
    private FrameLayout bottomBanner;
    private ImageView topBannerImage;
    private ImageView bottomBannerImage;
    private Switch genderToggle;
    private List<String> carouselImages;
    private DrawerLayout drawerLayout; // Sidebar layout
    private NavigationView navigationView; // Sidebar menu
    private ImageView menuIcon; // Hamburger menu button
    CleverTapAPI cleverTapAPI;
    private Var<String> genderTheme;
    EditText searchBarTop, searchBarBottom;
    private Var<Boolean> isMale;
    private SharedPreferences sharedPreferences;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cleverTapAPI = CleverTapAPI.getDefaultInstance(this);
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        List<String> searchSuggestions = new ArrayList<>();
        searchSuggestions.add("Pants");
        searchSuggestions.add("Jeans");
        searchSuggestions.add("Kurta");
        searchSuggestions.add("Saree");
        searchSuggestions.add("Tops");
        searchSuggestions.add("Shoes");
        searchSuggestions.add("Shirts");
        searchSuggestions.add("Watch");

         adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, searchSuggestions);

        genderTheme = cleverTapAPI.defineVariable("ABFRL.genderTheme", "str");
        isMale = cleverTapAPI.defineVariable("ABFRL.isMale", true);

        cleverTapAPI.syncVariables();

        // Fetch variables from CleverTap
        cleverTapAPI.fetchVariables(new FetchVariablesCallback() {
            @Override
            public void onVariablesFetched(boolean isSuccess) {
                if (isSuccess) {
                    runOnUiThread(() -> {
                        handleFetchedVariables();
                        });
                }
            }
        });

        genderToggle = findViewById(R.id.gender_toggle);

        // Existing elements
        searchBarBottom = findViewById(R.id.search_bar_bottom);
        searchBarTop = findViewById(R.id.search_bar_top);
        circularCarousel = findViewById(R.id.circular_carousel);
        squareCarousel = findViewById(R.id.square_carousel);

        bottomBanner = findViewById(R.id.bottom_banner);


        bottomBannerImage = new ImageView(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TogglePrefs", MODE_PRIVATE);

        // Retrieve and apply the saved toggle state (default is false -> Male)
        boolean isFemaleSelected = sharedPreferences.getBoolean("genderState", false);
        genderToggle.setChecked(isFemaleSelected);
        updateGenderView(isFemaleSelected);



        // Listen for toggle switch changes
        genderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update UI based on the toggle state
                updateGenderView(isChecked);

                // Save the state in SharedPreferences
                saveToggleState(isChecked);
            }
        });

        List<Integer> squareImages = Arrays.asList(
                R.drawable.square1, R.drawable.square2, R.drawable.square3,
                R.drawable.square4, R.drawable.square5
        );

        setupCarousel(squareCarousel, squareImages);

    }

    private void handleFetchedVariables() {
        try {
            Var<String> themeData = cleverTapAPI.getVariable("ABFRL.genderTheme");
            Var<Boolean> isMale = cleverTapAPI.getVariable("ABFRL.isMale");
            setupActivityScreen(isMale,themeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupActivityScreen(Var<Boolean> isMale, Var<String> themeData) throws JSONException {
        JSONObject themeObject = new JSONObject(themeData.stringValue());
        Boolean isMaleUser = isMale.value();

        if(!isMaleUser) {

            try {

                String themeGrid = themeObject.getString("gridview");
                String autoText = themeObject.getString("autoText");
                String themeColor = themeObject.getString("theme_color");
                findViewById(R.id.main_layout).setBackgroundColor(Color.parseColor(themeColor));
                if ("true".equalsIgnoreCase(autoText)) {
                    AutoCompleteTextView searchBarTop = findViewById(R.id.search_bar_top_auto);
                    searchBarTop.setVisibility(View.VISIBLE);
                    searchBarTop.setAdapter(adapter);
                }else{
                    findViewById(R.id.search_bar_top).setVisibility(View.VISIBLE);
                }
                // Load Top Banner Image
                topBanner = findViewById(R.id.top_banner);
                topBannerImage = new ImageView(this);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, -20, 0, 0);
                topBannerImage.setLayoutParams(layoutParams);

                topBanner.addView(topBannerImage);
                findViewById(R.id.female).setVisibility(View.VISIBLE);
                topBanner.setVisibility(View.VISIBLE);

                setBannerImage(topBannerImage, themeObject.getString("female_banner"));


                // Adjust UI layout based on "sections"
                if ("true".equalsIgnoreCase(themeGrid)) {
                    setupGridView(themeData);
                } else {
                    setupNonGridView(themeData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            String themeGrid = themeObject.getString("gridview");
            String autoText = themeObject.getString("autoText");
            String themeColor = themeObject.getString("theme_color");
            findViewById(R.id.main_layout).setBackgroundColor(Color.parseColor(themeColor));


            // Load Top Banner Image
            bottomBanner = findViewById(R.id.bottom_banner);
            bottomBannerImage = new ImageView(this);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, -20, 0, 0);
            bottomBannerImage.setLayoutParams(layoutParams);

            bottomBanner.addView(bottomBannerImage);
            findViewById(R.id.men).setVisibility(View.VISIBLE);
            bottomBanner.setVisibility(View.VISIBLE);

            setBannerImage(bottomBannerImage, themeObject.getString("male_banner"));

            if ("true".equalsIgnoreCase(autoText)) {
                AutoCompleteTextView searchBarBottom = findViewById(R.id.search_bar_bottom_auto);
                searchBarBottom.setVisibility(View.VISIBLE);
                searchBarBottom.setAdapter(adapter);
            }else{
                findViewById(R.id.search_bar_bottom).setVisibility(View.VISIBLE);
            }

            // Adjust UI layout based on "sections"
            if ("true".equalsIgnoreCase(themeGrid)) {
                setupGridView(themeData);
            } else {
                setupNonGridView(themeData);
            }

        }
    }

    private void setupGridView(Var<String> themeData)throws JSONException{
        JSONObject themeObject = new JSONObject(themeData.stringValue());

            findViewById(R.id.categories).setVisibility(View.VISIBLE);
            findViewById(R.id.circular_carousel).setVisibility(View.VISIBLE);
            // Load carousel images
            List<String> circularImages = Arrays.asList(
                    themeObject.getString("img1"), themeObject.getString("img2"), themeObject.getString("img3"),
                    themeObject.getString("img4"), themeObject.getString("img5")
            );

            List<String> circularLabels = Arrays.asList(
                    themeObject.getString("label1"), themeObject.getString("label2"), themeObject.getString("label3"),
                    themeObject.getString("label4"), themeObject.getString("label5")
            );

            // Setup carousels
            setupCarousel(circularCarousel, circularImages, circularLabels);

    }

    private void setupNonGridView(Var<String> themeData)throws JSONException{
        JSONObject themeObject = new JSONObject(themeData.stringValue());
        findViewById(R.id.menu_icon).setVisibility(View.VISIBLE);

        Log.d("DEBUG", "setupNonGridView called, menu icon should be visible now.");


        // ========== Sidebar Setup ==========
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        // Setup carousels
        setMenuItemIconFromUrl(this, navigationView.getMenu().findItem(R.id.nav_tshirt), themeObject.getString("imgIcon1"));
        navigationView.getMenu().findItem(R.id.nav_shirt).setTitle(themeObject.getString("label1"));
        setMenuItemIconFromUrl(this, navigationView.getMenu().findItem(R.id.nav_shirt), themeObject.getString("imgIcon2"));
        navigationView.getMenu().findItem(R.id.nav_tshirt).setTitle(themeObject.getString("label2"));
        setMenuItemIconFromUrl(this, navigationView.getMenu().findItem(R.id.nav_pants), themeObject.getString("imgIcon3"));
        navigationView.getMenu().findItem(R.id.nav_pants).setTitle(themeObject.getString("label3"));
        setMenuItemIconFromUrl(this, navigationView.getMenu().findItem(R.id.nav_shoes), themeObject.getString("imgIcon4"));
        navigationView.getMenu().findItem(R.id.nav_shoes).setTitle(themeObject.getString("label4"));
        setMenuItemIconFromUrl(this, navigationView.getMenu().findItem(R.id.nav_watches), themeObject.getString("imgIcon5"));
        navigationView.getMenu().findItem(R.id.nav_watches).setTitle(themeObject.getString("label5"));

        // Open sidebar when clicking the menu icon
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        // Sidebar item selection handling
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            try {
            if (id == R.id.nav_tshirt) {
                Toast.makeText(this, themeObject.getString("label1") + " Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_shirt) {
                Toast.makeText(this, themeObject.getString("label2") + " Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_pants) {
                Toast.makeText(this, themeObject.getString("label3") + " Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_shoes) {
                Toast.makeText(this, themeObject.getString("label4") + " Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_watches) {
                Toast.makeText(this, themeObject.getString("label5") + " Selected", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.nav_back){
                drawerLayout.closeDrawers(); // Close the drawer when Back is clicked
            }

            drawerLayout.closeDrawers();
            return true;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void setMenuItemIconFromUrl(Context context, MenuItem menuItem, String imageUrl) {
        ImageView iconView = new ImageView(context);
        iconView.setLayoutParams(new ViewGroup.LayoutParams(100, 100)); // Set size

        // Load the image dynamically from URL using Glide
        Glide.with(context)
                .load(imageUrl)
                .override(100, 100)  // Resize as needed
                .into(iconView);

        // Set the custom ImageView as the menu item icon
        menuItem.setActionView(iconView);
    }


    // Method to setup carousel with images and labels
    private void setupCarousel(ViewPager2 viewPager, List<String> images, List<String> labels) {
        CarouselAdapter adapter = new CarouselAdapter(images, labels);
        configureViewPager(viewPager, adapter);
    }

    // Method to setup carousel with images only
    private void setupCarousel(ViewPager2 viewPager, List<Integer> images) {
        CarouselAdapter adapter = new CarouselAdapter(images);
        configureViewPager(viewPager, adapter);
    }

    // Configuration method for ViewPager2
    private void configureViewPager(ViewPager2 viewPager, CarouselAdapter adapter) {
        viewPager.setAdapter(adapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);

        // Prevents over-scroll effect
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int itemWidth = (int) (screenWidth * 0.7);
        int sidePadding = (screenWidth - itemWidth) / 2;

        viewPager.setPadding(sidePadding, 0, sidePadding, 0);

        // Page transformer for carousel effect
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40)); // Space between items
        transformer.addTransformer((page, position) -> {
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
        });

        viewPager.setPageTransformer(transformer);
    }

    private void setBannerImage(ImageView banner, String ImageURL) {
        Glide.with(this)
                .load(ImageURL)
                .override(800, 600) // Resize for memory efficiency
                .centerCrop()
                .into(banner);
    }

    // Function to update UI based on gender selection
    private void updateGenderView(boolean isFemaleSelected) {
        if (isFemaleSelected) {
            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("User Gender", "Male");
            cleverTapAPI.pushProfile(profileUpdate);
            Toast.makeText(this, "Male selected", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("User Gender", "Female");
            cleverTapAPI.pushProfile(profileUpdate);
            Toast.makeText(this, "Female selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to save toggle state in SharedPreferences
    private void saveToggleState(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("genderState", isChecked);
        editor.apply();
    }
}
