package com.example.handyproject.ui.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.HandymanRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final HandymanRepository handymanRepository = new HandymanRepository();

    private final MutableLiveData<List<Handyman>> handymen = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Handyman>> getHandymen()  { return handymen; }
    public LiveData<String> getErrorMessage()      { return errorMessage; }

    public void startListening() {
        handymanRepository.startListening(new HandymanRepository.HandymanListCallback() {
            @Override
            public void onUpdate(List<Handyman> result) {
                handymen.setValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void stopListening() {
        handymanRepository.stopListening();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handymanRepository.stopListening();
    }
}
