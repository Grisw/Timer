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
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private static final int UPDATE_NUM=0;

    private static Handler handler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==UPDATE_NUM){
                TextView view = (TextView) msg.obj;
                view.setText(""+msg.arg1);
                return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.count);

        final List<TimerView.Time> list = new ArrayList<>();
        final RecyclerAdapter adapter=new RecyclerAdapter(list,false);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        recyclerView= (RecyclerView) findViewById(R.id.timer_view_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new TimerView.Time(0, 0, 0));
                adapter.notifyItemInserted(list.size());
                recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_count_down) {
            Intent intent=new Intent(this,Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,
                    new Pair<View, String>(fab, "fab"),
                    new Pair<View, String>(recyclerView, "recycler"),
                    new Pair<View, String>(toolbar, "toolbar"))
                    .toBundle());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

        private List<TimerView.Time> list=new ArrayList<>();

        private Set<ViewHolder> holders = new HashSet<>();

        private boolean isTypeCountDown=false;

        public RecyclerAdapter(List<TimerView.Time> list,boolean isTypeCountDown){
            this.list=list;
            this.isTypeCountDown=isTypeCountDown;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_view, parent, false);
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, final int position) {
            holders.add(holder);
            holder.numberView.setText("" + (position + 1));
            holder.timerView.setTime(list.get(position));
            holder.timerView.setIsTypeCountDown(isTypeCountDown);
            if(holder.timerView.isTypeCountDown()){
                holder.progressBar.setVisibility(View.VISIBLE);
                int time=(holder.timerView.getTime().getMinutes()*60+holder.timerView.getTime().getSeconds())*10;
                holder.progressBar.setMax(time);
                holder.progressBar.setProgress(time);
            }
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.timerView.isRunning()) {
                        holder.timerView.stop();
                        holder.button.setText(R.string.continues);
                    } else {
                        if (holder.timerView.isTypeCountDown()) {
                            holder.timerView.startCountDown(holder.button,holder.progressBar);
                            holder.button.setText(R.string.stop);
                        } else {
                            holder.timerView.startCount();
                            holder.button.setText(R.string.stop);
                        }
                    }
                }
            });
            holder.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.timerView.stop();
                    list.remove(holder.getAdapterPosition());
                    holders.remove(holder);
                    notifyItemRemoved(holder.getAdapterPosition());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (ViewHolder h : holders) {
                                handler.sendMessage(handler.obtainMessage(UPDATE_NUM, h.getAdapterPosition() + 1, 0, h.numberView));
                            }
                        }
                    }).start();
                }
            });

            if(holder.timerView.isTypeCountDown()){
                holder.timerView.startCountDown(holder.button,holder.progressBar);
                holder.button.setText(R.string.stop);
            }else{
                holder.timerView.startCount();
                holder.button.setText(R.string.stop);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public TimerView timerView;
            public TextView numberView;
            public Button button;
            public ImageButton clear;
            public ProgressBar progressBar;

            public ViewHolder(View itemView) {
                super(itemView);
                timerView= (TimerView) itemView.findViewById(R.id.timer_view);
                numberView= (TextView) itemView.findViewById(R.id.timer_number);
                button= (Button) itemView.findViewById(R.id.button);
                clear= (ImageButton) itemView.findViewById(R.id.clear);
                progressBar= (ProgressBar) itemView.findViewById(R.id.progress);
            }
        }
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
