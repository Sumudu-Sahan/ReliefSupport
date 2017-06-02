package disaster.com.disastermanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by WAKENSYS on 5/30/2017.
 */

public class ViewHelpersCountChartActivity extends Activity{
    Context context;
    Data data = new Data();
    DBOperations dbOperations = new DBOperations();
    NetworkStatChecker n = new NetworkStatChecker();

    TextView hiddenLayout;

    ImageView backButton;

    LineChart requirementChart;
    LineData requirementData;
    LineDataSet requirementDataSet;

    ArrayList<Entry> requirementCountList = new ArrayList<>();
    ArrayList<String> methodNameList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requirement_count_chart_layout);
        context = ViewHelpersCountChartActivity.this;

        requirementChart = (LineChart) findViewById(R.id.requirementChart);
        hiddenLayout = (TextView) findViewById(R.id.hiddenLayout);

        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonOption();
            }
        });

        new GetNeedCountDetails().execute();
    }

    @Override
    public void onBackPressed() {
        backButtonOption();
    }

    public void backButtonOption(){
        Intent i = new Intent(context, MainMenuActivity.class);
        startActivity(i);
        finish();
    }

    class GetNeedCountDetails extends AsyncTask<String, Void, String[][]> {
        boolean internetAvailable = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Loading", "Please wait");
        }

        @Override
        protected String[][] doInBackground(String... urls) {
            if(n.isConnected(context)){
                this.internetAvailable = true;
                String[][] res = dbOperations.getHelpersCountDetails();
                return res;
            }
            else{
                this.internetAvailable = false;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[][] result) {
            if(result != null){
                try{
                    String[] requirementMethod = result[0];
                    String[] requirementCount = result[1];

                    requirementCountList.clear();
                    methodNameList.clear();

                    for(int k = 0; k < requirementMethod.length; k++){
                        requirementCountList.add(new Entry(Integer.parseInt(requirementCount[k]), k));
                        methodNameList.add(requirementMethod[k]);
                    }

                    requirementDataSet = new LineDataSet(requirementCountList, "");
                    requirementDataSet.setValueTextColor(Color.parseColor("#ff8000"));
                    requirementDataSet.setColor(Color.parseColor("#ff8000"));
                    requirementDataSet.setFillColor(Color.parseColor("#FF8000"));

                    requirementData = new LineData(methodNameList, requirementDataSet);
                    requirementData.setValueTextColor(Color.parseColor("#FF8000"));

                    requirementDataSet.setColors(ColorTemplate.JOYFUL_COLORS); //
                    requirementDataSet.setDrawCubic(true);
                    requirementDataSet.setDrawFilled(true);

                    requirementChart.setData(requirementData);

                    XAxis xAxis = requirementChart.getXAxis();
                    xAxis.setTextColor(Color.parseColor("#FF8000"));

                    YAxis yAxis1 = requirementChart.getAxisLeft();
                    yAxis1.setTextColor(Color.parseColor("#FF8000"));

                    YAxis yAxis2 = requirementChart.getAxisRight();
                    yAxis2.setTextColor(Color.parseColor("#FF8000"));

                    requirementChart.animateY(3000);
                    requirementChart.setDescription("Number of requirements according to the request method");
                    requirementChart.setDescriptionTextSize(12f);
                    requirementChart.setDescriptionColor(Color.parseColor("#ffffff"));
                    requirementChart.setGridBackgroundColor(Color.TRANSPARENT);

                    hiddenLayout.setVisibility(View.GONE);
                    requirementChart.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    if(this.internetAvailable){
                        hiddenLayout.setVisibility(View.VISIBLE);
                        requirementChart.setVisibility(View.GONE);
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        hiddenLayout.setVisibility(View.VISIBLE);
                        requirementChart.setVisibility(View.GONE);
                        Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            else if(this.internetAvailable){
                hiddenLayout.setVisibility(View.VISIBLE);
                requirementChart.setVisibility(View.GONE);
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
            }

            else{
                hiddenLayout.setVisibility(View.VISIBLE);
                requirementChart.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

            try{
                progressDialog.dismiss();
            }
            catch(Exception ignored){}
        }
    }
}
