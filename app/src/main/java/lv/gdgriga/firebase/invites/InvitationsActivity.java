package lv.gdgriga.firebase.invites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import lv.gdgriga.firebase.util.ConnectionCallback;

import static lv.gdgriga.firebase.R.string.baby_lets_shake_hands;
import static lv.gdgriga.firebase.R.string.lets_shake_hands;
import static lv.gdgriga.firebase.R.string.lets_be_friends;

public class InvitationsActivity extends AppCompatActivity {
    private static final int REQUEST_INVITE = 0x1337;
    public static final String INVITATIONS = "InvitationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleApiClient client = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, connectionResult ->
                Log.e(INVITATIONS, "Connection failed: " + connectionResult))
            .addApi(Auth.GOOGLE_SIGN_IN_API).addApi(AppInvite.API)
            .build();
        client.registerConnectionCallbacks((ConnectionCallback) bundle -> sendInvitation());
    }

    private void sendInvitation() {
        Intent invite = new AppInviteInvitation.IntentBuilder(getString(lets_shake_hands))
            .setMessage(getString(baby_lets_shake_hands))
            .setCallToActionText(getString(lets_be_friends))
            .build();
        startActivityForResult(invite, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_INVITE) return;
        String[] invitationIds = AppInviteInvitation.getInvitationIds(resultCode, data);
        Log.e(INVITATIONS, "Invitations sent: " + invitationIds.length);
        finish();
    }
}
