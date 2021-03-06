package com.project.mathsbookapp.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.project.mathsbookapp.R;
import com.project.mathsbookapp.adapter.TopicAdapter;
import com.project.mathsbookapp.helper.ClickListener;
import com.project.mathsbookapp.helper.PreferenceHelper;
import com.project.mathsbookapp.models.Favourite;
import com.project.mathsbookapp.models.Maths;
import com.project.mathsbookapp.models.TopicList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsActivity extends AppCompatActivity implements ClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    TopicAdapter adapter;
    List<Maths.Topic.Subtopic> subtopicList;
    boolean darkTheme;
    int fontSize;
    PreferenceHelper helper;
    boolean favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        helper =  new PreferenceHelper(this);
        darkTheme = helper.isDarkTheme();
        fontSize = helper.getFontSize();

        if(darkTheme){
            setTheme(R.style.AppTheme_DarkTheme);
        }
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Gson gson = new Gson();
        setupWindowAnimations();

        final String topicName = getIntent().getStringExtra("topic_selected");
        final String topicFileLocation = getIntent().getStringExtra("topic_file_name");
        final int topicColor = getIntent().getIntExtra("topic_color", 1);

        try {
            String rawData = AssetJSONFile(topicFileLocation,this);
           final Maths.Topic topic = gson.fromJson(rawData, Maths.Topic.class);
            topic.setLogo_color(topicColor);
            String favString = helper.getFavouriteList();
            Favourite fav = gson.fromJson(favString,Favourite.class);
            if(fav !=null){
                favourite = isFavourite(topic,fav.getTopicList());
            }else {
                favourite = false;
            }

            if(favourite){
                Log.i("favourite", "Favourite topic changing color");
                fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_black_24dp));
            }

            subtopicList = topic.getSubtopic();
            adapter = new TopicAdapter(subtopicList,DetailsActivity.this,this,darkTheme,fontSize);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

            final Context context = DetailsActivity.this;

            fab.setOnClickListener( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if (favourite) {
                        helper.removeTopic ( topic , topicFileLocation );
                        Snackbar.make ( view , "Removed Topic From Favourites" , Snackbar.LENGTH_LONG ).show ();
                        fab.setImageDrawable ( ContextCompat.getDrawable ( context , R.drawable.ic_favorite_border_black_24dp ) );
                        favourite = false;
                    } else {
                        helper.addTopic ( topic , topicFileLocation );
                        Snackbar.make ( view , "Added Topic To Favourites" , Snackbar.LENGTH_LONG ).show ();
                        //String favString = helper.getFavouriteList();
                        // Favourite fav = gson.fromJson(favString,Favourite.class);
                        fab.setImageDrawable ( ContextCompat.getDrawable ( context , R.drawable.ic_favorite_black_24dp ) );
                        favourite = true;
                    }
                }
            } );

        } catch (IOException e) {
            Toast.makeText(this, "Error Loading Topic ! Please Go Back", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(topicName);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    void setupWindowAnimations(){
        Fade fade = new Fade();
        fade.setDuration(500);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    boolean isFavourite(Maths.Topic topic, List<TopicList.TopicDetails> topicList){
        boolean flag = false;
        for (TopicList.TopicDetails t : topicList){
            if(topic.getTopic_name().equals(t.getTopic_name())){
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void onClicked(int position) {
    }

    @Override
    public void onLongClicked(int position) {

    }
}