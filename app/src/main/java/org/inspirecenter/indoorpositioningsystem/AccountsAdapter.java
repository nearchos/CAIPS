package org.inspirecenter.indoorpositioningsystem;

import android.accounts.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Nearchos Paspallis
 * Created on 24/12/13.
 */
public class AccountsAdapter extends ArrayAdapter<Account>
{
    public static final String TAG = "ips";

    private final LayoutInflater layoutInflater;

    public AccountsAdapter(final Context context, final Account [] accounts)
    {
        super(context, R.layout.list_item_account, accounts);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_account, null);

        final Account account = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView accountName = (TextView) view.findViewById(R.id.list_item_account_name);
        final TextView accountType = (TextView) view.findViewById(R.id.list_item_account_type);

        // Bind the data efficiently with the holder.
        accountName.setText(account.name);
        accountType.setText(account.type);

        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}