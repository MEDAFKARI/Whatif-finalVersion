package ma.valueit.SfcBatchFtp.DAO.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ICT_ENCOURS_BRUT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "StrategyOneRequest")
public class InputEntity {

    @Column(name = "CODE_EXERCICE", length = 4)
    private String codeExercice;

    @Column(name = "CODE_PERIODE", length = 2)
    private String codePeriode;

    @Id
    @Column(name = "IE_AFFAIRE", length = 12)
    private String ieAffaire;

    @Column(name = "PERIODE")
    @Temporal(TemporalType.DATE)
    private Date periode;

    @Column(name = "TIERS_CLIENT", length = 10)
    private String tiersClient;

    @Column(name = "CODE_PRODUIT", length = 4)
    private String codeProduit;

    @Column(name = "CATEGORIE_AFFAIRE_CG", length = 17)
    private String categorieAffaireCg;

    @Column(name = "CODE_TYPE_PERSONNE", length = 4)
    private String codeTypePersonne;

    @Column(name = "CODE_PROFESSION", length = 4)
    private String codeProfession;

    @Column(name = "INDICE_ACTIVITE", length = 4)
    private String indiceActivite;

    @Column(name = "CODE_ASSIETTE_THEORIQUE", length = 12)
    private String codeAssietteTheorique;

    @Column(name = "CSP_CLIENT", length = 24)
    private String cspClient;

    @Column(name = "SECTEUR_ACTIVITE", length = 65)
    private String secteurActivite;

    @Column(name = "CODE_REGLEMENT", length = 10)
    private String codeReglement;

    @Column(name = "CODE_RESEAU", length = 4)
    private String codeReseau;

    @Column(name = "CODE_MARQUE", length = 10)
    private String codeMarque;

    @Column(name = "LIBELLE_MARQUE")
    private String libelleMarque;

    @Column(name = "DATE_DEBUT_AFFAIRE")
    @Temporal(TemporalType.DATE)
    private Date dateDebutAffaire;

    @Column(name = "DATE_FIN_AFFAIRE")
    @Temporal(TemporalType.DATE)
    private Date dateFinAffaire;

    @Column(name = "NB_IMPAYE", precision = 4)
    private BigDecimal nbImpaye;

    @Column(name = "DUREE_IMPAYE", precision = 5)
    private BigDecimal dureeImpaye;

    @Column(name = "FLAG_CTX", length = 1)
    private String flagCtx;

    @Column(name = "FLAG_DEFAUT", length = 1)
    private String flagDefaut;

    @Column(name = "FLAG_ADI", length = 1)
    private String flagAdi;

    @Column(name = "SOLD_COMPTABLE", precision = 18, scale = 3)
    private BigDecimal soldComptable;

    @Column(name = "ENCOURS", precision = 18, scale = 3)
    private BigDecimal encours;

    @Column(name = "ECHEANCE_TTC", precision = 18, scale = 3)
    private BigDecimal echeanceTtc;

    @Column(name = "DUREE_INITIAL", precision = 4, scale = 0)
    private BigDecimal dureeInitial;

    @Column(name = "TAUX_NOMINAL", precision = 11, scale = 5)
    private BigDecimal tauxNominal;

    @Column(name = "BASE_LOCATIVE", precision = 18, scale = 3)
    private BigDecimal baseLocative;

    @Column(name = "MT_BIEN_FIN_HT", precision = 18, scale = 3)
    private BigDecimal mtBienFinHt;

    @Column(name = "DEPOT_DE_GARANTIE", precision = 18, scale = 3)
    private BigDecimal depotDeGarantie;

    @Column(name = "VALEUR_RESIDUELLE", precision = 18, scale = 3)
    private BigDecimal valeurResiduelle;

    @Column(name = "PREMIER_LOYER_MAJORE", precision = 18, scale = 3)
    private BigDecimal premierLoyerMajore;

    @Column(name = "MT_APPORT", precision = 18, scale = 3)
    private BigDecimal mtApport;

    @Column(name = "PCT_APPORT", precision = 18, scale = 3)
    private BigDecimal pctApport;

    @Column(name = "PCT_VR", precision = 18, scale = 3)
    private BigDecimal pctVr;

    @Column(name = "PCT_PLM", precision = 18, scale = 3)
    private BigDecimal pctPlm;

    @Column(name = "PCT_ENDETTEMENT", precision = 18, scale = 3)
    private BigDecimal pctEndettement;

    @Column(name = "MT_ASSURANCE_DECES", precision = 18, scale = 3)
    private BigDecimal mtAssuranceDeces;

    @Column(name = "MARGE_ASSUR_VIE", precision = 18, scale = 3)
    private BigDecimal margeAssurVie;

    @Column(name = "MT_ASSU_PERT_AUTO", precision = 18, scale = 3)
    private BigDecimal mtAssuPertAuto;

    @Column(name = "MT_ASSU_PERTE", precision = 18, scale = 3)
    private BigDecimal mtAssuPerte;

    @Column(name = "FRAIS_DOSSIER", precision = 18, scale = 3)
    private BigDecimal fraisDossier;

    @Column(name = "COM_APPORT", precision = 18, scale = 3)
    private BigDecimal comApport;

    @Column(name = "FLAG_RPAT", length = 1)
    private String flagRpat;

    @Column(name = "CRD_RPAT", precision = 18, scale = 3)
    private BigDecimal crdRpat;

    @Column(name = "DUREE_RESIDUELLE", precision = 4)
    private BigDecimal dureeResiduelle;

    @Column(name = "FLAG_RESILIATION", length = 1)
    private String flagResiliation;

    @Column(name = "CRD_RESI", precision = 18, scale = 3)
    private BigDecimal crdResi;

    @Column(name = "DUREE_RESIDUELLE_1", precision = 4)
    private BigDecimal dureeResiduelle1;

    @Column(name = "FLAG_COMPENSATION", length = 1)
    private String flagCompensation;

    @Column(name = "CRD_COMP", precision = 18, scale = 3)
    private BigDecimal crdComp;

    @Column(name = "DUREE_RESIDUELLE_2", precision = 4)
    private BigDecimal dureeResiduelle2;

    @Column(name = "PHASE_DEBUT")
    private String phaseDebut;

    @Column(name = "ENCOURS_DEBUT", precision = 18, scale = 3)
    private BigDecimal encoursDebut;

    @Column(name = "IMPAYES_LOYE_DEBUT", precision = 18, scale = 3)
    private BigDecimal impayesLoyeDebut;

    @Column(name = "IMPAY_CESS_DEBUT", precision = 18, scale = 3)
    private BigDecimal impayCessDebut;

    @Column(name = "IMPAYES_FRAIS_DEBUT", precision = 18, scale = 3)
    private BigDecimal impayesFraisDebut;

    @Column(name = "EXTCPT_DEBUT", precision = 18, scale = 3)
    private BigDecimal extcptDebut;

    @Column(name = "REC_IMPAYES_LOYE", precision = 18, scale = 3)
    private BigDecimal recImpayesLoye;

    @Column(name = "REC_IMPAYES_CESS", precision = 18, scale = 3)
    private BigDecimal recImpayesCess;

    @Column(name = "REC_IMPAYES_FRAIS", precision = 18, scale = 3)
    private BigDecimal recImpayesFrais;

    @Column(name = "REC_IMPAYES_EXTCPT", precision = 18, scale = 3)
    private BigDecimal recImpayesExtcpt;

    @Column(name = "IMPAYES_BRUT_LOYE", precision = 18, scale = 3)
    private BigDecimal impayesBrutLoye;

    @Column(name = "IMPAYES_BRUT_CESS", precision = 18, scale = 3)
    private BigDecimal impayesBrutCess;

    @Column(name = "IMPAYES_BRUT_FRAIS", precision = 18, scale = 3)
    private BigDecimal impayesBrutFrais;

    @Column(name = "IMPAYES_BRUT_EXTCPT", precision = 18, scale = 3)
    private BigDecimal impayesBrutExtcpt;

    @Column(name = "PHASE_FIN", length = 25)
    private String phaseFin;

    @Column(name = "ENCOURS_FIN", precision = 18, scale = 3)
    private BigDecimal encoursFin;

    @Column(name = "IMPAYES_LOYE_FIN", precision = 18, scale = 3)
    private BigDecimal impayesLoyeFin;

    @Column(name = "IMPAY_CESS_FIN", precision = 18, scale = 3)
    private BigDecimal impayCessFin;

    @Column(name = "IMPAYES_FRAIS_FIN", precision = 18, scale = 3)
    private BigDecimal impayesFraisFin;

    @Column(name = "EXTCPT_FIN", precision = 18, scale = 3)
    private BigDecimal extcptFin;

    @Column(name = "STATUT_DEBUT", length = 10)
    private String statutDebut;

    @Column(name = "STATUT_FIN", length = 10)
    private String statutFin;

    @Column(name = "CLASSE_CONTAGION", length = 4)
    private String classeContagion;

    @Column(name = "CES_TTC_DEF", precision = 18, scale = 3)
    private BigDecimal cesTtcDef;

    @Column(name = "GARANTIE_DEF", precision = 18, scale = 3)
    private BigDecimal garantieDef;

    @Column(name = "AGIOS_RESERVES_DEF", precision = 18, scale = 3)
    private BigDecimal agiosReservesDef;

    @Column(name = "BASE_PROVISION_DEF", precision = 18, scale = 3)
    private BigDecimal baseProvisionDef;

    @Column(name = "PROVISION_DEF", precision = 18, scale = 3)
    private BigDecimal provisionDef;

    @Column(name = "DOTATION", precision = 18, scale = 3)
    private BigDecimal dotation;

    @Column(name = "RISQUE_DOTATION", precision = 18, scale = 3)
    private BigDecimal risqueDotation;

    @Column(name = "DATE_MEP")
    private Date dateMep;

    @Column(name = "VIN_IDENTIFIANT_CLIENT")
    private String vinIdentifiantClient;

    @Column(name = "VOUT_AGE_DUREE_PRET", precision = 18, scale = 3)
    private BigDecimal voutAgeDureePret;

    @Column(name = "VOUT_ANCIENNETE_ACT_PART", precision = 18, scale = 3)
    private BigDecimal voutAncienneteActPart;

    @Column(name = "VOUT_ANCI_ACTIVITE_ENTR", precision = 18, scale = 3)
    private BigDecimal voutAnciActiviteEntr;

    @Column(name = "VOUT_NBR_INCIDENTS", precision = 18, scale = 3)
    private BigDecimal voutNbrIncidents;

    @Column(name = "CATEGORIE")
    private String categorie;

    @Column(name = "CSP")
    private String csp;

    @Column(name = "TYPE_CLIENT")
    private String typeClient;

    @Column(name = "CODE_REGLEMENT_CG")
    private String codeReglementCg;

    @Column(name = "FLAG_SINISTRE")
    private String flagSinistre;

}