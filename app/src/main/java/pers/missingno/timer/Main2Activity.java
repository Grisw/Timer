package pers.missingno.timer;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private static Handler handler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.count_down);

        final List<TimerView.Time> list = new ArrayList<>();
        final MainActivity.RecyclerAdapter adapter=new MainActivity.RecyclerAdapter(list,true);

        recyclerView= (RecyclerView) findViewById(R.id.timer_view_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final EditText et_minutes= (EditText) findViewById(R.id.et_minutes);
        final EditText et_seconds= (EditText) findViewById(R.id.et_seconds);

        et_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_minutes.getText().toString().isEmpty()){
                    int num=Integer.parseInt(et_minutes.getText().toString());
                    if(num>99){
                        et_minutes.setText("99");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        et_seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_seconds.getText().toString().isEmpty()){
                    int num=Integer.parseInt(et_seconds.getText().toString());
                    if(num>59){
                        et_seconds.setText("59");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_minutes.getText().toString().isEmpty()){
                    et_minutes.setText("00");
                }
                if(et_seconds.getText().toString().isEmpty()){
                    et_seconds.setText("00");
                }
                if(Integer.parseInt(et_minutes.getText().toString())==0&&Integer.parseInt(et_seconds.getText().toString())==0){
                    return;
                }
                list.add(new TimerView.Time(0, Integer.parseInt(et_minutes.getText().toString()), Integer.parseInt(et_seconds.getText().toString())));
                adapter.notifyItemInserted(list.size());
                recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_count) {
            Intent intent=new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //noinspection unchecked
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,
                    new Pair<View, String>(fab, "fab"),
                    new Pair<View, String>(recyclerView, "recycler"),
                    new Pair<View, String>(toolbar, "toolbar"))
                    .toBundle());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
