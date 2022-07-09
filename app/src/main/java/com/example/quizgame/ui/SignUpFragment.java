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
import com.example.quizgame.databinding.FragmentLoginBinding;
import com.example.quizgame.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentSignUpBinding signUpBinding;

    public SignUpFragment() {
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
        signUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = signUpBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));

        signUpBinding.btnSignUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpBinding.btnSignUpConfirm.setClickable(false);
                String userEmail = signUpBinding.textInputEditTextSignUpEmail.getText().toString();
                String userPw = signUpBinding.textInputEditTextSignUpPw.getText().toString();
                signUpBinding.progressBarSignUp.show();
                signUp(userEmail, userPw);
            }
        });
    }

    private void signUp(String email, String pw) {
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.signup_fragment_msg), Snackbar.LENGTH_SHORT).show();
                    signUpBinding.progressBarSignUp.hide();
                    Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_loginFragment);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.signup_fragment_msg_error), Snackbar.LENGTH_SHORT).show();
                    signUpBinding.btnSignUpConfirm.setClickable(true);
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signUpBinding = null;
    }
}