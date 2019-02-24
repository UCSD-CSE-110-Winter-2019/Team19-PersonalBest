package com.example.team19.personalbest;

import android.app.Activity;
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

import static com.google.android.gms.common.ConnectionResult.INTERNAL_ERROR;

public class Auth {
    private static final int RC_GET_TOKEN = 9002;
    public static final String TAG = "AUTH";
    private Activity mActivity;
    private GoogleSignInClient mGoogleSignInClient;

    public Auth(Activity activity) {
        mActivity = activity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mActivity.getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
    }

    public void signIn(Runnable runnable) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);

        if (account != null) {
            String idToken = account.getIdToken();
            Log.d(TAG, "User is already logged in. Account is not null. IdToken: " + idToken);
            if (idToken != null) {
                handleFirebaseLogIn(account, runnable);
                return;
            }
        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mActivity.startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data, Runnable runnable) {
        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task, runnable);
        }
    }

    public void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask, final Runnable runnable) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            handleFirebaseLogIn(account, runnable);
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
                    signIn(runnable);
                }
            });
        }
    }

    private void handleFirebaseLogIn(final GoogleSignInAccount account, final Runnable runnable) {
        String idToken = account.getIdToken();
        Log.d(TAG, "Email: " + account.getEmail());
        Log.d(TAG, "idToken: " + idToken);

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
                            CloudToLocalStorageMigration c2lsM = new CloudToLocalStorageMigration(mActivity);
                            c2lsM.MigrateData();

                            Cloud.set("", "Email", account.getEmail());

                            runnable.run();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}
