package vn.edu.tlu.btln9.tuvung;

public class Question {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private String topic; // Chủ đề của câu hỏi (có thể dùng để lọc quiz theo chủ đề)

    // Constructor rỗng bắt buộc cho Firebase
    public Question() {
    }

    // Constructor đầy đủ
    public Question(String question, String option1, String option2, String option3,
                    String option4, String correctAnswer, String topic) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.topic = topic;
    }

    // Getter và Setter cho tất cả các trường

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
