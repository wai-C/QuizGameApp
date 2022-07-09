package com.example.quizgame.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizgame.R;
import com.example.quizgame.databinding.FragmentQuizBinding;
import com.example.quizgame.model.Score;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class QuizFragment extends Fragment {

    private int correct,wrong = 0;
    private int questionCount;
    private int questionNum = 1;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference, scoreReference;


    private boolean timerContinue;
    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 15000; //15s
    private long leftTime = TOTAL_TIME;

    private String quizQuestion, quizAnsA, quizAnsB, quizAnsC, quizAnsD, quizAnsCorrect, userAns;

    private FragmentQuizBinding quizBinding;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        quizBinding = FragmentQuizBinding.inflate(inflater, container, false);
        View view = quizBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        scoreReference =  database.getReference();
        reference = database.getReference().child("Questions");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        game();
        countDownTimer.onTick(TOTAL_TIME);
        quizBinding.textViewQuizOpA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                userAns = "a";
                checkAns(quizBinding.textViewQuizOpA);
            }
        });
        quizBinding.textViewQuizOpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                userAns = "b";
                checkAns(quizBinding.textViewQuizOpB);
            }
        });
        quizBinding.textViewQuizOpC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                userAns = "c";
                checkAns(quizBinding.textViewQuizOpC);
            }
        });
        quizBinding.textViewQuizOpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                userAns = "d";
                checkAns(quizBinding.textViewQuizOpD);
            }
        });
        quizBinding.btnQuizNextQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                game();
                reset();
            }
        });

        quizBinding.btnQuizFinishG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeScore();
                Navigation.findNavController(v).navigate(R.id.action_quizFragment_to_resultFragment);

            }
        });

    }

    public void game() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                questionCount = (int) snapshot.getChildrenCount();
                quizQuestion = snapshot.child(String.valueOf(questionNum)).child("q").getValue(String.class);
                quizAnsA = snapshot.child(String.valueOf(questionNum)).child("a").getValue().toString();
                quizAnsB = snapshot.child(String.valueOf(questionNum)).child("b").getValue().toString();
                quizAnsC = snapshot.child(String.valueOf(questionNum)).child("c").getValue().toString();
                quizAnsD = snapshot.child(String.valueOf(questionNum)).child("d").getValue().toString();
                quizAnsCorrect = snapshot.child(String.valueOf(questionNum)).child("answer").getValue().toString();
                quizBinding.textViewQuizQuestion.setText(quizQuestion);
                quizBinding.textViewQuizOpA.setText(quizAnsA);
                quizBinding.textViewQuizOpB.setText(quizAnsB);
                quizBinding.textViewQuizOpC.setText(quizAnsC);
                quizBinding.textViewQuizOpD.setText(quizAnsD);

                if (questionNum < questionCount) {
                    questionNum++;
                }
                else{
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.game_fragment_ans_msg), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.game_fragment_finish_msg), Snackbar.LENGTH_SHORT).show();
            }
        });
        setCountDownTimer();
    }

    private void checkAns(TextView view) {
        if (quizAnsCorrect.equals(userAns)) {
            view.setBackgroundColor(Color.GREEN);
            correct++;
            quizBinding.textViewQuizCorrectNb.setText(""+correct);
        }else{
            view.setBackgroundColor(Color.RED);
            showCorrectAns();
            wrong++;
            quizBinding.textViewQuizWrongNb.setText(""+wrong);
        }
    }
    private void showCorrectAns(){
        switch (quizAnsCorrect){
            case "a":
                quizBinding.textViewQuizOpA.setBackgroundColor(Color.GREEN);
                break;
            case "b":
                quizBinding.textViewQuizOpB.setBackgroundColor(Color.GREEN);
                break;
            case "c":
                quizBinding.textViewQuizOpC.setBackgroundColor(Color.GREEN);
                break;
            case "d":
                quizBinding.textViewQuizOpD.setBackgroundColor(Color.GREEN);
                break;
            default: break;
        }
    }
    private void setCountDownTimer(){
        //T=15s, interval = 1s
        countDownTimer = new CountDownTimer(leftTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateCountDown();
            }

            @Override
            public void onFinish() {
                timerContinue = false;
                pauseTimer();
                quizBinding.textViewQuizQuestion.setText(getString(R.string.game_fragment_finish_game_msg));
            }
        }.start();
        timerContinue = true;
    }
    private void resetTimer(){
        leftTime = TOTAL_TIME;
        updateCountDown();
    }
    private void updateCountDown(){
        String min = String.valueOf(leftTime / 1000 / 60);
        String sec = String.valueOf(leftTime / 1000 % 60);
        quizBinding.textViewQuizTime.setText(min+":"+sec);

    }
    private void pauseTimer(){
        countDownTimer.cancel(); // stop the timer
        timerContinue = false;
    }
    @SuppressLint("ResourceAsColor")
    private void reset(){
        userAns = "";
        quizBinding.textViewQuizOpA.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
        quizBinding.textViewQuizOpB.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
        quizBinding.textViewQuizOpC.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
        quizBinding.textViewQuizOpD.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
    }
    private void storeScore(){
        String finalCorrect = quizBinding.textViewQuizCorrectNb.getText().toString();
        String finalWrong = quizBinding.textViewQuizWrongNb.getText().toString();
        if(finalWrong.isEmpty()){ finalWrong = "0";}
        Score userScore = new Score(Integer.valueOf(finalCorrect),Integer.valueOf(finalWrong));
        scoreReference.child("Scores").child(user.getUid()).setValue(userScore);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
        quizBinding = null;
    }
}