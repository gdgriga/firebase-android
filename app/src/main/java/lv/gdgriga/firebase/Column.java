package lv.gdgriga.firebase;

import static java.lang.Math.abs;

public enum Column {
    Backlog("Backlog"),
    Sprint("Sprint"),
    InProgress("In Progress"),
    Done("Done");

    private final String readableName;

    Column(String readableName) {
        this.readableName = readableName;
    }

    public static Column fromInt(int n) {
        if (abs(n) >= values().length) throw new IllegalArgumentException();
        return values()[n];
    }

    @Override
    public String toString() {
        return readableName;
    }
}
