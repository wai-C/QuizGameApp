package com.example.quizgame.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizgame.R;
import com.example.quizgame.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {

    private ActivityResultLauncher<Intent> resultLauncher;
    private GoogleSignInClient signInClientGoogle;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentLoginBinding loginBinding;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = loginBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //register the ActivityResultLauncher
        setResultLauncherForGoogleSignIn();

        loginBinding.signInBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBinding.progressBarLogin.show();
                loginBinding.signInBtnGoogle.setClickable(false);
                signInGoogle();
            }
        });
        loginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBinding.progressBarLogin.show();
                loginBinding.btnLogin.setClickable(false);
                String userEmail = loginBinding.textInputEditTextEmail.getText().toString();
                String userPw = loginBinding.textInputEditTextPw.getText().toString();
                signIn(userEmail, userPw);
            }
        });
        loginBinding.textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });
        loginBinding.textViewForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            }
        });
    }

    public void signIn(String email, String pw) {
        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.login_fragment_user_login_email), Snackbar.LENGTH_SHORT).show();
                                loginBinding.progressBarLogin.hide();
                            }
                        });
                    } else {
                        //go to game page
                        loginBinding.progressBarLogin.hide();
                        Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_gameHomeFragment);
                    }
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.login_fragment_user_login_error), Snackbar.LENGTH_SHORT).show();
                    loginBinding.progressBarLogin.hide();
                }
                loginBinding.btnLogin.setClickable(true);
            }
        });
    }
    private void signInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("249498617427-m6itronqd918a26a11oehj62s47p5vec.apps.googleusercontent.com")
                .requestEmail().build();
        signInClientGoogle = GoogleSignIn.getClient(getContext(), gso);
        signInToGoogle();
    }

    private void signInToGoogle() {
        Intent iToSignInWithGoogle = signInClientGoogle.getSignInIntent();
        resultLauncher.launch(iToSignInWithGoogle);
    }

    private void setResultLauncherForGoogleSignIn() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if (resultCode == RESULT_OK && data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            fbSignInWithGoogle(task);
                        }
                    }
                });
    }

    private void fbSignInWithGoogle(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_gameHomeFragment);
            fbGoogleAccount(googleSignInAccount);
        } catch (ApiException e) {
            e.printStackTrace();
            Snackbar.make(getActivity().findViewById(android.R.id.content), e.toString(), Snackbar.LENGTH_SHORT).show();
        }
    }
    private void fbGoogleAccount(GoogleSignInAccount account){
        //unique code for device so access token = null
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //get user data here
                            FirebaseUser user = auth.getCurrentUser();
                            //user.getDisplayName();
                            //Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_gameFragment);
                            Log.d("login-google:","LoginIn");
                        }
                        else{

                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_gameHomeFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginBinding = null;
    }
}