package com.example.myapplication.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.ui.BasketActivity;
import com.example.myapplication.ui.CategoryPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutCategories);
        viewPager = view.findViewById(R.id.viewPagerItems);

        FloatingActionButton fabBasket = view.findViewById(R.id.fabBasket);
        Button fabChoosePassenger = view.findViewById(R.id.buttonChoosePassenger);


        // Setup the adapter for ViewPager2
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Drinks");
                    break;
                case 1:
                    tab.setText("Snacks");
                    break;
            }
        }).attach();

        fabChoosePassenger.setOnClickListener(v -> {
            // Start ChoosePassengerActivity when the button is clicked
            Intent intent = new Intent(requireActivity(), ChoosePassengerActivity.class);
            startActivityForResult(intent, 100); // Use a request code to handle the result
        });


        // Handle FAB click to navigate to BasketActivity
        fabBasket.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BasketActivity.class);
            startActivity(intent);
        });

        return view;
    }


}
