package com.example.handyproject.data.repository;

import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class PortfolioRepository {

    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public void uploadPortfolioImage(String uid, byte[] imageBytes, UploadCallback callback) {
        StorageReference ref = FirebaseService.getStorage()
                .getReference()
                .child("portfolio")
                .child(uid)
                .child(UUID.randomUUID().toString() + ".jpg");

        UploadTask uploadTask = ref.putBytes(imageBytes);
        uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException() != null
                                ? task.getException() : new RuntimeException("Upload failed");
                    }
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    FirebaseService.getFirestore()
                            .collection(Constants.COLLECTION_USERS)
                            .document(uid)
                            .update(Constants.FIELD_PORTFOLIO_PHOTOS, FieldValue.arrayUnion(url))
                            .addOnSuccessListener(v -> callback.onSuccess(url))
                            .addOnFailureListener(e -> callback.onFailure(
                                    e.getMessage() != null ? e.getMessage() : "Failed to save image."));
                })
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to upload image."));
    }

    public void deletePortfolioImage(String uid, String downloadUrl, SimpleCallback callback) {
        StorageReference ref = FirebaseService.getStorage().getReferenceFromUrl(downloadUrl);

        ref.delete()
                .addOnSuccessListener(v -> FirebaseService.getFirestore()
                        .collection(Constants.COLLECTION_USERS)
                        .document(uid)
                        .update(Constants.FIELD_PORTFOLIO_PHOTOS, FieldValue.arrayRemove(downloadUrl))
                        .addOnSuccessListener(v2 -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(
                                e.getMessage() != null ? e.getMessage() : "Failed to remove image.")))
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to delete image."));
    }
}
