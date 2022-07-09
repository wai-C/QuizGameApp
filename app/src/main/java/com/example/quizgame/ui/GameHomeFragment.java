package com.example.quizgame.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizgame.R;
import com.example.quizgame.databinding.FragmentGameHomeBinding;
import com.example.quizgame.databinding.FragmentSignUpBinding;
import com.google.firebase.auth.FirebaseAuth;


public class GameHomeFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentGameHomeBinding gameBinding;
    public GameHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gameBinding = FragmentGameHomeBinding.inflate(inflater, container, false);
        View view = gameBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.hide();

        gameBinding.btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_gameHomeFragment_to_quizFragment);
            }
        });

        gameBinding.btnGameSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Navigation.findNavController(v).navigate(R.id.action_gameHomeFragment_to_loginFragment);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gameBinding = null;
    }
}