package com.example.handyproject.ui.handyman;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.UserRepository;

public class HandymanHomeViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<User> handymanProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<User> getHandymanProfile() { return handymanProfile; }
    public LiveData<String> getErrorMessage()  { return errorMessage; }

    public void loadProfile(String uid) {
        userRepository.fetchUser(uid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                handymanProfile.setValue(user);
            }

            @Override
            public void onFailure(String message) {
                errorMessage.setValue(message);
            }
        });
    }
}
