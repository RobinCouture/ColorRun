package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "INSCRIPTIONS")
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSCRIPTION", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COURSE")
    private Courses idCourse;

    @Column(name = "DOSSARD", nullable = false, length = 50)
    private String dossard;

    @Column(name = "QR_CODE")
    private String qrCode;

    @Column(name = "DATE_INSCRIPTION")
    private Instant dateInscription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Courses getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Courses idCourse) {
        this.idCourse = idCourse;
    }

    public String getDossard() {
        return dossard;
    }

    public void setDossard(String dossard) {
        this.dossard = dossard;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Instant getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Instant dateInscription) {
        this.dateInscription = dateInscription;
    }

}