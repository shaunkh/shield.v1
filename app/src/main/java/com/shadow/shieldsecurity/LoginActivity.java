package com.shadow.shieldsecurity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipconfig.IPConfiguration;
import com.jsonparser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;
    JSONArray jsonArray;
    private static String url_signup = IPConfiguration.getIP() + "/SecurityDBConnection/LoginSV";
    private static final String TAG_LOGIN_NODE = "loginNodes";
    private static final String TAG_MESSAGE = "out_message";
    private static final String TAG_OUTCODE = "out_code";
    private static final String TAG_USERTYPE = "user_type";
    String userid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Authenticating....");


        LoadGbobalVariables();

        _emailText.setText(userid);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {

            return;
        }

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String type = "login";

        if(validate()){


            if(isOnline()==false){
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            else{
                SaveGlobalValues("userid",email);
                new DoLogin().execute(email,password);
            }
        }
        //final Background backgroundwork = new Background(this);
       // backgroundwork.execute(type, email, password);
        // TODO: Implement your own authentication logic here.

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000); */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {


                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccessAdmin() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginSuccessUser() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, UserSearch.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 36) {
            _passwordText.setError("between 3 and 36 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    private class DoLogin extends AsyncTask<String, String, String> {
        String out_message;
        String out_code;
        String usertype;
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if("0".equals(out_code) && usertype.equals("A") ){

                _emailText.setText("");
                //_passwordText.setText("");
                Toast.makeText(getApplicationContext(), out_message, Toast.LENGTH_SHORT).show();
                onLoginSuccessAdmin();


            } else if("0".equals(out_code) && usertype.equals("U")){
                onLoginSuccessUser();
            }


            else{
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... args) {

            String email = args[0];
            String pass = args[1];

            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("pass", pass));

            jsonObject = jsonParser.makeHttpRequest(url_signup, "POST", params);
            try {
                if (jsonObject != null) {
                    jsonArray = jsonObject.getJSONArray(TAG_LOGIN_NODE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        out_message = c.getString(TAG_MESSAGE);
                        out_code = c.getString(TAG_OUTCODE);
                        usertype = c.getString(TAG_USERTYPE);
                    }

                    // Log.d("error Code", errorCode);
                } else {
                    // Log.d("Server Unreachable",
                    // "Unable to Perform Your request.Server Is Unreachable! Please Try Later.");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    private void SaveGlobalValues(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    private void LoadGbobalVariables() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        userid = sp.getString("userid", "");

    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        return true;
    }*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if (getCurrentFocus() != null){
            InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
