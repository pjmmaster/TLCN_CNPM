package pjm.tlcn.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pjm.tlcn.Adapter.RecyclerView_TabPost;
import pjm.tlcn.Model.Comment;
import pjm.tlcn.Model.Photo;
import pjm.tlcn.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class View_ProfilePost extends Fragment {
    private RecyclerView rv_tabpostView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView_TabPost recyclerView_tabPostProfileView;
    private ArrayList<Photo> photoArrayList = new ArrayList<Photo>();
    private DatabaseReference uDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_view__profile_post, container, false);
        rv_tabpostView = (RecyclerView) v.findViewById(R.id.rv_tabpostView);

        recyclerView_tabPostProfileView = new RecyclerView_TabPost(photoArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_tabpostView.setLayoutManager(layoutManager);
        rv_tabpostView.setAdapter(recyclerView_tabPostProfileView);

        //Swipe
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                photoArrayList.clear();
                loadData();
                recyclerView_tabPostProfileView.notifyDataSetChanged();
            }
        });
        loadData();
        return v;
    }

    public void loadData(){
        //Firebase
        //get key tu ben adapter
       final String key_user = getActivity().getIntent().getStringExtra("send");
       // Intent intent = getIntent();
       // final String key = intent.getStringExtra("send");

        uDatabase = FirebaseDatabase.getInstance().getReference().child("user_photos").child(key_user);
        uDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photoArrayList.clear();
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get("caption").toString());
                        photo.setPhoto_id(objectMap.get("photo_id").toString());
                        photo.setUser_id(objectMap.get("user_id").toString());
                        photo.setDate_created(objectMap.get("date_created").toString());
                        photo.setImage_path(objectMap.get("image_path").toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
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
                    recyclerView_tabPostProfileView.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}