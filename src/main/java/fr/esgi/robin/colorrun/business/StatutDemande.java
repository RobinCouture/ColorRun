package fr.esgi.robin.colorrun.business;

public enum StatutDemande {
    EN_ATTENTE("en attente"),
    ACCEPTE("accepte"),
    REFUSE("refuse");

    private final String statut;

    StatutDemande(String statut) {
        this.statut = statut;
    }

    public String getStatut() {
        return statut;
    }
    
    public static StatutDemande fromStatut(String statut) {
        for (StatutDemande s : values()) {
            if (s.getStatut().equalsIgnoreCase(statut)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Statut inconnu: " + statut);
    }
}
