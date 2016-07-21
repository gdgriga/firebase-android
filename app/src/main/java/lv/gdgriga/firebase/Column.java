package lv.gdgriga.firebase;

import static java.lang.Math.abs;

enum Column {
    ToDo("To Do"),
    InProgress("In Progress"),
    Done("Done");

    private final String readableName;

    Column(String readableName) {
        this.readableName = readableName;
    }

    static Column fromInt(int n) {
        if (abs(n) >= values().length) throw new IllegalArgumentException();
        return values()[n];
    }


    @Override
    public String toString() {
        return readableName;
    }
}
