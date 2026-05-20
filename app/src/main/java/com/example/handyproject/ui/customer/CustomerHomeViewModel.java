package com.example.handyproject.ui.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.UserRepository;

public class CustomerHomeViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<User> customerProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<User> getCustomerProfile() { return customerProfile; }
    public LiveData<String> getErrorMessage()  { return errorMessage; }

    public void loadProfile(String uid) {
        userRepository.fetchUser(uid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                customerProfile.setValue(user);
            }

            @Override
            public void onFailure(String message) {
                errorMessage.setValue(message);
            }
        });
    }
}
