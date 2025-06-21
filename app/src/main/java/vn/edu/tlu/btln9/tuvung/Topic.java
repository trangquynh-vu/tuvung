package vn.edu.tlu.btln9.tuvung;

public class Topic {
    private String topicId;
    private String title;
    private String description;

    public Topic() {
        // Firebase cần constructor rỗng
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
