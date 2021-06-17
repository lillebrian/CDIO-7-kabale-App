package com.example.a7_kabale.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a7_kabale.R;


public class CardViewerFragment extends Fragment {


    public CardViewerFragment() {
        // Required empty public constructor
    }

    public static CardViewerFragment newInstance(String param1, String param2) {
        CardViewerFragment fragment = new CardViewerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_viewer, container, false);
    }
}