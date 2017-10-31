/*
 *
 *  * Copyright © 2017 @ Pablo Grela
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package justforcommunity.radiocom.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wang.avi.AVLoadingIndicatorView;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.task.GetAccount;

import static justforcommunity.radiocom.utils.FileUtils.processBuilder;
import static justforcommunity.radiocom.utils.GlobalValues.signupURL;

public class Authenticate extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "Authenticate";
    private static final int RC_SIGN_IN = 9001;

    private Context mContext;

    private AVLoadingIndicatorView avi;
    private EditText mEmailField;
    private EditText mPasswordField;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView accountEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        mContext = this;

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        accountEmail = (TextView) findViewById(R.id.accountEmail);

        // Buttons
        findViewById(R.id.signin_email_button).setOnClickListener(this);
        findViewById(R.id.signin_google_button).setOnClickListener(this);
        setGooglePlusButtonText();
        findViewById(R.id.signout_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        findViewById(R.id.reset_password_button).setOnClickListener(this);
        findViewById(R.id.signup_button).setOnClickListener(this);

        // Initialize authenticate
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        // auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //GetAccount getAccount = new GetAccount(mContext);
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user);
                    accountEmail.setText(user.getEmail());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(Authenticate.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    @Override
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, R.string.google_services_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_google_button:
                signIn();
                break;
            case R.id.signin_email_button:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.signout_button:
                signOut();
                break;
            case R.id.verify_email_button:
                sendEmailVerification();
                break;
            case R.id.reset_password_button:
                startActivity(new Intent(this, ResetPassword.class));
                break;
            case R.id.signup_button:
                processBuilder(mContext, this, signupURL);
                break;
        }
    }

    // Put correct value in Google sign in button
    private void setGooglePlusButtonText() {
        SignInButton signInButton = ((SignInButton) findViewById(R.id.signin_google_button));
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setPadding(0, 0, 20, 0);
                tv.setText(R.string.signin);
                return;
            }
        }
    }

    // Authenticate with google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        avi.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // User is signed in, getAccount of Members
                            GetAccount getAccount = new GetAccount(mContext);
                            getAccount.execute();
                            Toast.makeText(Authenticate.this, R.string.auth_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Authenticate.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                        avi.hide();
                    }
                });
    }

    // Sign in
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Authenticate with email and password
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        avi.show();

        // sign_in_with_email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // User is signed in, getAccount of Members
                            GetAccount getAccount = new GetAccount(mContext);
                            getAccount.execute();
                            Toast.makeText(Authenticate.this, R.string.auth_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(Authenticate.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }


                        avi.hide();
                    }
                });
    }

    // sign out
    private void signOut() {
        GetAccount getAccount = new GetAccount(mContext);
        getAccount.removeAccount();
        mAuth.signOut();
        updateUI(null);
    }

    // Verified email
    private void sendEmailVerification() {

        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            findViewById(R.id.verify_email_button).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(Authenticate.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(Authenticate.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Validate form (email and password)
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getResources().getString(R.string.required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getResources().getString(R.string.required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    // Update view is user authenticated
    private void updateUI(FirebaseUser user) {
        avi.hide();

        if (user != null) {
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());

            if (user.isEmailVerified()) {
                findViewById(R.id.verify_email_button).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }
}
