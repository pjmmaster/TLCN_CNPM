package pjm.tlcn.Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pjm.tlcn.R;

@SuppressWarnings("deprecation")
public class TabActivity_profile extends TabActivity {
    public FirebaseUser user;
    private TextView tv_UserName;
    private TabHost Tabhost_profile;
    private Button btn_edit_profile;
    private ImageButton ImgBtn_setting;
    public String UserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_profile);

        //Create Variable
        Tabhost_profile =(TabHost) findViewById(android.R.id.tabhost);
        btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile);
        ImgBtn_setting = (ImageButton) findViewById(R.id.ImgBtn_setting);
        tv_UserName = (TextView) findViewById(R.id.tv_UserName);

        //get Current User
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            tv_UserName.setText(user.getEmail().toString());
        }


        //Create Tabhost

        Tabhost_profile.getTabWidget().setDividerDrawable(null);
        TabHost.TabSpec Tabhost_post =  Tabhost_profile.newTabSpec("Tab1");
        TabHost.TabSpec Tabhost_like =  Tabhost_profile.newTabSpec("Tab2");
        TabHost.TabSpec Tabhost_following =  Tabhost_profile.newTabSpec("Tab3");
        //Create Tab1 - Post
        Tabhost_post.setIndicator("POST");
        Tabhost_post.setContent(new Intent(this, Post.class));
        //Create Tab2 - Likes
        Tabhost_like.setIndicator("LIKES");
        Tabhost_like.setContent(new Intent(this, Likes.class));
        //Create Tab3 - Following
        Tabhost_following.setIndicator("FOLLOWING");
        Tabhost_following.setContent(new Intent(this, Following.class));
        //Add Tab
        Tabhost_profile.addTab(Tabhost_post);
        Tabhost_profile.addTab(Tabhost_like);
        Tabhost_profile.addTab(Tabhost_following);
        Tabhost_profile.getTabWidget().setBackgroundResource(R.color.colorWhite);
        Tabhost_profile.getTabWidget().setStripEnabled(false);
        //End Create Tabhost

        //Start Set Onclick btn_edit_profile
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabActivity_profile.this,Edit_profile.class);
                startActivity(intent);
            }
        });
        //End Set Onclick btn_edit_profile

        //Start Set Onclick ImgBtn_setting
        ImgBtn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabActivity_profile.this,Setting.class);
                startActivity(intent);
            }
        });
        //End Set Onclick ImgBtn_setting
    }
}
