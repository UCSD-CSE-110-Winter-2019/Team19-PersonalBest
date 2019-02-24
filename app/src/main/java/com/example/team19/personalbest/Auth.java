package com.example.team19.personalbest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.team19.personalbest.fitness.MainScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Auth {
    private static final int RC_GET_TOKEN = 9002;
    private static final String TAG = "AUTH";
    private MainScreen mActivity;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isEmailPhase = true;

    public Auth(MainScreen activity) {
        mActivity = activity;

        mGoogleSignInClient = getTokenClient();
    }

    private GoogleSignInClient getEmailClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(mActivity, gso);
    }

    private GoogleSignInClient getTokenClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mActivity.getString(R.string.web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(mActivity, gso);
    }

    public void signIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);

        Log.d(TAG, "Account: " + account);

        if (account != null) {
            String idToken = account.getIdToken();
            Log.d(TAG, "User is already logged in. Account is not null. IdToken: " + idToken);
            if (idToken != null) {
                handleFirebaseLogIn(account);
                return;
            }
        }

        //if (isEmailPhase) {
        //            startSignIn(getEmailClient());
        //} else {
            mGoogleSignInClient.signOut().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startSignIn(getTokenClient());
                }
            });
        //}
        //isEmailPhase = !isEmailPhase;
    }

    private void startSignIn(GoogleSignInClient client) {
        mGoogleSignInClient = client;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mActivity.startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            handleFirebaseLogIn(account);
        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            Log.w(TAG, "handleSignInResult:error=" + e.getStatusCode());
            if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS) {
                Log.w(TAG, "ERROR: A sign in process is currently in progress and the current one cannot continue. e.g. the user clicks the SignInButton multiple times and more than one sign in intent was launched.");
            }
            else if (statusCode == CommonStatusCodes.INTERNAL_ERROR) {
                Log.w(TAG, "ERROR: An internal error occurred. Retrying should resolve the problem.");
            }

             mGoogleSignInClient.signOut().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Retrying sign in...");
                    signIn();
                }
            });
        }
    }

    private void handleFirebaseLogIn(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        Log.d(TAG, "Email: " + account.getEmail());
        Log.d(TAG, "idToken: " + idToken);

        if (idToken == null) {
            signIn();
            return;
        }

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Cloud.mUser = user;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}
