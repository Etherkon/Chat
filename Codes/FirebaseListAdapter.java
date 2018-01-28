package com.firebase.keskustelu;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firebase.client.Query;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public abstract class FirebaseListAdapter<T> extends BaseAdapter {

    private int mLayout;

    private Query mRef;
	
    private Class<T> mModelClass;
    private List<T> mModels;
    private List<String> mKeys;

	private LayoutInflater mInflater;
	private ChildEventListener mListener;

    public FirebaseListAdapter(Query mRef, Class<T> mModelClass, int mLayout, Activity activity) {
		
	    mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<T>();
        mKeys = new ArrayList<String>();
		
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
		
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
			
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                T model = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
                String key = dataSnapshot.getKey();

                if (previousChildName == null) {
                    mModels.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(model);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }
            }
			
			@Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                String key = dataSnapshot.getKey();
			    int index = mKeys.indexOf(key);
                T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
				
                mModels.remove(index);
                mKeys.remove(index);
				
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
			    int index = mKeys.indexOf(key);
                T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);;

                mModels.set(index, newModel);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mModels.remove(index);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Kuuntelu lopetettu");
            }

        });
    }

    public void cleanup() {
		
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
		
    }
	
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
		
        if (view == null) {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }

        T model = mModels.get(i);
        populateView(view, model);
        return view;
		
    }


    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    protected abstract void populateView(View v, T model);
}
