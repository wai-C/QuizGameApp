package com.example.quizgame.ui;

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
import com.example.quizgame.databinding.FragmentForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ForgotPasswordFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentForgotPasswordBinding passwordBinding;
    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        passwordBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        View view = passwordBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));

        passwordBinding.btnForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordBinding.progressBarForgotPw.show();
                passwordBinding.btnForgotPw.setClickable(false);
                String userEmail = passwordBinding.textInputEditTextForgotPwEmail.getText().toString();
                resetPassword(userEmail);
            }
        });
    }
    private void resetPassword(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    passwordBinding.progressBarForgotPw.hide();
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.forgot_fragment_sent_msg), Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
                }else{
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.forgot_fragment_sent_msg_error), Snackbar.LENGTH_SHORT).show();
                    passwordBinding.btnForgotPw.setClickable(true);
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        passwordBinding = null;
    }
}