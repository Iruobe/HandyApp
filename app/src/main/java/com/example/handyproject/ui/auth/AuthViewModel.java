package com.example.handyproject.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<FirebaseUser> getCurrentUser() { return currentUser; }
    public LiveData<User> getUserProfile()         { return userProfile; }
    public LiveData<String> getErrorMessage()      { return errorMessage; }
    public LiveData<Boolean> getLoading()          { return loading; }

    public FirebaseUser getSignedInUser() {
        return authRepository.getCurrentUser();
    }

    public void signIn(String email, String password) {
        loading.setValue(true);
        authRepository.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                currentUser.setValue(user);
                fetchUserProfile(user.getUid());
            }

            @Override
            public void onFailure(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void fetchUserProfile(String uid) {
        userRepository.fetchUser(uid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                loading.setValue(false);
                userProfile.setValue(user);
            }

            @Override
            public void onFailure(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void signOut() {
        authRepository.signOut();
        currentUser.setValue(null);
        userProfile.setValue(null);
    }
}
