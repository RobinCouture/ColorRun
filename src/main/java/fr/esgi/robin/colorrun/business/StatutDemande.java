package fr.esgi.robin.colorrun.business;

public enum StatutDemande {
    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé");

    private final String statut;

    StatutDemande(String statut) {
        this.statut = statut;
    }

    public String getStatut() {
        return statut;
    }
}
