package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ui.ItemAdapter;
import java.util.List;

public class SnacksFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewItems);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        List<String> items = List.of("Chips", "Cookies", "Nuts", "Chocolate");
        // Set up the adapter and handle item addition via callback
        itemAdapter = new ItemAdapter(items, (itemName, quantity) -> {
            if (getActivity() instanceof DrinksFragment.OnItemAddListener) {
                ((DrinksFragment.OnItemAddListener) getActivity()).onItemAdd(itemName, quantity);
            }
        });
        itemAdapter.refreshQuantities();

        recyclerView.setAdapter(itemAdapter);

        return view;
    }
}
