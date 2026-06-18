package com.example.handyproject.data.repository;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Login failed."));
    }

    public void createAccount(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Registration failed."));
    }

    public void changePassword(String currentPassword, String newPassword, AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onFailure("No signed-in user.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> user.updatePassword(newPassword)
                        .addOnSuccessListener(unused2 -> callback.onSuccess(user))
                        .addOnFailureListener(e -> callback.onFailure(
                                e.getMessage() != null ? e.getMessage() : "Failed to update password.")))
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Current password is incorrect."));
    }

    public void signOut() {
        auth.signOut();
    }
}
