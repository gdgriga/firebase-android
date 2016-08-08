package lv.gdgriga.firebase;

import java.util.List;

import java8.util.Optional;

import static java.util.Arrays.asList;

public final class UserContainer {
    public static final List<User> users = asList(
        new User("John Cussack", Optional.empty()),
        new User("Michael Mirsky", Optional.empty()),
        new User("Tomas Chipz", Optional.empty()),
        new User("Bake Oharry", Optional.empty()),
        new User("Lean Bean Johnson", Optional.empty())
    );
}
