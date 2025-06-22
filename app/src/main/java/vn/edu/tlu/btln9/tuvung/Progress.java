package vn.edu.tlu.btln9.tuvung;

public class Progress {
    private int topicsLearned;
    private int quizzesCompleted;
    private double averageScore;
    private int overallProgress;

    // Constructor rỗng cần thiết cho Firebase
    public Progress() {
    }

    // Constructor đầy đủ
    public Progress(int topicsLearned, int quizzesCompleted, double averageScore, int overallProgress) {
        this.topicsLearned = topicsLearned;
        this.quizzesCompleted = quizzesCompleted;
        this.averageScore = averageScore;
        this.overallProgress = overallProgress;
    }

    // Getter và Setter
    public int getTopicsLearned() {
        return topicsLearned;
    }

    public void setTopicsLearned(int topicsLearned) {
        this.topicsLearned = topicsLearned;
    }

    public int getQuizzesCompleted() {
        return quizzesCompleted;
    }

    public void setQuizzesCompleted(int quizzesCompleted) {
        this.quizzesCompleted = quizzesCompleted;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(int overallProgress) {
        this.overallProgress = overallProgress;
    }
}
