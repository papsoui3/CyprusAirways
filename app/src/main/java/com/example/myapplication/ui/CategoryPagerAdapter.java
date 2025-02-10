package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryPagerAdapter extends FragmentStateAdapter {
    public CategoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DrinksFragment();
            case 1:
                return new SnacksFragment();
            default:
                return new DrinksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of categories
    }
}
