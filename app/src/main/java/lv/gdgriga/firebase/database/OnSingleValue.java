package lv.gdgriga.firebase.database;

import java8.util.function.Consumer;

class OnSingleValue /*TODO: Implement com.google.firebase.database.ValueEventListener*/ {
    private final Consumer<Object /*TODO: Replace with DataSnapshot*/> onValue;

    OnSingleValue(Consumer<Object /*TODO: Replace with DataSnapshot*/> onValue) {
        this.onValue = onValue;
    }

    // TODO: Override onDataChange

    // TODO: Override onCancelled
}
