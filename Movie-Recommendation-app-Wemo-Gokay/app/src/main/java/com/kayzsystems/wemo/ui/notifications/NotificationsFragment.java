package com.kayzsystems.wemo.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kayzsystems.wemo.SignInActivity;
import com.kayzsystems.wemo.databinding.FragmentNotificationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private FirebaseAuth mAuth;

    private Button logoutButton, submitButton;

    private TextInputEditText nameEdit, emailEdit, phoneEdit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        submitButton = binding.submitButton;
        logoutButton = binding.logoutButton;

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
            startActivity(signInIntent);
        }

        nameEdit = binding.fullNameProfile;
        phoneEdit = binding.phoneProfile;
        emailEdit = binding.emailProfile;

        SharedPreferences prefs = getActivity().getSharedPreferences("profile", MODE_PRIVATE);

        nameEdit.setText(prefs.getString("name", "No name defined"));
        phoneEdit.setText(prefs.getString("phone", "No phone number defined"));
        emailEdit.setText(prefs.getString("email", "No email defined"));


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                SharedPreferences.Editor prefs = getActivity().getSharedPreferences("profile", MODE_PRIVATE).edit();
                prefs.clear();
                prefs.apply();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}