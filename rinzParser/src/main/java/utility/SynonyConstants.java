package utility;

public enum SynonyConstants {
    CYRILLIC_TO_LATIN("Russian-Latin/BGN"),
    LATIN_TO_CYRILLIC("Latin-Russian/BGN");

    private String value;

    SynonyConstants(String extValue) {
        this.value = extValue;
    }

    public String getValue() {
        return value;
    }
}
