package com.example.handyproject.ui.customer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.handyproject.R;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordDialog extends DialogFragment {

    private final AuthRepository authRepository = new AuthRepository();

    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private MaterialButton btnCancel, btnUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilCurrentPassword = view.findViewById(R.id.tilCurrentPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        btnCancel.setOnClickListener(v -> dismiss());
        btnUpdate.setOnClickListener(v -> attemptUpdate());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void attemptUpdate() {
        String currentPassword = ViewUtils.getTextFrom(tilCurrentPassword);
        String newPassword = ViewUtils.getTextFrom(tilNewPassword);
        String confirmPassword = ViewUtils.getTextFrom(tilConfirmPassword);

        boolean valid = true;
        valid &= ValidationUtils.validateRequired(tilCurrentPassword, currentPassword, "Current password");
        valid &= ValidationUtils.validatePassword(tilNewPassword, newPassword);
        valid &= ValidationUtils.validateConfirmPassword(tilConfirmPassword, newPassword, confirmPassword);

        if (!valid) return;

        btnUpdate.setEnabled(false);
        authRepository.changePassword(currentPassword, newPassword, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                btnUpdate.setEnabled(true);
            }
        });
    }
}
