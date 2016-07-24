package lv.gdgriga.firebase;

public class User {
    final String name;
    final String avatar;

    User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return name;
    }
}
