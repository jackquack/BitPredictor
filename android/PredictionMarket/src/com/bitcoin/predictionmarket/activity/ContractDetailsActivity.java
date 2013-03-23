package com.bitcoin.predictionmarket.activity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bitcoin.predictionmarket.R;
import com.bitcoin.predictionmarket.model.Contract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

public class ContractDetailsActivity extends SherlockFragmentActivity {
	private static final String BUNDLE_CONTRACT = "BUNDLE_CONTRACT";	
	
	private final DateFormat dateFormatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL);
	private final DecimalFormat doubleFormatter = new DecimalFormat("฿ #.##");
	private final DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
	
	public static void launch(Context context, Contract contract) {
		Intent intent = new Intent(context, ContractDetailsActivity.class);
		intent.putExtra(BUNDLE_CONTRACT, contract);	
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.contract_details);		
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		Contract contract = (Contract) getIntent().getSerializableExtra(BUNDLE_CONTRACT);		
		((TextView)findViewById(R.id.contractName)).setText(contract.name);
		((TextView)findViewById(R.id.contractExpiration)).setText(getString(R.string.expiresOn, dateFormatter.format(contract.expiration)));
		
		GraphView graphView = new LineGraphView(this, "example") {  
			   @Override  
			   protected String formatLabel(double value, boolean isValueX) {  
			      if (isValueX) {  
			         return timeFormatter.format(value);  
			      } else {
			    	 return doubleFormatter.format(value);   
			      }
			   }  
			};  
		graphView.addSeries(genRandomWalkDataForDay()); // data
		graphView.setScalable(true);
		graphView.setGraphViewStyle(new GraphViewStyle(Color.rgb(0, 0, 0), Color.rgb(0, 0, 0), Color.rgb(96, 96, 96)));	

		LinearLayout layout = (LinearLayout) findViewById(R.id.graphRoot);
		layout.addView(graphView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Apparently works around a bug that manifests itself on some Android
		// versions.
		getSherlock().dispatchInvalidateOptionsMenu();		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			MainActivity.launchFromChild(this);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}	
	
	private GraphViewSeries genRandomWalkDataForDay() {
		final Random random = new Random();
		final int size = 240;		
		final double min = 0;
		final double max = 10;
		final double range = max - min;
		final double walkPerTick = range / 1000.0;		
		double current = min + random.nextDouble() * range;
		
		final long now = System.currentTimeMillis();
		final long dayMs = 60 * 60 * 24 * 1000;
		final long interval = dayMs / size;
		
		GraphViewData[] graphViewData = new GraphViewData[size];
		
		for (int i = 0; i < size; i++) {
			graphViewData[i] = new GraphViewData(now - interval * (size - i), current);	
			double delta = (random.nextDouble() - 0.5) * walkPerTick;
			current += delta;
			current = clamp(current, min, max);
		}
		
		return new GraphViewSeries(graphViewData);
	}
	
	private double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(val, max));
	}
}