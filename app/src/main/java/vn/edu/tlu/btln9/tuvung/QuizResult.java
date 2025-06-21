package vn.edu.tlu.btln9.tuvung;

public class QuizResult {
    private int userId;
    private String topic;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private String timestamp;
    private int durationSeconds;

    // Constructor mặc định cần thiết cho Firebase
    public QuizResult() {}

    // Getter và Setter đầy đủ

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}