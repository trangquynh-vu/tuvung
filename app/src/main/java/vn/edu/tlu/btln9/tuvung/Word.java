package vn.edu.tlu.btln9.tuvung;

public class Word {
    private String Word;
    private String Pronounce;
    private String Meaning;
    private String Example;
    private String Topic; // Viết hoa đúng theo JSON

    public Word() {
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
        return Topic != null ? Topic.trim().toLowerCase() : "";
    }
}
