package Entity;

public enum StatoSessione {
    ASSEGNATA,
    IN_CORSO,
    COMPLETATA;

    public boolean equalsIgnoreCase(String stato) {
        // Se la stringa passata è null, restituisce false
        if (stato == null) {
            return false;
        }
        // Confronta il nome della costante enum (es. "ASSEGNATA") con la stringa ricevuta
        return this.name().equalsIgnoreCase(stato.trim());
    }
}