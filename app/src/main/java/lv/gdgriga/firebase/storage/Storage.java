package lv.gdgriga.firebase.storage;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java8.util.Optional;
import java8.util.function.Consumer;

import static android.net.Uri.fromFile;

public final class Storage {
    private static final String attachments = "attachments";

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

    private static String toGsLink(String bucket, String path) {
        return "gs://" + bucket + "/" + path;
    }

    private static void error(String message) {
        Log.e(Storage.class.getName(), message);
    }

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

    private static Uri toUri(String path) {
        return fromFile(new File(path));
    }

    public static void deleteAttachment(String url) {
        storage(url).ifPresent(StorageReference::delete);
    }
}
