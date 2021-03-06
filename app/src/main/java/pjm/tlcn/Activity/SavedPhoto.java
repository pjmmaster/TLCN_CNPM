package pjm.tlcn.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pjm.tlcn.Adapter.PostGrid_Adapter;
import pjm.tlcn.Model.Item_GridPhoto;
import pjm.tlcn.R;

public class SavedPhoto extends AppCompatActivity {
Toolbar toolbar_saved_photo;
    PostGrid_Adapter postGrid_adapter;
    RecyclerView recyclerView;
    ArrayList<Item_GridPhoto> arrayPhoto = new ArrayList<Item_GridPhoto>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_photo);

        //Set back Toolbar
        toolbar_saved_photo = (Toolbar) findViewById(R.id.toolbar_saved_photo);
        setSupportActionBar(toolbar_saved_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar_saved_photo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set  grid view
        recyclerView = (RecyclerView) findViewById(R.id.rc_saved_photo);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3,GridLayoutManager.VERTICAL, false));
        postGrid_adapter = new PostGrid_Adapter(getApplicationContext(),arrayPhoto);
        recyclerView.setAdapter(postGrid_adapter);

        //Loadadta
        Query query = databaseRef.child("saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    arrayPhoto.clear();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference.child("photos")
                                .orderByChild("photo_id").equalTo(singleSnapshot.child("photo_id").getValue().toString());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                                    String photo_id = (objectMap.get("photo_id").toString());
                                    for(DataSnapshot dSnapshot : singleSnapshot
                                            .child("image_path").getChildren()) {
                                        Item_GridPhoto item_gridPhoto = new Item_GridPhoto();
                                        String path = dSnapshot.child("path").getValue().toString();
                                        item_gridPhoto.setPhoto_id(photo_id);
                                        item_gridPhoto.setPath(path);
                                        arrayPhoto.add(item_gridPhoto);
                                    }
                                }
                                Collections.reverse(arrayPhoto);
                                postGrid_adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Swipe
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_saved_photo);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                arrayPhoto.clear();
                LoadData();
                postGrid_adapter.notifyDataSetChanged();
            }
        });
    }


    public void LoadData(){
        //Firebase

        Query query = databaseRef.child("saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    arrayPhoto.clear();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference.child("photos")
                                .orderByChild("photo_id").equalTo(singleSnapshot.child("photo_id").getValue().toString());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    Item_GridPhoto item_gridPhoto = new Item_GridPhoto();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    item_gridPhoto.setPhoto_id(objectMap.get("photo_id").toString());
                                    for(DataSnapshot dSnapshot : singleSnapshot
                                            .child("image_path").getChildren()) {
                                        String path = dSnapshot.child("path").getValue().toString();
                                        item_gridPhoto.setPath(path);
                                        arrayPhoto.add(item_gridPhoto);
                                    }

                                }
                                Collections.reverse(arrayPhoto);
                                postGrid_adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
