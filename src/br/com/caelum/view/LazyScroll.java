package br.com.caelum.view;

import java.util.ArrayList;
import java.util.List;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public class LazyScroll<T> implements OnScrollListener{
	private final List<T> list;
	private final BaseAdapter adapter;
	private final AbsListView listView;
	private final LazyScrollListener<T> listener;
	
	private final int buffer;
	private int tail;
	private int head = 0;

	public LazyScroll(AbsListView listView, BaseAdapter adapter, List<T> initial, int buffer, LazyScrollListener<T> listener) {
		this.listView = listView;
		this.adapter = adapter;
		this.list = initial;
		this.buffer = buffer;
		this.listener = listener;
		this.listView.setOnScrollListener(this);
		this.tail = list.size();
	}	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean tailAlmostReached = firstVisibleItem + visibleItemCount >= totalItemCount - buffer;
		if (tailAlmostReached) {
			List<T> elementsToAdd = listener.appendToTail(tail);
			List<T> elementsToRemove = new ArrayList<T>(list.subList(0, elementsToAdd.size()));
			
			list.addAll(elementsToAdd);
			list.removeAll(elementsToRemove);
			
			shiftPositions(elementsToAdd.size());
			adapter.notifyDataSetChanged();
		} else {
			boolean headAlmostReached = head > 1 && firstVisibleItem < buffer;
			
			if (headAlmostReached) {
				List<T> elementsToAdd = listener.appendToHead(head);
				List<T> elementsToRemove = new ArrayList<T>(list.subList(list.size()-elementsToAdd.size(), list.size()));

				list.removeAll(elementsToRemove);
				list.addAll(0, elementsToAdd);
				
				shiftPositions(-elementsToAdd.size());
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	private void shiftPositions(int amount) {
		tail += amount ;
		head += amount;
		listView.setSelection(listView.getSelectedItemPosition()-amount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	public interface LazyScrollListener<A> {
		List<A> appendToHead(int headPosition);
		List<A> appendToTail(int tailPosition);
	}
}
