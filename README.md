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
> View the list of branches for the workshop
>
    git branch -v
>
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

![screenshot_2016-08-26-00-35-51](https://cloud.githubusercontent.com/assets/5235166/18006376/e7589962-6ba7-11e6-8c90-84846c7328aa.png)

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

![screenshot_2016-08-26-00-55-11](https://cloud.githubusercontent.com/assets/5235166/18006546/aedf4440-6ba8-11e6-9cec-af518c35d41a.png)

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

![screenshot_2016-08-26-01-56-24](https://cloud.githubusercontent.com/assets/5235166/18006583/e546a80c-6ba8-11e6-8b61-0775e4eaec40.png)

We can edit tasks, but why not create? Implement *createTask* in **FirebasDb**:
```java
public static void createTask(Task task) {
    db().child(tasks).push().setValue(task);
}
```

Launch the app, touch the floating button in the bottom right corner and try creating tasks.

![screenshot_2016-08-26-16-22-31](https://cloud.githubusercontent.com/assets/5235166/18006664/4e78212a-6ba9-11e6-8735-daac18525250.png)

## Enable Remote Storage
> To skip this step, do:
>
    git checkout -f 4/storage

We can create, edit and delete tasks. But wouldn't it be cool if we could add something more to them? How about an image attachment? To do that we'll need a storage. Luckily for us, Firebase comes with one.

Start by adding the storage dependency to app/build.gradle:
```groovy
compile 'com.google.firebase:firebase-storage:9.4.0'
```
Now we're ready to upload! Implement *uploadAttachment* and *storage* methods in **Storage** class:
```java
public static void uploadAttachment(String path, Consumer<String> onSuccess) {
    Uri uri = toUri(path);
    storage().child(attachments).child(uri.getLastPathSegment()).putFile(uri)
           .addOnSuccessListener(snapshot -> {
               StorageMetadata meta = snapshot.getMetadata();
               onSuccess.accept(toGsLink(meta.getBucket(), meta.getPath()));
           })
           .addOnFailureListener(error -> error(error.getMessage()));
}

private static StorageReference storage() {
  return FirebaseStorage.getInstance().getReference();
}
```

All operations with Firebase storage are performed through an instance reference (just like with **FirebaseDatabase**). The first call to *child* selects the folder for our file (which is "attachments"), the second sets the name of the uploaded file (which we get from the URI). *toGsLink* assembles a URI in Firebase storage format (gs://<bucket>/<path>). It's shorter than a usual URL and Firebase can work with both formats. *putFile* then leaves us with an **UploadTask** that we have to register the success and failure listeners on.

Let's implement attachment downloading. Implement *getAttachmentStream* and *storage(url)* in the same **Storage** class.

```java
public static void getAttachmentStream(String url, Consumer<InputStream> onSuccess) {
    storage(url).ifPresent(ref ->
        ref.getStream((snapshot, stream) -> {
            try {
                onSuccess.accept(stream);
                stream.close();
            } catch (IOException e) {
                error(e.getMessage());
            }
        }).addOnFailureListener(error -> error(error.getMessage())));
}

private static Optional<StorageReference> storage(String url) {
    try {
        return Optional.of(FirebaseStorage.getInstance().getReferenceFromUrl(url));
    } catch (IllegalArgumentException e) {
        return Optional.empty();
    }
}
```

Getting a reference from a URL is a bit trickier, because we need to account for malformed URLs. That's why we wrap the value in an Optional. If a reference was acquired, we call *getStream* which accepts an instance of **com.google.firebase.storage.StreamDownloadTask.StreamProcessor** (we pass it as a lambda). All you need to do here is pass the stream to the *onSuccess* consumer. And then, preferably, close it.

Lastly, let's implement deletion (which is the easiest of the three). In **Storage**:
```java
public static void deleteAttachment(String url) {
    storage(url).ifPresent(StorageReference::delete);
}
```

Launch the app, and try creating a task with an attachment. Let the attachment load before hitting the apply button. If all goes well, the task will be viewed with the attachment. Edit the task to delete the attachment, it should work as well.

![screenshot_2016-08-26-23-45-20](https://cloud.githubusercontent.com/assets/5235166/18023745/85d9ee08-6c05-11e6-9673-e0af0796d662.png)

## Enable Notifications
> To skip this step, do:
>
    git checkout -f 5/notifications

Firebase Cloud Messaging (FCM) makes notifications simple. You can easily send them to your users based on their device, language, app version, etc.

To enable FCM, you first need to extend **com.google.firebase.messaging.FirebaseMessagingService**:
```java
package lv.gdgriga.firebase.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
```
In *onMessageReceived* you're getting an instance of the message for processing, we'll simply delegate the actions to the **FirebaseMessagingService**.

You can create custom message groups (Topics) and subscribe to them. To do that, extend the **com.google.firebase.iid.FirebaseInstanceIdService**:
```java
package lv.gdgriga.firebase.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String topic = "board_updates";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", token);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }
}
```
You'll also need to add the services under manifest's application tag:
```xml
<service
    android:name=".notifications.MessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>
<service
    android:name=".notifications.InstanceIdService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT"/>
    </intent-filter>
</service>
```

Launch the app and put it in background. Go to the [project's console][7] and send the notification to your device.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             

## Remote Configuration
> To skip this step, do:
>
    git checkout -f 6/remote-config

Firebase allows your app to have a remotely-stored configuration. In the Remote Config section of the [console][7] create a parameter with name *toolbar_color* and set it to whichever color you like (#ffd25a, for example).

We'll implement all the necessary logic in the **RemoteConfig** class.
```java
package lv.gdgriga.firebase.remote_config;

import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import java8.util.function.Consumer;

public final class RemoteConfig {
    private final FirebaseRemoteConfig config;

    private RemoteConfig() {
        this.config = FirebaseRemoteConfig.getInstance();
        setup();
    }

    public static void fetchConfig(Consumer<FirebaseRemoteConfig> onSuccess) {
        new RemoteConfig().fetch(onSuccess);
    }

    private void setup() {
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(true)
            .build();

        config.setConfigSettings(settings);
        config.setDefaults(new HashMap<String, Object>() {{
            put("toolbar_color", "#303F9F");
        }});
    }

    private void fetch(Consumer<FirebaseRemoteConfig> onSuccess) {
        config.fetch(cacheExpiration())
              .addOnSuccessListener(nothing -> {
                  config.activateFetched();
                  onSuccess.accept(config);
              })
              .addOnFailureListener(error -> {
                  Log.e(getClass().getName(), error.getMessage());
                  onSuccess.accept(config);
              });
    }

    private long cacheExpiration() {
        return config.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : 3600;
    }
}
```

All the operations happen through an instance of **FirebaseRemoteConfig**. First you need to set it up by providing the settings which you build with the appropriate builder. Then you need to set the defaults in case values couldn't be fetched. Finally, when actually fetching the configuration, you need to set the cache expiration time and add the listeners for success and failure cases. After the data was fetched, we have to activate it.

Let's make it possible for the user to trigger configuration fetching by clicking on the section in the popup menu.

```java
case fetch_config:
    RemoteConfig.fetchConfig(config ->
        toolbar.setBackgroundColor(parseColor(config.getString("toolbar_color"))));
    break;
```

Launch the app and choose the *Fetch Configuration* option from the menu. The color of the toolbar should change.

![screenshot_2016-08-27-01-59-02](https://cloud.githubusercontent.com/assets/5235166/18023686/e21251fc-6c04-11e6-96a4-bb473c46e821.png)

## Send Installation Invitations
> To skip this step, do:
>
    git checkout -f 7/install-invites

Firebase provides the possibility to send install invitations.

app/build.gradle:
```java
compile 'com.google.android.gms:play-services-appinvite:9.4.0'
```

Inside the *onCreate* method of the **InvitationsActivity** build the invitations API and register the connection callback on it. When the API connection happens, the *sendInvitation* method will be invoked. Inside it, build the intent to send an invitation at start an activity for result (this same activity):
```java
@Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       GoogleApiClient client = new GoogleApiClient.Builder(this)
           .enableAutoManage(this, connectionResult ->
               Log.e(INVITATIONS, "Connection failed: " + connectionResult))
           .addApi(Auth.GOOGLE_SIGN_IN_API).addApi(AppInvite.API)
           .build();
       client.registerConnectionCallbacks((ConnectionCallback) bundle -> sendInvitation());
   }

   private void sendInvitation() {
       Intent invite = new AppInviteInvitation.IntentBuilder(getString(lets_shake_hands))
           .setMessage(getString(baby_lets_shake_hands))
           .setCallToActionText(getString(lets_be_friends))
           .build();
       startActivityForResult(invite, REQUEST_INVITE);
   }
```
Inside the *onActivityResult* we'll just log the number of invitations sent. To do that, we'll retrieve invitation ids from the result data.
```java
String[] invitationIds = AppInviteInvitation.getInvitationIds(resultCode, data);
```

Launch the app and touch the popup menu's *Send Invitation* entry. Try to invite a friend to install the app (he won't be able to, because your app won't be published to Google Play).

## Enable Analytics
> To skip this step, do:
>
    git checkout -f 8/analytics

```groovy
compile 'com.google.firebase:firebase-analytics:9.4.0'
```
Analytics in Firebase work from the box. But if you want to log custom events to track user navigation through the app, for example, you can do that too.

Inside **Analytics**.*userOpenedApp*, do:
```java
FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
```
This will log an event which you should be able to see on the analytics tab in your [Firebase Console][7].

## Report Crashes
> To skip this step, do:
>
    git checkout -f 9/crashes

```groovy
compile 'com.google.firebase:firebase-crash:9.4.0'
```
This one is pretty straightforward. Firebase will log all the exceptions that happened in your app with their stacktraces.

Let's cause the app to crash by throwing an exception when the *Crash and Burn* option is selected from the popup menu.
```java
case crash_menu:
    throw new MotherKaliStartedPartyDarklyException("Just checkin'");
```
To to the [Firebase Console][7] and see that your exception was recorded.

## Ads!
> To skip this step, do:
>
    git checkout -f 10/ads

It's that time when you want to start earning money. Who doesn't love ads? Huge profits are just around the corner.

```groovy
compile 'com.google.android.gms:play-services-ads:9.4.0'
```

In activity_signin.xml, declare the ads widget:
```xml
...
<FrameLayout
    xmlns:ads="http://schemas.android.com/apk/res-auto"
    ...>

    ...

    <com.google.android.gms.ads.AdView
        android:id="@+id/ad_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        ads:adSize="BANNER"
        ads:adUnitId="@string/banner_ad_unit_id"/>
</FrameLayout>
```

Bind it as a field in the **SigninActivity**
```java
@BindView(ad_view) AdView adView;
```
Then load it inside the *onCreate*:
```java
adView.loadAd(new AdRequest.Builder().build());
```

And integrate it with the **SigninActivity**'s lifecycle:
```java
@Override
protected void onPause() {
    if (adView != null) adView.pause();
    super.onPause();
}

@Override
protected void onResume() {
    super.onResume();
    if (adView != null) adView.resume();
}

@Override
protected void onDestroy() {
    if (adView != null) adView.destroy();
    super.onDestroy();
}
```

Launch the app and behold the ads!

![screenshot_2016-08-27-03-20-22](https://cloud.githubusercontent.com/assets/5235166/18023730/5be55538-6c05-11e6-82ad-e7825c1cf741.png)

Congratulations, you're done. Hope you enjoyed the event. We'll be happy to see you at [the next one][8]!

[1]: https://firebase.google.com/docs/android/setup
[2]: https://firebase.google.com
[3]: https://trello.com/b/fQYAslyL/template-sprint-board
[4]: https://console.firebase.google.com
[5]: https://firebase.google.com/docs/android/setup
[6]: https://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
[7]: https://console.firebase.google.com
[8]: http://gdgriga.lv
