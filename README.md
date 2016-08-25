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

The avatar won't appear just yet, but you can still run the app to make sure it still compiles and launches.

[1]: https://firebase.google.com/docs/android/setup
[2]: https://firebase.google.com
[3]: https://trello.com/b/fQYAslyL/template-sprint-board
[4]: https://console.firebase.google.com
[5]: https://firebase.google.com/docs/android/setup
