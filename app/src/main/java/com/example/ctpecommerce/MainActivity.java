package com.example.ctpecommerce;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 circularCarousel;
    private ViewPager2 squareCarousel;
    private FrameLayout topBanner;
    private FrameLayout bottomBanner;
    private ImageView topBannerImage;
    private ImageView bottomBannerImage;

    private DrawerLayout drawerLayout; // Sidebar layout
    private NavigationView navigationView; // Sidebar menu
    private ImageView menuIcon; // Hamburger menu button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Existing elements
        circularCarousel = findViewById(R.id.circular_carousel);
        squareCarousel = findViewById(R.id.square_carousel);
        topBanner = findViewById(R.id.top_banner);
        bottomBanner = findViewById(R.id.bottom_banner);

        topBannerImage = new ImageView(this);
        bottomBannerImage = new ImageView(this);

        // Set layout parameters for banners
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, -20, 0, 0);
        topBannerImage.setLayoutParams(layoutParams);
        bottomBannerImage.setLayoutParams(layoutParams);

        // Add banners to layout
        topBanner.addView(topBannerImage);
        bottomBanner.addView(bottomBannerImage);
        topBanner.setVisibility(View.VISIBLE);
        bottomBanner.setVisibility(View.VISIBLE);

        // Image Data
        List<Integer> circularImages = Arrays.asList(
                R.drawable.circle1, R.drawable.circle2, R.drawable.circle3,
                R.drawable.circle4, R.drawable.circle5
        );

        List<String> circularLabels = Arrays.asList(
                "T-Shirt", "Shirt", "Watches", "Shoes", "Pants"
        );

        List<Integer> squareImages = Arrays.asList(
                R.drawable.square1, R.drawable.square2, R.drawable.square3,
                R.drawable.square4, R.drawable.square5
        );

        // Setup carousels
        setupCarousel(circularCarousel, circularImages, circularLabels);
        setupCarousel(squareCarousel, squareImages);

        // Set banner images
        setBannerImage(topBannerImage, R.drawable.female_banner);
        setBannerImage(bottomBannerImage, R.drawable.male_banner);

        // ========== Sidebar Setup ==========
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        // Open sidebar when clicking the menu icon
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Sidebar item selection handling
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_tshirt) {
                Toast.makeText(this, "T-Shirts Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_shirt) {
                Toast.makeText(this, "Shirts Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_pants) {
                Toast.makeText(this, "Pants Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_shoes) {
                Toast.makeText(this, "Shoes Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_watches) {
                Toast.makeText(this, "Watches Selected", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.nav_back){
                drawerLayout.closeDrawers(); // Close the drawer when Back is clicked
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // Method to setup carousel with images and labels
    private void setupCarousel(ViewPager2 viewPager, List<Integer> images, List<String> labels) {
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

    private void setBannerImage(ImageView banner, int imageResId) {
        Glide.with(this)
                .load(imageResId)
                .override(800, 600) // Resize for memory efficiency
                .centerCrop()
                .into(banner);
    }
}
