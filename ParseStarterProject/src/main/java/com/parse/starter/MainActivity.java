package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView loginTextView;
  Button signUpButton;
  ImageView logoImageView;
  RelativeLayout backgroundLayout;
  EditText passwordEditText;
  EditText usernameEditText;

  public void showUserLess(){
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);
  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      signUpClicked(view);
    }

    return false;
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.loginTextView) {

      if (signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or, Sign Up");
      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or, Login");
      }

    }else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      assert inputMethodManager != null;
      inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
    }
  }

  public void signUpClicked(View view) {

    if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
      Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();

    } else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Toast.makeText(MainActivity.this, "Signup Successfull", Toast.LENGTH_SHORT).show();
              showUserLess();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {
        // Login
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {
              Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
              showUserLess();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Instagram");

    signUpButton = (Button) findViewById(R.id.signUpButton);
    logoImageView = (ImageView) findViewById(R.id.logoImageView);
    backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
    usernameEditText = findViewById(R.id.usernameEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    loginTextView = (TextView) findViewById(R.id.loginTextView);
    loginTextView.setOnClickListener(this);
    logoImageView.setOnClickListener(this);
    backgroundLayout.setOnClickListener(this);
    passwordEditText.setOnKeyListener(this);

    if (ParseUser.getCurrentUser() != null) {

      showUserLess();

    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}