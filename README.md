Android Client for GDG Riga Firebase Workshop
===========================

![firebase-app](https://cloud.githubusercontent.com/assets/5235166/17940536/44afbc4c-6a38-11e6-89d2-6a3553e71009.png)

## What's This
An Android client for Google's [Firebase][2] project. This app was built for [GDG Riga's][1] Firebase Workshop event which took place on 27th of August 2016 in Riga, Latvia. Follow this guide to get started with [Firebase][2] development on Android.

## What You'll Need
* Device with Android 2.3+ and Google Play Services 9.4.0+
* Android Studio 1.5+
* Google Play Services SDK

## What We'll Build
A sprint board (something like [Trello][3]) that utilizes most of the Firebase features.

## Getting Started
> To skip this step, do:
>
    git checkout -f 1/firebase-setup

> Note that you'll still need the google-services.json file to be placed in app folder.

First thing you'll need is a Firebase project. You can make one [here][4]. Then, follow [this guide][5] to add Firebase to the app.

## Enable Authentication
> To skip this step, do:
>
    git checkout -f 2/authentication

Add these dependencies to app/build.gradle:
```groovy
compile 'com.google.firebase:firebase-auth:9.4.0'
compile 'com.google.android.gms:play-services-auth:9.4.0'
```
Then, add the sign-in button to activity_signin.xml:
```xml
<com.google.android.gms.common.SignInButton
        android:id="@+id/sign_in_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:visibility="visible"/>
```

In **SignInActivity**, declare these two fields:
```java
@BindView(sign_in_button) SignInButton signInButton;
private GoogleApiClient googleClient;
```
In the *onCreate*, build the client and set the **OnClickListener**:
```java
googleClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this::onConnectionFailed)
                                                        .addApi(GOOGLE_SIGN_IN_API, buildSignInOptions())
                                                        .build();
signInButton.setOnClickListener(this::onSignIn);
```
Build the sign-in options:
```java
private GoogleSignInOptions buildSignInOptions() {
    return new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN).requestIdToken(getString(default_web_client_id))
                                                           .requestEmail()
                                                           .build();
}
```
Display a toast if connection fails:
```java
private void onConnectionFailed(ConnectionResult connectionResult) {
    toast("Google Play Services error.");
}
```
Implement the sign-in logic:
```java
private void signIn(Intent data) {
    GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    if (signInResult.isSuccess()) {
        signInTo(signInResult.getSignInAccount());
    } else {
        toast("Google Sign In failed");
    }
}

private void signInTo(GoogleSignInAccount account) {
    FirebaseAuth.getInstance()
                .signInWithCredential(fromToken(account.getIdToken()))
                .addOnCompleteListener(this, this::onSignInComplete);
}

private void onSignInComplete(Task<AuthResult> signinResult) {
    if (signinResult.isSuccessful()) {
        GoogleUser.saveIfNew();
        startActivityForResult(new Intent(this, BoardActivity.class), signOutCode);
    } else {
        toast("Authentication failed.");
    }
}

private AuthCredential fromToken(String token) {
    return GoogleAuthProvider.getCredential(token, null);
}
```

Run the app, you should see the following screen:

![screenshot_2016-08-25-01-08-48](https://cloud.githubusercontent.com/assets/5235166/17952236/43addf34-6a71-11e6-865c-dc587b593a19.png)

After clicking on the Sign In button, the main screen should open:

![screenshot_2016-08-25-01-09-01](https://cloud.githubusercontent.com/assets/5235166/17952268/9b4d2128-6a71-11e6-8067-57cb862daf78.png)

Now, let's implement sign out.

First, let's make an interface that extends **GoogleApiClient.ConnectionCallbacks** and implement onConnectionSuspended that will do nothing but log the error:
```java
package lv.gdgriga.firebase.util;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

public interface ConnectionCallback extends GoogleApiClient.ConnectionCallbacks {
    @Override
    default void onConnectionSuspended(int i) {
        Log.e("Google Api Connection", "suspended.");
    }
}
```
Then, let's implement *signOut* method in **SigInActivity**:
```java
private void signOut() {
    GoogleUser.signOut();
    googleClient.registerConnectionCallbacks((ConnectionCallback) bundle -> {
        Auth.GoogleSignInApi.signOut(googleClient);
        toast("Signed Out.");
    });
    googleClient.connect();
}
```
And *signOut* method in **GoogleUser**:
```java
static void signOut() {
    auth().signOut();
}

private static FirebaseAuth auth() {
    return FirebaseAuth.getInstance();
}
```
Run the app. On the main screen, press on the grey square in the top right corner and choose the Sign Out item in the popup menu. You should be brought back to the sign in screen and a toast "Signed Out." should appear.

![screenshot_2016-08-25-02-23-01](https://cloud.githubusercontent.com/assets/5235166/17952295/d2765fac-6a71-11e6-99bb-847983e3acc4.png)

Let's get the signed-in user's avatar. To do that we'll first convert the Firebase user to the domain **User**.
In **GoogleUser**:
```java
public static User getSignedIn() {
    return toDomainUser(getCurrentUser());
}

private static FirebaseUser getCurrentUser() {
    return auth().getCurrentUser();
}

private static User toDomainUser(FirebaseUser user) {
    return new User(user.getDisplayName(), user.getEmail(), getAvatarFrom(user).orElse(null));
}

private static Optional<String> getAvatarFrom(FirebaseUser user) {
    return Optional.ofNullable(user.getPhotoUrl()).map(Uri::toString);
}
```

One more method to implement is *getUserId*, we'll need it later:
```java
public static String getUserId() {
    return getCurrentUser().getUid();
}
```

The avatar won't appear just yet, but you can still run the app to make sure it still compiles and launches.

## Configure the Database
> To skip this step, do:
>
    git checkout -f 3/database

Add the following dependencies to app/build.gradle:
```groovy
compile 'com.google.firebase:firebase-database:9.4.0'
compile 'com.firebaseui:firebase-ui-database:0.4.4'
```
Let's start by implementing the **TaskViewAdapter** which will be responsible for showing our tasks on the board. Make **TaskViewAdapter** extend **FirebaseRecyclerAdapter<Task, TaskViewHolder>**:
```java
class TaskViewAdapter extends FirebaseRecyclerAdapter<Task, TaskViewHolder> {
    TaskViewAdapter(Column column) {
        super(Task.class, view_task, TaskViewHolder.class, FirebaseDb.getTasksFor(column.name()));
    }
    ...
}
```
Notice the constructor call in which we pass the entity class of our domain object (**Task**), the layout id for the representation (*view_task*), the class of the ViewHolder we'll use (find more about view holders [here][6]), and and an instance of the query we'll use to retrieve the entities (we'll implement it in a second).

**FirebaseRecyclerAdapter** defines the *populateViewHolder* method in which all the magic happens. The only thing you need to do there is to get the entity's key:
```java
String taskKey = getRef(position >= getItemCount() ? getItemCount() - 1 : position).getKey();
```
*getRef* expects an int, which is the position of the element in the list. The selection of the previous to last element is a hack to prevent the ArrayIndexOutOfBounds exception from happening in some cases of fast user actions.

Now, let's implement a query that'll get us all the tasks for the given collection (*Backlog*, *Sprint*, *In Progress*, *Done*). In FirebasDb:
```java
public static Query getTasksFor(String collectionName) {
     return db().child(tasks)
                .orderByChild(collection).equalTo(collectionName);
}

private static DatabaseReference db() {
    return FirebaseDatabase.getInstance().getReference();
}
```
All the connection logic is handled by Firebase itself, all you need is to get the instance reference and start querying.

Next query to implement is getting the task's user by the user's key. Value queries in Firebase are asynchronous, they expect an instance of **com.google.firebase.database.ValueEventListener**. The **ValueEventListener** has two methods: *onDataChange* which is called in case of successful query, and *onCancelled* which is called if something goes wrong. Let's make our life easier by deriving our own implementation of the **ValueEventListener** that will allow us to write concise lambda callbacks to use in queries. Change **OnSingleValue** to look like below:
```java
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
```

Now we can write a query that'll get a user by key. In FirebaseDb:
```java
public static void getUserByKey(String key, Consumer<DataSnapshot> onValue) {
     db().child(users).child(key).addListenerForSingleValueEvent(new OnSingleValue(onValue));
}
```

Let's return to **TaskViewAdapter** to handle the snapshot when it's retrieved from the database. Snapshot are raw data representations returned by Firebase queries. You have to convert them to domain entities before they can be used. You can convert a snapshot to an entity by calling the *getValue* method and passing in the right class.
```java
private Consumer<String> setAssigneeAvatar(TaskViewHolder viewHolder) {
     return key -> FirebaseDb.getUserByKey(key, snapshot -> {
         if (!snapshot.exists()) return;
         User user = snapshot.getValue(User.class);
         ...
     });
}
```
You can check whether the record you searched for exists by calling the *exists* method on the snapshot.

And inside *updateAvatar* method in **BoardActivity**, convert the snapshot to a **User**:
```java
public void updateAvatar() {
    FirebaseDb.getUserByKey(GoogleUser.getUserId(), snapshot -> {
        User user = snapshot.getValue(User.class);
        ...
    });
}
```

Now we can implement *saveIfNew* method in **GoogleUser**. Inside it we'll check whether the user with given UID exists in the database already and if not, we'll create a new record:
```java
static void saveIfNew() {
    FirebaseUser currentUser = getCurrentUser();
    FirebaseDb.getUserByKey(currentUser.getUid(), snapshot -> {
        if (snapshot.exists()) return;
        FirebaseDb.createUser(currentUser.getUid(), toDomainUser(currentUser));
    });
}
```
The *createUser* in *FirebaseDb* will look like this:
```java
public static void createUser(String uid, User user) {
    db().child(users).child(uid).setValue(user);
}
```

Launch the app, if all goes well, you should see the user's avatar in the top right corner.

[>>>>>]Screen with user's avatar[<<<<<]

Now let's display the tasks.

In **ColumnFragment**'s *onViewCreated*, register the **RecyclerView.AdapterDataObserver** in the **TaskViewAdapter**. This will make sure that every time a new task is inserted, the application will scroll to it. Then, set this adapter as an adapter for the task list, this will trigger the task population.
```java
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
    ...
    adapter.registerAdapterDataObserver(observer);
    taskList.setAdapter(adapter);
    ...
}
```
Launch the app, the tasks should show up.

[>>>>>>>>>>]Board with tasks[<<<<<<<<<<<<<]

If you try moving the tasks around, the app will crash. Let's fix that by updating the **KarmaManager**'s *updateUserKarma* method by converting the data snapshot to the **User** entity.
```java
static void updateUserKarma(String userKey, int diff) {
    FirebaseDb.getUserByKey(userKey, snapshot -> {
        User user = snapshot.getValue(User.class);
        ...
    });
}
```
Also, implement *updateUser* in **FirebaseDb**:
```java
public static void updateUser(String userKey, User user) {
    db().child(users).child(userKey).setValue(user);
}
```

Every time a user moves a task, his karma value is updated. If the task is moved to the right (i.e. being completed), user's karma increases and vice-versa. We'll use this logic later to punish/praise the user for his actions.

Now the tasks can be dragged without a crash, but once the task is released, it appears back where it was. Let's fix that. In **FirebaseDb**, implement the *changeTaskColumn* method:
```java
public static void changeTaskColumn(String draggedTaskKey, String newColumn) {
    db().child(tasks).child(draggedTaskKey).child(collection).setValue(newColumn);
}
```

Launch the app and try dragging tasks around. Now they should stick.

Moving tasks around is fun, but let's make it more fun by making editing tasks possible. In **FirebaseDb**, implement *getTaskByKey*.
```java
public static void getTaskByKey(String taskKey, Consumer<DataSnapshot> onValue) {
     db().child(tasks).child(taskKey).addListenerForSingleValueEvent(new OnSingleValue(onValue));
}
```

 In **EditTaskActivity**, convert data snapshot to **Task** bean.
```java
task = snapshot.getValue(Task.class);
```

We'll also need a list of all users so we can reassign the task. Let's implement *getAllUsers* in **FirebaseDb**:
```java
public static void getAllUsers(Consumer<DataSnapshot> onValue) {
    db().child(users).orderByValue().addListenerForSingleValueEvent(new OnSingleValue(onValue));
}
```
To make conversion of many users simpler, let's make an utility method. In **Snapshot**, implement the *toUsers* method:
```java
public static List<User> toUsers(DataSnapshot dataSnapshot) {
   ArrayList<User> users = new ArrayList<>();
   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
       User user = snapshot.getValue(User.class);
       user.setKey(snapshot.getKey());
       users.add(user);
   }
   return users;
}
```

Launch the app and touch any task, a dialog will open in which you can edit the task's attributes. You can play around, but when you click apply, the changes won't be persisted.

To persist the changes, implement *updateTask* in **FirebasDb**:
```java
public static void updateTask(String taskKey, Task task) {
    db().child(tasks).child(taskKey).setValue(task);
}
```
Let's also implement *deleteTask* while we're there.
```java
public static void deleteTask(String taskKey) {
    db().child(tasks).child(taskKey).removeValue();
}
```

Launch the app and try editing/deleting tasks.

[>>>>>>>>]Edit dialog[<<<<<<<<<<<<<<<<<<]

We can edit tasks, but why not create? Implement *createTask* in **FirebasDb**:
```java
public static void createTask(Task task) {
    db().child(tasks).push().setValue(task);
}
```

Launch the app, touch the floating button in the bottom right corner and try creating tasks.

[>>>>>>>>>>>]Create task dialog[<<<<<<<<<<<]

[1]: https://firebase.google.com/docs/android/setup
[2]: https://firebase.google.com
[3]: https://trello.com/b/fQYAslyL/template-sprint-board
[4]: https://console.firebase.google.com
[5]: https://firebase.google.com/docs/android/setup
[6]: https://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
