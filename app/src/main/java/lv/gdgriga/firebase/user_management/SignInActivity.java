package lv.gdgriga.firebase.user_management;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.board.BoardActivity;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;
import static com.google.android.gms.auth.api.Auth.GoogleSignInApi;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;
import static lv.gdgriga.firebase.R.id.sign_in_button;
import static lv.gdgriga.firebase.R.layout.activity_signin;
import static lv.gdgriga.firebase.R.string.default_web_client_id;

public class SignInActivity extends AppCompatActivity {
    private static final int signOutCode = 0xCAFE;
    private static final int signInCode = 0xC001;
    @BindView(sign_in_button) SignInButton signInButton;
    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signin);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        googleClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this::onConnectionFailed)
                                                        .addApi(GOOGLE_SIGN_IN_API, buildSignInOptions())
                                                        .build();
        signInButton.setOnClickListener(this::onSignIn);
    }

    private GoogleSignInOptions buildSignInOptions() {
        return new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN).requestIdToken(getString(default_web_client_id))
                                                               .requestEmail()
                                                               .build();
    }

    private void onConnectionFailed(ConnectionResult connectionResult) {
        toast("Google Play Services error.");
    }

    private void onSignIn(View view) {
        Intent signIn = GoogleSignInApi.getSignInIntent(googleClient);
        startActivityForResult(signIn, signInCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == signInCode) {
            signIn(data);
        } else if (requestCode == signOutCode) {
            signOut();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signIn(Intent data) {
        GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (signInResult.isSuccess()) {
            signInTo(signInResult.getSignInAccount());
        } else {
            toast("Google Sign In failed");
        }
    }

    private void signInTo(GoogleSignInAccount account) {
        FirebaseAuth.getInstance()
                    .signInWithCredential(fromToken(account.getIdToken()))
                    .addOnCompleteListener(this, this::onSignInComplete);
    }

    private void onSignInComplete(Task<AuthResult> signinResult) {
        if (signinResult.isSuccessful()) {
            GoogleUser.saveIfNew();
            startActivityForResult(new Intent(this, BoardActivity.class), signOutCode);
        } else {
            toast("Authentication failed.");
        }
    }

    private AuthCredential fromToken(String token) {
        return GoogleAuthProvider.getCredential(token, null);
    }

    private void signOut() {
        // TODO: Implement sign out
    }

    private void toast(String message) {
        makeText(this, message, LENGTH_SHORT).show();
    }
}
