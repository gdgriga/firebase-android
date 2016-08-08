package lv.gdgriga.firebase;

import java8.util.Optional;

public class User {
    private final String name;
    private final Optional<String> avatar;

    public User(String name, Optional<String> avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getAvatar() {
        return avatar;
    }
}
