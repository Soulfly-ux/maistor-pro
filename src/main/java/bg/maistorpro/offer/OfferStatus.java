package bg.maistorpro.offer;

public enum OfferStatus {
    DRAFT("Чернова"),
    SENT("Изпратена"),
    ACCEPTED("Приета"),
    REJECTED("Отказана");

    private final String label;

    OfferStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
