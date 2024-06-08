package com.example.moodswings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProflieFragment extends Fragment {

    private Button logoutButton;
    private TextView nameTextView, emailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        nameTextView = view.findViewById(R.id.tvUsername);
        emailTextView = view.findViewById(R.id.tvEmail);
        logoutButton = view.findViewById(R.id.btnLogout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), ActivityLogin.class));
                requireActivity().finish();
            }
        });

        // Fetch and display user data
        fetchAndDisplayUserData();

        return view;
    }

    private void fetchAndDisplayUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("Name").getValue(String.class);
                        String email = snapshot.child("Email").getValue(String.class);

                        if (name != null && email != null) {
                            nameTextView.setText(name);
                            emailTextView.setText(email);
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Name or Email is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to fetch user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }
}
