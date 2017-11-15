package com.immymemine.kevin.rxbasic3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomAdapter adapter;

    Observable<String> observable;
    List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        adapter = new CustomAdapter();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //
    }

    PublishSubject<String> publishSubject = PublishSubject.create();

    public void get(View view) {
        new Thread(
                () -> {
                    for(int i=0; i<10; i++) {
                        // publishSubject.onNext(" blah ~ " + i);

                        // behaviorSubject.onNext(" blah ~ " + i);

                        // replaySubject.onNext(" blah ~ " + i);

                        asyncSubject.onNext(" blah ~ " + i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    asyncSubject.onComplete();
                }
        ).start();
    }

    // 마지막 발행 시점[ exclude ] 이후부터 Subscribe
    public void publishSubscribe(View view) {
        publishSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                result -> {
                    data.add(result);
                    adapter.setDataAndRefresh(data);
                }
        );
    }

    // 마지막 발행 시점[ include ]부터 Subscribe
    BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

    public void behaviorSubscribe(View view) {
        behaviorSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            data.add(result);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }

    // 최초 발행 시점부터 Stack 처럼 쌓아놓고 Subscribe
    ReplaySubject<String> replaySubject = ReplaySubject.create();

    public void replaySubscribe(View view) {
        data.clear();
        replaySubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            data.add(result);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }

    // 발행 완료 시점에 Subscribe 가능 <<< onComplete() 必
    AsyncSubject<String> asyncSubject = AsyncSubject.create();

    public void asyncSubscribe(View view) {
        asyncSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            data.add(result);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder>{

    List<String> data = new ArrayList<>();

    public void setDataAndRefresh(List<String> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1,parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.text1.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView text1;

        public Holder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
