package com.example.lr1_1;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogsFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ОШИБКА АВТОРИЗАЦИИ")
                .setIcon(R.drawable.error_mark)
                .setMessage("Неверный логин или пароль!")
                .setPositiveButton("OK", null);

        return builder.create();
    }
}
