package com.parse.starter;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;

    public void getPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImages = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data!= null) {

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImages);
                Log.i("Image Selected","Good Work");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] bytesArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png",bytesArray);

                ParseObject parseObject = new ParseObject("Image");

                parseObject.put("image", file);
                parseObject.put("username",ParseUser.getCurrentUser().getUsername());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Toast.makeText(UserListActivity.this, "Image has been Shared!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserListActivity.this, "there has been an issue uploading the image :( ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                getPhoto();
            }
        }else if (item.getItemId() == R.id.logout){
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("User List");

        listView = findViewById(R.id.userListView);
        final ArrayList<String> usernames = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                intent.putExtra("username",usernames.get(i));
                startActivity(intent );
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0) {
                        for (ParseUser user:objects){
                            usernames.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}