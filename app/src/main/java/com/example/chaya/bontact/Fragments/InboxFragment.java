package com.example.chaya.bontact.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chaya.bontact.Data.InboxAdapter;
import com.example.chaya.bontact.R;


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final int CONTACT_LOADER = 0;

    RecyclerView recyclerView;
    InboxAdapter adapter;


    public InboxFragment() {
    }


    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
              return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.inbox_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;//content provider uri
        return new CursorLoader(getContext(), CONTACT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    //don't match the parent's method
   /* @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();
        adapter = new InboxAdapter(getContext(), cursor);
        recyclerView.setAdapter(adapter);
    }*/


    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}





