package com.guidovezzoni.githubbrowser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Guido
 */
public class MainActivity extends ActionBarActivity implements Callback<List<GitHubAPIClient.Item>>, View.OnClickListener {
    ListView mListView = null;
    EditText mRepoOwner = null;
    EditText mRepoName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.commits_list_view);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        mRepoOwner = (EditText) findViewById(R.id.repo_owner_edit_text);
        mRepoName =  (EditText) findViewById(R.id.repo_name_edit_text);

        // pre-populate a repo
        mRepoOwner.setText("rails");
        mRepoName.setText("rails");
    }


    private void reloadCommits() {
        GitHubAPIClient.getService().commits(mRepoOwner.getText().toString(), mRepoName.getText().toString(), this);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRepoName.getWindowToken(), 0);
    }

    public void setAdapater(List<GitHubAPIClient.Item> items){
        mListView.setAdapter(new CommitAdapter(this, R.layout.list_item, items));
    }


    @Override
    public void success(final List<GitHubAPIClient.Item> items, Response response) {
        setAdapater(items);
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(getClass().getSimpleName(), "Retrofit Exception request to " + GitHubAPIClient.URL, error);
    }

    @Override
    public void onClick(View v) {
        // keyboard prevents the list to be seen
        hideSoftKeyboard();
        // this is not really needed, it's just to give the user a feedback it refreshed, as most
        // of the times the content won't change
        mListView.setAdapter(null);
        reloadCommits();
    }

    /*
    *  this adapter helps populating teach instance of list_item.xml for each GitHubAPIClient.Item
    * */
    static class CommitAdapter extends ArrayAdapter<GitHubAPIClient.Item> {
        Context mContext;
        int mResourceID;

        CommitAdapter(Context context, int resourceID, List<GitHubAPIClient.Item> items) {
            super(context, resourceID, items);

            this.mContext = context;
            this.mResourceID = resourceID;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            if (row == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                row = inflater.inflate(mResourceID, parent, false);

                holder = new ViewHolder();
                holder.imgIcon = (ImageView) row.findViewById(R.id.icon);
                holder.authorTextView = (TextView) row.findViewById(R.id.authorTextView);
                holder.commitTextView = (TextView) row.findViewById(R.id.commitTextView);
                holder.commitMsgTextView = (TextView) row.findViewById(R.id.commitMsgTextView);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.authorTextView.setText(getItem(position).commit.author.name);
            holder.commitTextView.setText(getItem(position).url);
            holder.commitMsgTextView.setText(getItem(position).commit.message);

            return row;
        }

        class ViewHolder {
            ImageView imgIcon;
            TextView authorTextView;
            TextView commitTextView;
            TextView commitMsgTextView;
        }
    }
}
