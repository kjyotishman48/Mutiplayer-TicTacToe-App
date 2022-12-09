package androidsamples.java.tictactoe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText mEmail;
    private EditText mPassword;
    private ProgressDialog progressDialog;
    //Create object of DatabaseReference class to access Firebase's RealTime Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("users");

    private static final String TAG = "LogInFragment";

    FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO if a user is logged in, go to Dashboard
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null) {
            sendUserToDashBoard();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mEmail = view.findViewById(R.id.edit_email);
        mPassword = view.findViewById(R.id.edit_password);
        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Please wait while authentication...");
        progressDialog.setTitle("Authentication");
        view.findViewById(R.id.btn_log_in)
                .setOnClickListener(v -> {
                    // TODO implement sign in logic
                    progressDialog.show();
                    PerformAuth();
                    //progressDialog.dismiss();
                    NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                    Navigation.findNavController(v).navigate(action);
                });
        return view;
    }
    // No options menu in login fragment.

    private void PerformAuth() {
        String emailTxt = mEmail.getText().toString().trim();
        String passwordTxt = mPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(emailTxt.isEmpty()) {
            Toast.makeText(getContext(), "Please enter non empty email",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else if(passwordTxt.isEmpty()) {
            Toast.makeText(getContext(),"Plaease enter non empty password",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"REG_COMPLETE");
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration Successful",Toast.LENGTH_SHORT);
                            sendUserToDashBoard();
                            databaseReference.child(task.getResult().getUser().getUid()).child("won").setValue(0);
                            databaseReference.child(task.getResult().getUser().getUid()).child("lost").setValue(0);
                            databaseReference.child(task.getResult().getUser().getUid()).child("draw").setValue(0);
                            databaseReference.child(task.getResult().getUser().getUid()).child("email").setValue(emailTxt);
                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                login(emailTxt, passwordTxt);
                            }
                            else {
                                Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void login(String emailTxt, String passwordTxt) {
        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        sendUserToDashBoard();
                    } else {
                        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }

    private void sendUserToDashBoard() {
        //Then navigate
        NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
    }
}