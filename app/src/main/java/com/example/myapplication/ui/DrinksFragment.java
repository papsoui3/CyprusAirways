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

import java.util.List;


public class DrinksFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<String> items = List.of("Coke", "Water", "Juice", "Pepsi");

        itemAdapter = new ItemAdapter(items, (itemName, quantity) -> {
            if (getActivity() instanceof OnItemAddListener) {
                ((OnItemAddListener) getActivity()).onItemAdd(itemName, quantity);
            }
        });

        recyclerView.setAdapter(itemAdapter);

        return view;
    }
    //This is useful if item quantities change while the user is on another screen (e.g., returning from a checkout page).
    @Override
    public void onResume() {
        super.onResume();
        if (itemAdapter != null) {
            itemAdapter.refreshQuantities();
        }
    }

    public interface OnItemAddListener {
        void onItemAdd(String itemName, int quantity);
    }
}

