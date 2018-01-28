package com.firebase.keskustelu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.content.SharedPreferences
import android.widget.Toast;
import android.view.KeyEvent;
import android.widget.TextView;
import android.view.View;
import android.database.DataSetObserver;
import android.widget.ListView;
import android.view.inputmethod.EditorInfo;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

public class MainActivity extends ListActivity {


    private String mUsername;
	private static final String FIREBASE_URL = "https://androidbashfirebase.firebaseio.com";
	
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
	private Firebase mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// Muodostetaan yhteys
        mFirebase = new Firebase(FIREBASE_URL).child("chat");
	    setUsername();

        EditText inputMessage = (EditText) findViewById(R.id.messageInput);
        inputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public void onStart() {
		
        super.onStart();
		
        final ListView listView = getListView();
		
        mChatListAdapter = new ChatListAdapter(mFirebase.limit(40), this, R.layout.viesti, mUsername);
        listView.setAdapter(mChatLAdapter);
        mChatLAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        mConnectedListener = mFirebase.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    // Yhteys toimii
                } else {
                    // Yhteys on suljettu
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
		
    }

  
    private void setUsername() {
		
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
		
		// Luodaan randomi käyttäjä
        if (mUsername == null) {
            Random r = new Random();
            mUsername = "Käyttäjä" + r.nextInt(150000);
            prefs.edit().putString("username", mUsername).commit();
        }
		
    }

    private void sendMessage() {
		
        EditText inputMessage = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
		
        if (!input.equals("")) {
            Chat chat = new Chat(input, mUsername);
            mFirebase.push().setValue(chat);
            inputMessage.setText("");
        }
		
    }
	
	 @Override
    public void onStop() {
		
        super.onStop();
        mFirebase.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
		
    }

}
