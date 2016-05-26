/**
 * Created by Gumokun on 25/05/16.
 */
public class ScoreIndex implements Comparable<ScoreIndex>{
    private double score;
    private int index;

    public ScoreIndex(double score, int index) {
        this.score = score;
        this.index = index;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int compareTo(ScoreIndex si){
        if(si.getScore() < getScore())
            return -1;
        else if(si.getScore() > getScore())
            return 1;
        else
            return 0;
    }
}
