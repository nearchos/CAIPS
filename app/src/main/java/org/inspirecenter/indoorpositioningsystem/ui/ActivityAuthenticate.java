package org.inspirecenter.indoorpositioningsystem.ui;

import android.Manifest;
import android.accounts.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.inspirecenter.indoorpositioningsystem.AccountsAdapter;
import org.inspirecenter.indoorpositioningsystem.R;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Nearchos Paspallis
 * Created on 19/06/2014 / 16:14.
 */
public class ActivityAuthenticate extends AppCompatActivity {

    public static final String TAG = "ips";

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        listView = (ListView) findViewById(R.id.activity_select_account_list_view);
    }

    public static final int PERMISSIONS_REQUEST_GET_ACCOUNT = 43;

    @Override protected void onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[] { Manifest.permission.GET_ACCOUNTS }, PERMISSIONS_REQUEST_GET_ACCOUNT);
        } else {
            populateAccounts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_GET_ACCOUNT) {
            populateAccounts();
        }
    }

    private void populateAccounts() {
        final AccountManager accountManager = AccountManager.get(this);
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        Log.d(TAG, "accounts: " + Arrays.toString(accounts));

        listView.setAdapter(new AccountsAdapter(this, accounts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Account account = accounts[position];
                AccountManager.get(ActivityAuthenticate.this).getAuthToken(account, "ah", new Bundle(), false, new GetAuthTokenCallback(), null);
            }
        });
    }

    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle>
    {
        public void run(AccountManagerFuture<Bundle> result)
        {
            Bundle bundle;
            try
            {
                bundle = result.getResult();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null)
                {
                    // User input required
                    startActivity(intent);
                }
                else
                {
                    final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                    final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    setAccountName(accountName);
                    setAuthToken(authToken);
                    startActivity(new Intent(ActivityAuthenticate.this, ActivityLocations.class));
                    finish();
                }
            }
            catch (OperationCanceledException | AuthenticatorException | IOException e)
            {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    // handling preferences
    public static final String KEY_ACCOUNT_NAME = "account-name";
    public static final String KEY_AUTH_TOKEN   = "auth-token";

    private boolean setAccountName(final String accountName)
    {
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(KEY_ACCOUNT_NAME, accountName)
                .commit();
    }

    private boolean setAuthToken(final String authToken)
    {
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(KEY_AUTH_TOKEN, authToken)
                .commit();
    }
}