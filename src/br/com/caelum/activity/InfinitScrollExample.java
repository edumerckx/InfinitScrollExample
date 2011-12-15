package br.com.caelum.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.caelum.view.LazyScroll;
import br.com.caelum.view.LazyScroll.LazyScrollListener;

public class InfinitScrollExample extends Activity {
	private static final int BUFFER = 5;
	private static int PAGESIZE = 10;
	private ListView listView;
	private ArrayAdapter<Integer> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		listView = (ListView) findViewById(R.id.lista2);
		List<Integer> initial = generateList(0, 30);
		adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, initial);
		
		listView.setAdapter(adapter);
		
		new LazyScroll<Integer>(listView, adapter, initial, BUFFER, new LazyScrollListener<Integer>() {
			@Override
			public List<Integer> appendToEndOfList(int firstResult) {
				return generateList(firstResult, PAGESIZE+ firstResult);
			}

			@Override
			public List<Integer> appendToStartOfList(int lastResult) {
				int start = (lastResult - PAGESIZE > 0 ? lastResult - PAGESIZE : 0);
				return generateList(start, start + PAGESIZE);
			}
		});
	}
	
	private List<Integer> generateList(int from, int to) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = from; i < to; i++) {
			temp.add(i);
		}
		return temp;
	}
}