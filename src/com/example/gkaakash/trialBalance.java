package com.example.gkaakash;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gkaakash.controller.Report;
import com.gkaakash.controller.Startup;

public class trialBalance extends Activity{
    private Report report;
    static Integer client_id;
    static Object[] trialBalanceResult;
    TableLayout trialBaltable;
    TableRow tr;
    TextView label;
    ArrayList<ArrayList<String>> trialBalGrid;
    ArrayList<String> trialBalanceResultList;
    String trialbalancetype;
    String[] ColumnNameList;
   
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_table);
        report = new Report();
	    client_id= Startup.getClient_id();
	    
	    try {
	    	/*
		      * get financial from and to date from startup page
		      */
		    String financialFromDate =Startup.getfinancialFromDate();
		    String financialToDate=Startup.getFinancialToDate();
		    String trialToDateString = reportMenu.givenToDateString;
		    trialbalancetype=reportMenu.trialbalancetype;
		    /*
		     * set financial from date and to date in textview
		     */
		    TextView tvfinancialFromDate = (TextView) findViewById( R.id.tvTfinancialFromDate );
		    TextView tvfinancialToDate = (TextView) findViewById( R.id.tvTfinancialToDate );
		      
		    tvfinancialFromDate.setText("Financial from : " +financialFromDate);
		    tvfinancialToDate.setText("Financial to : " +financialToDate);
		    /*
		     * send params to controller report.getTrialBalance to get the result
		     */
		  
		    Object[] params = new Object[]{financialFromDate,financialFromDate,trialToDateString};
		    //System.out.println("Trial Balance Type: "+trialbalancetype);
		   if("Net Trial Balance".equals(trialbalancetype)){
			   trialBalanceResult = (Object[]) report.getTrialBalance(params,client_id);
		   }else if ("Gross Trial Balance".equals(trialbalancetype)) {
			   trialBalanceResult = (Object[]) report.getGrossTrialBalance(params,client_id);
		   }else if ("Extended Trial Balance".equals(trialbalancetype)) {
			   trialBalanceResult = (Object[]) report.getExtendedTrialBalance(params,client_id);
		   }
		       
	        trialBalGrid = new ArrayList<ArrayList<String>>();
	        for(Object tb : trialBalanceResult)
	        {
	            Object[] t = (Object[]) tb;
	            trialBalanceResultList = new ArrayList<String>();
	            for(int i=0;i<t.length;i++){
	            	
	                trialBalanceResultList.add((String) t[i].toString());
	               
	            }
	            trialBalGrid.add(trialBalanceResultList);
	        }
        trialBaltable = (TableLayout)findViewById(R.id.maintable);
        addTable();
	    } catch (Exception e) {
	    	System.out.println("m in exte err"+e);
	    	AlertDialog.Builder builder = new AlertDialog.Builder(trialBalance.this);
	    	
	           builder.setMessage("Please try again")
	                   .setCancelable(false)
	                   .setPositiveButton("Ok",
	                           new DialogInterface.OnClickListener() {
	                               public void onClick(DialogInterface dialog, int id) {
	                               	
	                               }
	                           });
	                   
	           AlertDialog alert = builder.create();
	           alert.show();		}
    }
   
    private void addTable() {
        addHeader();
        /** Create a TableRow dynamically **/
        for(int i=0;i<trialBalGrid.size();i++){
            ArrayList<String> columnValue = new ArrayList<String>();
            columnValue.addAll(trialBalGrid.get(i));
            tr = new TableRow(this);
           
            for(int j=0;j<columnValue.size();j++){
                /** Creating a TextView to add to the row **/
                addRow(columnValue.get(j));   
                label.setBackgroundColor(Color.BLACK);
                /*
                 * set right aligned gravity for amount and for others set center gravity
                 */
                
                
                if(!"Extended Trial Balance".equals(trialbalancetype)){
                	if(j==3 || j==4){
                        label.setGravity(Gravity.RIGHT);
                        //For adding rupee symbol
                        if(columnValue.get(j).length() > 0){
                        
                            final SpannableString rsSymbol = new SpannableString(trialBalance.this.getText(R.string.Rs)); 
                            label.setText(rsSymbol+" "+columnValue.get(j).toString());
                        }
                    }
                    else{
                        label.setGravity(Gravity.CENTER);
                    }	
                }else {
                	if(j==3 || j==4 || j==5 || j==6 || j==7){
                        label.setGravity(Gravity.RIGHT);
                        //For adding rupee symbol
                        if(columnValue.get(j).length() > 0){
                        
                            final SpannableString rsSymbol = new SpannableString(trialBalance.this.getText(R.string.Rs)); 
                            label.setText(rsSymbol+" "+columnValue.get(j).toString());
                        }
                    }
                    else{
                        label.setGravity(Gravity.CENTER);
                    }	
				}
                
            }
            // Add the TableRow to the TableLayout
            trialBaltable.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
        
        /*
         * display the difference between total dr and total cr
         */
        TextView difference = (TextView) findViewById(R.id.tvdifference);
        final SpannableString rsSymbol = new SpannableString(trialBalance.this.getText(R.string.Rs));
        ArrayList<String> lastrow=trialBalGrid.get(trialBalGrid.size()-1);
        if(!"Extended Trial Balance".equals(trialbalancetype)){
        	Float result=Float.parseFloat(lastrow.get(4))-Float.parseFloat(lastrow.get(3));
            difference.setText("Difference : "+rsSymbol+" "+(String.format("%.2f", Math.abs(result))));
        }else {
        	Float result=Float.parseFloat(lastrow.get(7))-Float.parseFloat(lastrow.get(6));
        	difference.setText("Difference : "+rsSymbol+" "+(String.format("%.2f", Math.abs(result))));
		}
       
        
        
    }

    /*
     * add column heads to the table
     */
    void addHeader(){
        /** Create a TableRow dynamically **/
    	if ("Net Trial Balance".equals(trialbalancetype)){
    		 ColumnNameList = new String[] { "Sr. no.","Account name","Group name","Debit","Credit"};	
    	}else if ("Gross Trial Balance".equals(trialbalancetype)) {
    		ColumnNameList = new String[] { "Sr. no.","Account name","Group name","Total debit","Total credit"};	
		}else if ("Extended Trial Balance".equals(trialbalancetype)) {
			ColumnNameList = new String[] { "Sr. no.","Account name","Group name","Opening Balance","Total debit transaction","Total credit transaction","Debit balance","Credit balance"};	
		}
       
        tr = new TableRow(this);
       
        for(int k=0;k<ColumnNameList.length;k++){
            /** Creating a TextView to add to the row **/
            addRow(ColumnNameList[k]);
            label.setBackgroundColor(Color.parseColor("#348017"));
            label.setGravity(Gravity.CENTER);
        }
       
         // Add the TableRow to the TableLayout
        trialBaltable.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    }
    
    
    /*
     * this function add the value to the row
     */
    void addRow(String param){
        label = new TextView(this);
        label.setText(param);
        label.setTextColor(Color.WHITE);
        label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        label.setPadding(2, 2, 2, 2);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 1, 1, 1);
        Ll.addView(label,params);
        tr.addView((View)Ll); // Adding textView to tablerow.
        
        
       
    }

}