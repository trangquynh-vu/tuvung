package vn.edu.tlu.btln9.tuvung;

public class Topic {
    private String topicId;
    private String title;
    private String description;

    // Constructor rỗng cho Firebase
    public Topic() {
    }

    // Constructor đầy đủ
    public Topic(String topicId, String title, String description) {
        this.topicId = topicId;
        this.title = title;
        this.description = description;
    }

    // ✅ Constructor chỉ có topicId và title
    public Topic(String topicId, String title) {
        this.topicId = topicId;
        this.title = title;
        this.description = "";
    }

    // Getter
    public String getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Setter
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
