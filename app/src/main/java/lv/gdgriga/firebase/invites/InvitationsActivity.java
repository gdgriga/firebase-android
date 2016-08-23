package lv.gdgriga.firebase.invites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class InvitationsActivity extends AppCompatActivity {
    private static final int REQUEST_INVITE = 0x1337;
    public static final String INVITATIONS = "InvitationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Build client and register connection callbacks
    }

    private void sendInvitation() {
        Intent invite = /* TODO: Build AppInviteInvitation Intent */ new Intent();
        startActivityForResult(invite, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_INVITE) return;
        String[] invitationIds = {}; // TODO: Get sent invitation count
        Log.e(INVITATIONS, "Invitations sent: " + invitationIds.length);
        finish();
    }
}
