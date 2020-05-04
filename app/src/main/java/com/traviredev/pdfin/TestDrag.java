package com.traviredev.pdfin;

import android.content.ClipData;
import android.content.Context;
//import android.support.v4.util.Pair;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

public class TestDrag extends AppCompatActivity {

    private ArrayList<Pair<Long, String>> mItemArray;
    private DragListView mDragListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testdraglayout);

        mDragListView = (DragListView) findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                Toast.makeText(mDragListView.getContext(), "Posisi Awal : " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    Toast.makeText(mDragListView.getContext(), "Posisi Akhir : " + toPosition, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mItemArray = new ArrayList<>();
        //List<ClipData.Item> listData = new TestDrag(this).getListDataMutabaahPointAll();
        for (int i = 0; i < 50; i++) {
            mItemArray.add(new Pair<>(Long.valueOf(i), "Item "+i));
        }

        setupListRecyclerView();
    }

    private void setupListRecyclerView() {
        mDragListView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapterOrder listAdapter = new ItemAdapterOrder(mItemArray, R.layout.list_row, R.id.image, false);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_row));
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            //dragView.setBackgroundColor(dragView.getResources().getColor(R.color.accent));
        }
    }
}
