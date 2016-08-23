package lv.gdgriga.firebase.storage;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import java8.util.Optional;
import java8.util.function.Consumer;

import static android.net.Uri.fromFile;

public final class Storage {
    private static final String attachments = "attachments";

    public static void uploadAttachment(String path, Consumer<String> onSuccess) {
        Uri uri = toUri(path);
        // TODO: Upload attachment
    }

    private static String toGsLink(String bucket, String path) {
        return "gs://" + bucket + "/" + path;
    }

    public static void getAttachmentStream(String url, Consumer<InputStream> onSuccess) {
        // TODO: Get attachment stream
    }

    private static int error(String message) {
        return Log.e(Storage.class.getName(), message);
    }

    private static /* TODO Replace with StorageReference */ Object storage() {
        return /*Return FirebaseStorage reference*/ null;
    }

    private static Optional</*TODO: Replace with StorageReference */Object> storage(String url) {
        try {
            return Optional.of(/*TODO: Get reference from URL*/null);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Uri toUri(String path) {
        return fromFile(new File(path));
    }

    public static void deleteAttachment(String url) {
        // TODO: Delete the attachemnt file
    }
}
