package br.com.caelum.view;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public class LazyScroll<T> implements OnScrollListener{
	private final List<T> list;
	private final BaseAdapter adapter;
	private final AbsListView listView;
	private final LazyScrollListener<T> listener;
	
	private final int buffer;
	private int nextFirstResult;
	private int startOfList = 0;
	private boolean listHasBeenCroped;

	public LazyScroll(AbsListView listView, BaseAdapter adapter, List<T> initial, int buffer, LazyScrollListener<T> listener) {
		this.listView = listView;
		this.adapter = adapter;
		this.list = initial;
		this.buffer = buffer;
		this.listener = listener;
		this.listView.setOnScrollListener(this);
		this.nextFirstResult = list.size();
	}	
	
	public interface LazyScrollListener<A> {
		List<A> appendToStartOfList(int lastResult);
		List<A> appendToEndOfList(int firstResult);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (listHasBeenCroped && firstVisibleItem < buffer) {
			List<T> elementsToAdd = listener.appendToStartOfList(startOfList);
			List<T> elementsToRemove = list.subList(list.size()-elementsToAdd.size(), list.size());

			list.removeAll(new ArrayList<T>(elementsToRemove));
			list.addAll(0, elementsToAdd);
			
			changeCurrentListReference(-elementsToAdd.size());
			if (startOfList <= 1) listHasBeenCroped = false;
			adapter.notifyDataSetChanged();
		}
		
		if (firstVisibleItem + visibleItemCount >= totalItemCount - buffer) {
			List<T> elementsToAdd = listener.appendToEndOfList(nextFirstResult);
			List<T> elementsToRemove = new ArrayList<T>(list.subList(0, elementsToAdd.size()));
			
			list.addAll(elementsToAdd);
			list.removeAll(elementsToRemove);
			
			listHasBeenCroped = true;
			
			changeCurrentListReference(elementsToAdd.size());
			adapter.notifyDataSetChanged();
		}
	}
	
	private void changeCurrentListReference(int amount) {
		nextFirstResult += amount ;
		startOfList += amount;
		listView.setSelection(listView.getSelectedItemPosition()-amount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.i("Scroll state:", ""+scrollState);
	}
}
