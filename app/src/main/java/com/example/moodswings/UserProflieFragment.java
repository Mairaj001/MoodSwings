package com.example.moodswings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.zip.Inflater;

public class UserProflieFragment extends Fragment {

    Button logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view= inflater.inflate(R.layout.user_profile_fragment,container,false);

      this.logout=view.findViewById(R.id.btnLogout);

      this.logout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              FirebaseAuth.getInstance().signOut();
              Toast.makeText(getContext(),"Logout",Toast.LENGTH_SHORT).show();
              startActivity( new Intent(getContext(), ActivityLogin.class));
              requireActivity().finish();
          }
      });


      return view;
    }
}
