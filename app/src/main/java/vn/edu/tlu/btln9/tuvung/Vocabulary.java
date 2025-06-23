package vn.edu.tlu.btln9.tuvung;

public class Vocabulary {
    private Object vocabId; // sửa kiểu từ String thành Object
    private String Word;
    private String Pronounce;
    private String Meaning;
    private String Example;
    private String Topic;

    // Constructor rỗng cho Firebase
    public Vocabulary() {}

    // Constructor đầy đủ
    public Vocabulary(Object vocabId, String topic, String word, String pronounce, String meaning, String example) {
        this.vocabId = vocabId;
        this.Topic = topic;
        this.Word = word;
        this.Pronounce = pronounce;
        this.Meaning = meaning;
        this.Example = example;
    }

    // Getter: ép vocabId về String để dùng an toàn
    public String getVocabId() {
        return String.valueOf(vocabId);
    }

    public String getWord() { return Word; }
    public String getPronounce() { return Pronounce; }
    public String getMeaning() { return Meaning; }
    public String getExample() { return Example; }
    public String getTopic() { return Topic; }

    // Setter
    public void setVocabId(Object vocabId) { this.vocabId = vocabId; }
    public void setWord(String word) { this.Word = word; }
    public void setPronounce(String pronounce) { this.Pronounce = pronounce; }
    public void setMeaning(String meaning) { this.Meaning = meaning; }
    public void setExample(String example) { this.Example = example; }
    public void setTopic(String topic) { this.Topic = topic; }
}
