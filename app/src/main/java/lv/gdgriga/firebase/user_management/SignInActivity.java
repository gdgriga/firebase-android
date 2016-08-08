package lv.gdgriga.firebase.user_management;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static lv.gdgriga.firebase.R.layout.activity_signin;

public class SignInActivity extends AppCompatActivity {
    private static final int signOutCode = 0xCAFE;
    private static final int signInCode = 0xC001;
    // TODO: Bind sign-in button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signin);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        // TODO: Set click listener
    }

    private void onSignIn(View view) {
        // TODO: Replace with sign in Intent
        Intent signIn = new Intent();
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
        // TODO: Implement sign in
    }

    private void signOut() {
        // TODO: Implement sign out
    }

    private void toast(String message) {
        makeText(this, message, LENGTH_SHORT).show();
    }
}
