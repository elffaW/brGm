package mkawa.okhttp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

/**
 * Created by mattkawahara on 8/30/16.
 */
public class TokenGraphAdapter extends RecyclerView.Adapter<TokenGraphAdapter.TGViewHolder> {

    private List<TokenGraph> tokenGraphList;
    private ClickListener clickListener;

    public class TGViewHolder extends RecyclerView.ViewHolder {
        public ImageView tokenIcon;
        public AutofitTextView tokenNameTV;
        public AutofitTextView tokenPurseValTV;
        public AutofitTextView tokenCurValTV;
        public AutofitTextView tokenBaseValTV;
        public LineChart tokenGraph;
        public LinearLayout mainLay;
        public LinearLayout tokenTitle;
        private RecyclerView recyclerView;
        private final Context context;




        public TGViewHolder(View view) {
            super(view);
            context = view.getContext();
            tokenIcon = (ImageView) view.findViewById(R.id.tokenIcon);
            tokenPurseValTV = (AutofitTextView) view.findViewById(R.id.tokenPurseCount);
            tokenCurValTV = (AutofitTextView) view.findViewById(R.id.tokenCurVal);
            tokenGraph = (LineChart) view.findViewById(R.id.tokenGraph);
            mainLay = (LinearLayout) view.findViewById(R.id.tokenLayout);
            recyclerView = (RecyclerView) view.findViewById(R.id.marketScrollWindow);
            tokenTitle = (LinearLayout) view.findViewById(R.id.tokenTitle);

            mainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){

                        clickListener.itemClicked(v, getAdapterPosition());

                    }



                }
            });


        }

    }


    public TokenGraphAdapter(List<TokenGraph> tokenGraphList) {
        this.tokenGraphList = tokenGraphList;
    }

    @Override
    public TGViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.token_market, parent, false);

        return new TGViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TGViewHolder holder, int position){
        TokenGraph tokenGraph = tokenGraphList.get(position);

        holder.tokenIcon.setImageResource(tokenGraph.getTokenID());
        holder.tokenCurValTV.setText(String.valueOf(tokenGraph.getCurVal()));
        holder.tokenPurseValTV.setTextColor(ContextCompat.getColor(holder.tokenGraph.getContext(), R.color.Gold));
        holder.tokenPurseValTV.setTypeface(EasyFonts.ostrichBlack(holder.tokenGraph.getContext()));
        holder.tokenPurseValTV.setText(String.valueOf(tokenGraph.getTokenPurseVal()));
        holder.tokenCurValTV.setTypeface(EasyFonts.ostrichRegular(holder.tokenGraph.getContext()));

        //Set Graph Formatting
        XAxis xAxis = holder.tokenGraph.getXAxis();
        xAxis.setAxisMaxValue(6.5f);
        xAxis.setAxisMinValue(0.5f);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLabels(false);
        //xAxis.addLimitLine(cVal);
        xAxis.setGridColor(ContextCompat.getColor(holder.tokenGraph.getContext(), R.color.DarkGoldenrod));

        YAxis rightAxis = holder.tokenGraph.getAxisRight();
        rightAxis.setGranularityEnabled(true);
        rightAxis.setGranularity(0.4f);
        rightAxis.setLabelCount(5, true);
        rightAxis.setAxisMinValue(tokenGraph.getBaseVal()-1f);
        rightAxis.setAxisMaxValue(tokenGraph.getBaseVal()+1f);
        rightAxis.setDrawLabels(true);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rightAxis.setTextColor(ContextCompat.getColor(holder.tokenGraph.getContext(), R.color.GhostWhite));
        rightAxis.setTypeface(EasyFonts.ostrichBlack(holder.tokenGraph.getContext()));

        holder.tokenGraph.getAxisLeft().setEnabled(false);
        holder.tokenGraph.setDescription("");
        holder.tokenGraph.setDrawBorders(true);
        holder.tokenGraph.setBorderColor(ContextCompat.getColor(holder.tokenGraph.getContext(), R.color.DarkGoldenrod));
        holder.tokenGraph.setTouchEnabled(false);
        holder.tokenGraph.getLegend().setEnabled(false);

        //Set Graph Data
        ArrayList<Entry> graphEntries = new ArrayList<>();
            graphEntries.add(new Entry(1f, tokenGraph.getVal5()));
            graphEntries.add(new Entry(2f, tokenGraph.getVal4()));
            graphEntries.add(new Entry(3f, tokenGraph.getVal3()));
            graphEntries.add(new Entry(4f, tokenGraph.getVal2()));
            graphEntries.add(new Entry(5f, tokenGraph.getVal1()));
            graphEntries.add(new Entry(6f, tokenGraph.getCurVal()));

        LineDataSet marketDataSet = new LineDataSet(graphEntries, "time");
        marketDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        marketDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        List<ILineDataSet> stockDataSets = new ArrayList<>();
        stockDataSets.add(marketDataSet);
        LineData stockData = new LineData(stockDataSets);


        if(tokenGraph.getHaveToken() == null){
            holder.mainLay.setClickable(false);
            holder.tokenIcon.setColorFilter(Color.argb(250, 0, 0, 0));
            holder.tokenGraph.setNoDataText("");

        } else if(tokenGraph.getHaveToken().equals(true)){
            holder.mainLay.setClickable(false);
            holder.tokenIcon.setColorFilter(Color.argb(0, 0, 0, 0));
            holder.tokenGraph.setData(stockData);
            holder.tokenGraph.getLineData().setDrawValues(false);
            holder.tokenGraph.setViewPortOffsets(0f,0f,0f,0f);
            holder.tokenGraph.animateY(1000);

        } else {
            holder.mainLay.setClickable(false);
            holder.tokenIcon.setColorFilter(Color.argb(250, 0, 0, 0));
            holder.tokenGraph.setNoDataText("");
        }

        if(tokenGraph.getCurVal()*tokenGraph.getTokenPurseVal() >= 1){
            holder.mainLay.setClickable(true);
            holder.tokenTitle.setBackgroundResource(R.drawable.token_purchase_title);
        }



    }

    public void setClickListener (ClickListener clickListener){
        this.clickListener = clickListener;

    }

    @Override
    public int getItemCount(){
        return tokenGraphList.size();
    }

    public interface ClickListener{
        void itemClicked(View view, int position);
    }


}


