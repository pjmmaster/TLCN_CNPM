package pjm.tlcn.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import pjm.tlcn.Adapter.RecyclerView_TabPost;
import pjm.tlcn.Model.Comment;
import pjm.tlcn.Model.Photo;
import pjm.tlcn.R;

public class Saved extends Fragment {
    private RecyclerView rv_tabsaved;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView_TabPost recyclerView_tabSaved;
    private ArrayList<Photo> photoArrayList = new ArrayList<Photo>();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_saved, container, false);

        rv_tabsaved = (RecyclerView) v.findViewById(R.id.rv_tabsaved);
        loadData();
        recyclerView_tabSaved = new RecyclerView_TabPost(photoArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_tabsaved.setLayoutManager(layoutManager);
        rv_tabsaved.setAdapter(recyclerView_tabSaved);

        //Swipe
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.saved_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                photoArrayList.clear();
                loadData();
                recyclerView_tabSaved.notifyDataSetChanged();
            }
        });
        return v;
    }

    public void loadData(){
        //Firebase

        Query query = databaseRef.child("saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    photoArrayList.clear();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference.child("photos")
                                .orderByChild("photo_id").equalTo(singleSnapshot.child("photo_id").getValue().toString());
                        Log.d("key saved",singleSnapshot.child("photo_id").getValue().toString());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    Photo photo = new Photo();
                                    Log.d("key photo", singleSnapshot.child("photo_id").getValue().toString());
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get("caption").toString());
                                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                                    photo.setUser_id(objectMap.get("user_id").toString());
                                    photo.setDate_created(objectMap.get("date_created").toString());
                                    photo.setImage_path(objectMap.get("image_path").toString());

                                    ArrayList<Comment> comments = new ArrayList<Comment>();
                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child("comments").getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        comments.add(comment);
                                    }

                                    photo.setComments(comments);
                                    photoArrayList.add(photo);

                                }
                                Collections.reverse(photoArrayList);
                                recyclerView_tabSaved.notifyDataSetChanged();
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