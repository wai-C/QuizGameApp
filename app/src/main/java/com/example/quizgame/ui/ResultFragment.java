package com.example.quizgame.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizgame.R;
import com.example.quizgame.databinding.FragmentResultBinding;
import com.example.quizgame.model.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultFragment extends Fragment {


    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FragmentResultBinding resultBinding;
    private List<Integer> scoreList = new ArrayList<>();
    private TextView textViewCorrect, textViewWrong;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        resultBinding = FragmentResultBinding.inflate(inflater, container, false);
        View view = resultBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Scores");
        getData();
        getHighestScore();
        resultBinding.btnResultQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        resultBinding.btnResultPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_resultFragment_to_quizFragment);
            }
        });
    }

    private void getData() {
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Score userScore = snapshot.getValue(Score.class);
                int correct = userScore.getCorrect();
                int wrong = userScore.getWrong();
                resultBinding.textViewResultCorrect.setText(getActivity().getString(R.string.res_fragment_correct) + "" + correct);
                resultBinding.textViewResultWrong.setText(getActivity().getString(R.string.res_fragment_wrong) + "" + wrong);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHighestScore() {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Score score = snapshot.getValue(Score.class);
                scoreList.add(score.getCorrect());
                if(scoreList.size()>0){
                    int highestNum = Collections.max(scoreList);
                    resultBinding.textViewResultHighest.setText(getActivity().getString(R.string.res_fragment_highest)+" "+highestNum);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resultBinding = null;
    }
}