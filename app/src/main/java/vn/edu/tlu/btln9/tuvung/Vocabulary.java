package vn.edu.tlu.btln9.tuvung;

public class Vocabulary {
    private String Word;
    private String Pronounce;
    private String Meaning;
    private String Example;
    private String Topic;
    private int vocabId;

    public Vocabulary() {
        // Firebase cần constructor rỗng
    }

    public String getWord() {
        return Word;
    }

    public String getPronounce() {
        return Pronounce;
    }

    public String getMeaning() {
        return Meaning;
    }

    public String getExample() {
        return Example;
    }

    public String getTopic() {
        return Topic;
    }

    public int getVocabId() {
        return vocabId;
    }
}
