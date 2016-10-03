package mkawa.okhttp;

/**
 * Created by mattkawahara on 8/24/16.
 */
public class PlayerStats {

    //grab settings


    private String name;
    private float drinks = 0;
    private float oz = 0;
    private float abv = 0;
    private float ibu = 0;
    private float level = 0;
    private float drinkPts = 0;
    private float ozPts = 0;
    private float abvPts = 0;
    private float ibuPts = 0;
    private float drinkTokens = 0;
    private float ozTokens = 0;
    private float abvTokens = 0;
    private float ibuTokens = 0;
    private float nextDrinkPt = 0;
    private float nextOzPt = 0;
    private float nextAbvPt = 0;
    private float nextIbuPt = 0;
    private int drinkRank;
    private int ozRank;
    private int abvRank;
    private int ibuRank;
    private float ozPerDrink = 0;
    private float abvPerDrink = 0;
    private float ibuPerDrink = 0;
    private float pointPerDrink = 0;
    private float avgABV = 0;



    public PlayerStats(){
    }



    //GETTERS
    public String getName(){
        return name;
    }
    public float getDrinks(){
        return drinks;
    }
    public float getOz(){
        return oz;
    }
    public float getAbv(){
        return abv;
    }
    public float getIbu(){
        return ibu;
    }

    public float getDrinkTokens(){
        return drinkTokens;
    }
    public float getOzTokens(){
        return ozTokens;
    }
    public float getAbvTokens(){
        return abvTokens;
    }
    public float getIbuTokens(){
        return ibuTokens;
    }

    public int getDrinkRank(){
        return drinkRank;
    }
    public int getOzRank(){
        return ozRank;
    }
    public int getAbvRank(){
        return abvRank;
    }
    public int getIbuRank(){
        return ibuRank;
    }

    public float getLevel(){
        return level;
    }

    public float getDrinkPts(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return Math.min(curLevel(drinks,settings.drinkLevel)+getDrinkTokens(),settings.catgPoints);
    }
    public float getOzPts(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return Math.min(curLevel(oz,settings.ozLevel)+getOzTokens(),settings.catgPoints);
    }
    public float getAbvPts(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return Math.min(curLevel(abv,settings.abvLevel)+getAbvTokens(),settings.catgPoints);
    }
    public float getIbuPts(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return Math.min(curLevel(ibu,settings.ibuLevel)+getIbuTokens(),settings.catgPoints);
    }

    public float getNextDrinkPt(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return nextLevel(getDrinks(),settings.drinkLevel);
    }
    public float getNextOzPt(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return nextLevel(getOz(),settings.ozLevel);
    }
    public float getNextAbvPt(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return nextLevel(getAbv(),settings.abvLevel);
    }
    public float getNextIbuPt(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return nextLevel(getIbu(),settings.ibuLevel);
    }

    public float getDrinkPct(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return pctComp(getDrinks(),settings.drinkLevel);
    }
    public float getOzPct(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return pctComp(getOz(),settings.ozLevel);
    }
    public float getAbvPct(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return pctComp(getAbv(),settings.abvLevel);
    }
    public float getIbuPct(){
        FetchSettings settings = FetchSettings.findById(FetchSettings.class,1);
        return pctComp(getIbu(),settings.ibuLevel);
    }

    public float getOzPerDrink(){
        return getOz()/getDrinks();
    }
    public float getAbvPerDrink(){
        return getAbv()/getDrinks();
    }
    public float getIbuPerDrink(){
        return getIbu()/getDrinks();
    }
    public float getAvgABV() {
        return 100*getAbv()/getOz();
    }
    public float getPointPerDrink(){
        return (getDrinkPts()+getOzPts()+getAbvPts()+getIbuPts())/getDrinks();
    }

    //SETTERS
    public void setName(String name){
        this.name = name;
    }
    public void setDrinks(float drinks){
        this.drinks = drinks;
    }
    public void setOz(float oz){
        this.oz = oz;
    }
    public void setAbv(float abv){
        this.abv = abv;
    }
    public void setIbu(float ibu){
        this.ibu = ibu;
    }

    public void setDrinkTokens(float drinkTokens){
        this.drinkTokens = drinkTokens;
    }
    public void setOzTokens(float ozTokens){
        this.ozTokens = ozTokens;
    }
    public void setAbvTokens(float abvTokens){
        this.abvPts = abvTokens;
    }
    public void setIbuTokens(float ibuTokens){
        this.ibuTokens = ibuTokens;
    }

    public void setDrinkRank(int drinkRank){
        this.drinkRank = drinkRank;
    }
    public void setOzRank(int ozRank){
        this.ozRank = ozRank;
    }
    public void setAbvRank(int abvRank){
        this.abvRank = abvRank;
    }
    public void setIbuRank(int ibuRank){
        this.ibuRank = ibuRank;
    }

    public void setLevel(float level){
        this.level = level;
    }

    public void setDrinkPts(float drinkPts){
        this.drinkPts = drinkPts;
    }
    public void setOzPts(float ozPts){
        this.ozPts = ozPts;
    }
    public void setAbvPts(float abvPts){
        this.abvPts = abvPts;
    }
    public void setIbuPts(float ibuPts){
        this.ibuPts = ibuPts;
    }



    //METHODS
    //method for determining current level
    public float curLevel(float current, float inc){
        float level;
        level = (current-(current % inc))/inc;
        return level;
    }

    //method for determining amount to next level
    public float nextLevel(float current, float inc){
        float level;
        level = (float)Math.round((inc-(current % inc))*10)/10;
        return level;
    }

    //method for determining percentage of current level complete
    public float pctComp(float current, float inc){
        float pct;
        pct = (inc-nextLevel(current, inc))/inc;
        return pct;
    }

}





