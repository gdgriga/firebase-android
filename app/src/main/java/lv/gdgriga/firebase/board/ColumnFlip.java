package lv.gdgriga.firebase.board;

enum ColumnFlip {
    LEFT,
    RIGHT,
    NONE;

    static ColumnFlip fromRelativePosition(float dragPosition) {
        if (dragPosition < 0.1) return LEFT;
        if (dragPosition > 0.9) return RIGHT;
        return NONE;
    }
}
