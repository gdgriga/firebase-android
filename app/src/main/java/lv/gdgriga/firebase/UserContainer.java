package lv.gdgriga.firebase;

import java.util.List;

import static java.util.Arrays.asList;

final class UserContainer {
    static final List<User> users = asList(
        new User("John Cussack", ""),
        new User("Michael Mirsky", ""),
        new User("Tomas Chipz", ""),
        new User("Bake Oharry", ""),
        new User("Lean Bean Johnson", "")
    );
}
