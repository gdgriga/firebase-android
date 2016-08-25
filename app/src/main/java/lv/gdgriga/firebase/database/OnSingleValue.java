package lv.gdgriga.firebase.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java8.util.function.Consumer;

class OnSingleValue implements ValueEventListener {
    private final Consumer<DataSnapshot> onValue;

    OnSingleValue(Consumer<DataSnapshot> onValue) {
        this.onValue = onValue;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        onValue.accept(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("GDGFirebase", databaseError.getMessage());
    }
}
