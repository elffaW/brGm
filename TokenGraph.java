package mkawa.okhttp;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattkawahara on 8/30/16.
 */
public class TokenGraph {
    private int tokenID;
    private String tokenName;

    private float curVal;
    private float val1;
    private float val2;
    private float val3;
    private float val4;
    private float val5;

    private float tokenPurseVal;
    private float baseVal;
    private Boolean haveToken;

    public TokenGraph() {
    }

    public TokenGraph(int tokenID,
                      String tokenName,
                      float curVal,
                      float val1,
                      float val2,
                      float val3,
                      float val4,
                      float val5,
                      float tokenPurseVal,
                      float baseVal,
                      boolean haveToken,
                      int tokenTitle) {
        this.tokenID = tokenID;
        this.tokenName = tokenName;
        this.curVal = curVal;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.val5 = val5;
        this.tokenPurseVal = tokenPurseVal;
        this.baseVal = baseVal;
        this.haveToken = haveToken;
    }

    // GETTERS
    public int getTokenID () {
        return tokenID;
    }

    public String getTokenName() {
        return tokenName;
    }

    public float getCurVal() {
        return curVal;
    }

    public float getVal1(){
        return val1;
    }

    public float  getVal2(){
        return val2;
    }

    public float getVal3(){
        return val3;
    }

    public float getVal4(){
        return val4;
    }

    public float getVal5(){
        return val5;
    }

    public float getTokenPurseVal(){
        return tokenPurseVal;
    }

    public float getBaseVal(){
        return baseVal;
    }

    public Boolean getHaveToken(){
        return haveToken;
    }

    //SETTERS
    public void setTokenID(int tokenID){
        this.tokenID = tokenID;
    }

    public void setTokenName(String tokeName){
        this.tokenName = tokeName;
    }

    public void setCurVal(float curVal){
        this.curVal = curVal;
    }

    public void setVal1(float val1){
        this.val1 = val1;
    }

    public void setVal2(float val2){
        this.val2 = val2;
    }

    public void setVal3(float val3){
        this.val3 = val3;
    }

    public void setVal4(float val4){
        this.val4 = val4;
    }

    public void setVal5(float val5){
        this.val5 = val5;
    }

    public void setTokenPurseVal(float tokenPurseVal){
        this.tokenPurseVal = tokenPurseVal;
    }

    public void setBaseVal(float baseVal){
        this.baseVal = baseVal;
    }

    public void setHaveToken(Boolean haveToken){
        this.haveToken = haveToken;
    }


}
