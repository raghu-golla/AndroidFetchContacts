package com.raghuprojects.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyRecyclerViewAdapter;
import POJO.Contact;

public class MainActivity extends AppCompatActivity {

    private int Contact_Permission_Code = 100;
    RecyclerView recyclerView;
    public static final String TAG = "MainActivty";
    public SearchView search;
MyRecyclerViewAdapter adapter;

    private String[] mcolumprojection = new String[]{
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts._ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyler_view);
        search=(SearchView) findViewById(R.id.search);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        } else {
            getContacts();
        }
//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//              adapter.getFilter().filter(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                adapter.getFilter().filter(editable.toString());
//            }
//        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);

                return false;
            }
        });

    }



    private void getContacts() {


        List<Contact> mycontacts = new ArrayList<Contact>();
        Contact contact;
        Cursor cursor = null, pCur = null;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, mcolumprojection, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contact = new Contact();
                    String id = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if (cursor.getInt(cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        pCur = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME)) != null) {
                                contact.setName(cursor.getString(cursor.getColumnIndex(
                                        ContactsContract.Contacts.DISPLAY_NAME)));
                                contact.setMobilenumber(phoneNo);
                            }
                        }
                        pCur.close();
                    }

                    mycontacts.add(contact);
                }
            } else {
                cursor.close();
            }
            if (mycontacts.size() > 0) {
                 adapter = new MyRecyclerViewAdapter(mycontacts);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void requestPermissions() {
        Log.d(TAG, "request permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, Contact_Permission_Code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Contact_Permission_Code && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onResult");
            getContacts();
        }
    }

}