package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Constantes.ConstantesBloc;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nicky
 * modifier par @roneh pour la gestion des ouvertures et la fusion des zone
 */
public class ArmatureBlocsService {

    private static final double EPSILON = 0.0001;
    private final ConstructeurRangeeService constructeur = new ConstructeurRangeeService();
    private final OuvertureService ouvertureService = new OuvertureService();

    /**
     * Surcharge de compatibilité avec les appels existants.
     */
    public List<Bloc> simuler(
            ArmatureBlocs armature,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc
    ) {
        return simuler(armature, List.of(), hauteurBloc, largeurBloc, prixBloc);
    }

    /**
     * Simulation tenant compte des ouvertures et des formes non rectangulaires.
     */
    public List<Bloc> simuler(
            ArmatureBlocs armature,
            List<Ouverture> ouvertures,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc
    ) {
        if (armature == null) {
            return List.of();
        }

        return simulerGroupe(
                List.of(armature),
                ouvertures,
                hauteurBloc,
                largeurBloc,
                prixBloc
        );
    }

    /**
     * Simulation d'un groupe temporaire de zones contiguës.
     * Les zones restent distinctes dans le modèle, mais partagent
     * la continuité des rangées pendant la simulation.
     */
    public List<Bloc> simulerGroupe(
            List<ArmatureBlocs> groupe,
            List<Ouverture> ouvertures,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc
    ) {
        if (groupe == null || groupe.isEmpty()
                || hauteurBloc <= 0 || largeurBloc <= 0 || prixBloc < 0) {
            return List.of();
        }

        List<ArmatureBlocs> groupeSecurise = groupe.stream()
                .filter(z -> z != null)
                .toList();

        if (groupeSecurise.isEmpty()) {
            return List.of();
        }

        List<Ouverture> ouverturesSecurisees = (ouvertures == null) ? List.of() : ouvertures;

        List<OuvertureService.OuvertureInfo> infosOuvertures = new ArrayList<>();
        for (Ouverture ouverture : ouverturesSecurisees) {
            OuvertureService.OuvertureInfo info =
                    ouvertureService.analyserOuverture(ouverture, hauteurBloc, largeurBloc);
            if (info != null) {
                infosOuvertures.add(info);
            }
        }

        for (ArmatureBlocs armature : groupeSecurise) {
            armature.viderRangees();
        }

        List<Bloc> blocsUtilises = new ArrayList<>();

        double minY = groupeSecurise.stream()
                .flatMap(z -> z.getPoints().stream())
                .mapToDouble(p -> p.y)
                .min()
                .orElse(0.0);

        double maxY = groupeSecurise.stream()
                .flatMap(z -> z.getPoints().stream())
                .mapToDouble(p -> p.y)
                .max()
                .orElse(0.0);

        double y = minY;
        double resteBloc = 0.0;

        while (y + hauteurBloc <= maxY + EPSILON) {
            double yEchantillon = y + (hauteurBloc / 2.0);

            List<GeometryService.SegmentHorizontal> segmentsBase =
                    segmentsFusionnesDuGroupe(groupeSecurise, yEchantillon);

            if (segmentsBase.isEmpty()) {
                y += hauteurBloc;
                resteBloc = 0.0;
                continue;
            }

            List<GeometryService.SegmentHorizontal> retraits = new ArrayList<>();
            List<Bloc> linteaux = new ArrayList<>();
            boolean rangeeSpeciale = false;

            for (OuvertureService.OuvertureInfo info : infosOuvertures) {
                if (info.chevaucheCorps(y, hauteurBloc)) {
                    rangeeSpeciale = true;

                    double marge = info.necessiteArmatureTraditionnelle()
                            ? ouvertureService.getMargeArmatureTraditionnelle()
                            : 0.0;

                    retraits.add(new GeometryService.SegmentHorizontal(
                            info.xMin() - marge,
                            info.xMax() + marge
                    ));
                } else if (info.estRangeeVideAuDessus(y, hauteurBloc)) {
                    rangeeSpeciale = true;

                    retraits.add(new GeometryService.SegmentHorizontal(
                            info.xMin() - ouvertureService.getMargeArmatureTraditionnelle(),
                            info.xMax() + ouvertureService.getMargeArmatureTraditionnelle()
                    ));
                } else if (info.estRangeeLinteau(y, hauteurBloc)) {
                    rangeeSpeciale = true;

                    Bloc linteau = ouvertureService.creerBlocLinteau(
                            info,
                            y,
                            hauteurBloc,
                            largeurBloc,
                            prixBloc,
                            segmentsBase
                    );

                    if (linteau != null) {
                        linteaux.add(linteau);
                        retraits.add(new GeometryService.SegmentHorizontal(
                                linteau.getX(),
                                linteau.getX() + linteau.getLargeur()
                        ));
                    } else {
                        retraits.add(new GeometryService.SegmentHorizontal(
                                info.xMin() - ouvertureService.getMargeArmatureTraditionnelle(),
                                info.xMax() + ouvertureService.getMargeArmatureTraditionnelle()
                        ));
                    }
                }
            }

            List<GeometryService.SegmentHorizontal> segments =
                    soustraireSegments(segmentsBase, retraits);

            segments = filtrerSegmentsMinimaux(
                    segments,
                    ConstantesBloc.LARGEUR_MINIMALE
            );

            if (segments.isEmpty() && linteaux.isEmpty()) {
                y += hauteurBloc;
                resteBloc = 0.0;
                continue;
            }

            RangeeBloc rangeeGlobale = new RangeeBloc(y);

            double resteFinRangee = construireBlocsSurSegments(
                    segments,
                    y,
                    hauteurBloc,
                    largeurBloc,
                    prixBloc,
                    resteBloc,
                    rangeeGlobale
            );

            for (Bloc linteau : linteaux) {
                rangeeGlobale.ajouterBloc(linteau);
            }

            if (!rangeeGlobale.getBlocs().isEmpty()) {
                distribuerRangeeDansGroupe(
                        rangeeGlobale,
                        groupeSecurise,
                        hauteurBloc
                );
                blocsUtilises.addAll(rangeeGlobale.getBlocs());
            }

            resteBloc = rangeeSpeciale ? 0.0 : resteFinRangee;
            y += hauteurBloc;
        }

        return blocsUtilises;
    }

    private List<GeometryService.SegmentHorizontal> segmentsFusionnesDuGroupe(
            List<ArmatureBlocs> groupe,
            double y
    ) {
        List<GeometryService.SegmentHorizontal> segments = new ArrayList<>();

        for (ArmatureBlocs armature : groupe) {
            segments.addAll(GeometryService.segmentsHorizontaux(armature, y));
        }

        return fusionnerSegments(segments);
    }

    private List<GeometryService.SegmentHorizontal> fusionnerSegments(
            List<GeometryService.SegmentHorizontal> segments
    ) {
        if (segments == null || segments.isEmpty()) {
            return List.of();
        }

        List<GeometryService.SegmentHorizontal> tries = new ArrayList<>(segments);
        tries.sort(Comparator.comparingDouble(GeometryService.SegmentHorizontal::xMin));

        List<GeometryService.SegmentHorizontal> resultat = new ArrayList<>();
        GeometryService.SegmentHorizontal courant = tries.get(0);

        for (int i = 1; i < tries.size(); i++) {
            GeometryService.SegmentHorizontal suivant = tries.get(i);

            if (suivant.xMin() <= courant.xMax() + EPSILON) {
                courant = new GeometryService.SegmentHorizontal(
                        courant.xMin(),
                        Math.max(courant.xMax(), suivant.xMax())
                );
            } else {
                resultat.add(courant);
                courant = suivant;
            }
        }

        resultat.add(courant);
        return resultat;
    }

    private void distribuerRangeeDansGroupe(
            RangeeBloc rangeeGlobale,
            List<ArmatureBlocs> groupe,
            double hauteurBloc
    ) {
        Map<ArmatureBlocs, RangeeBloc> rangeesParZone = new LinkedHashMap<>();

        for (ArmatureBlocs armature : groupe) {
            rangeesParZone.put(armature, new RangeeBloc(rangeeGlobale.getPositionY()));
        }

        for (Bloc bloc : rangeeGlobale.getBlocs()) {
            for (ArmatureBlocs armature : groupe) {
                if (blocChevaucheZone(armature, bloc, hauteurBloc)) {
                    rangeesParZone.get(armature).ajouterBloc(bloc);
                }
            }
        }

        for (Map.Entry<ArmatureBlocs, RangeeBloc> entree : rangeesParZone.entrySet()) {
            if (!entree.getValue().getBlocs().isEmpty()) {
                entree.getKey().ajouterRangee(entree.getValue());
            }
        }
    }

    private boolean blocChevaucheZone(
            ArmatureBlocs zone,
            Bloc bloc,
            double hauteurBloc
    ) {
        double yEchantillon = bloc.getY() + (hauteurBloc / 2.0);
        double blocMinX = bloc.getX();
        double blocMaxX = bloc.getX() + bloc.getLargeur();

        List<GeometryService.SegmentHorizontal> segments =
                GeometryService.segmentsHorizontaux(zone, yEchantillon);

        for (GeometryService.SegmentHorizontal segment : segments) {
            boolean chevauche =
                    blocMaxX > segment.xMin() + EPSILON
                    && blocMinX < segment.xMax() - EPSILON;

            if (chevauche) {
                return true;
            }
        }

        return false;
    }

    private double construireBlocsSurSegments(
            List<GeometryService.SegmentHorizontal> segments,
            double positionY,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc,
            double resteInitial,
            RangeeBloc rangeeComplete
    ) {
        if (segments == null || segments.isEmpty()) {
            return 0.0;
        }

        List<GeometryService.SegmentHorizontal> tries = new ArrayList<>(segments);
        tries.sort(Comparator.comparingDouble(GeometryService.SegmentHorizontal::xMin));

        double resteCourant = resteInitial;
        Double finSegmentPrecedent = null;

        for (GeometryService.SegmentHorizontal segment : tries) {
            if (segment == null || segment.largeur() < ConstantesBloc.LARGEUR_MINIMALE) {
                continue;
            }

            /*
             * Correction importante :
             * on utilise construireSegment(...),
             * car le ConstructeurRangeeService actuel attend aussi
             * la position x de départ du segment.
             */
            if (finSegmentPrecedent != null
                    && Math.abs(segment.xMin() - finSegmentPrecedent) > EPSILON) {
                resteCourant = 0.0;
            }

            ResultatRangee resultat = constructeur.construireSegment(
                    segment.xMin(),
                    segment.largeur(),
                    largeurBloc,
                    hauteurBloc,
                    prixBloc,
                    positionY,
                    resteCourant
            );

            if (resultat == null || resultat.rangee() == null) {
                resteCourant = 0.0;
                continue;
            }

            List<Bloc> blocsSegment = resultat.rangee().getBlocs();
            if (!blocsSegment.isEmpty()) {
                for (Bloc bloc : blocsSegment) {
                    rangeeComplete.ajouterBloc(bloc);
                }
            }

            resteCourant = resultat.restePourProchaineRangee();
            finSegmentPrecedent = segment.xMax();
        }

        return resteCourant;
    }

    private List<GeometryService.SegmentHorizontal> filtrerSegmentsMinimaux(
            List<GeometryService.SegmentHorizontal> segments,
            double largeurMinimale
    ) {
        List<GeometryService.SegmentHorizontal> resultat = new ArrayList<>();
        if (segments == null) {
            return resultat;
        }

        for (GeometryService.SegmentHorizontal segment : segments) {
            if (segment != null && segment.largeur() + EPSILON >= largeurMinimale) {
                resultat.add(segment);
            }
        }

        return resultat;
    }

    private List<GeometryService.SegmentHorizontal> soustraireSegments(
            List<GeometryService.SegmentHorizontal> bases,
            List<GeometryService.SegmentHorizontal> retraits
    ) {
        if (bases == null || bases.isEmpty()) {
            return List.of();
        }

        if (retraits == null || retraits.isEmpty()) {
            return new ArrayList<>(bases);
        }

        List<GeometryService.SegmentHorizontal> resultat = new ArrayList<>(bases);

        for (GeometryService.SegmentHorizontal retrait : retraits) {
            List<GeometryService.SegmentHorizontal> nouveau = new ArrayList<>();

            for (GeometryService.SegmentHorizontal base : resultat) {
                if (retrait.xMax() <= base.xMin() + EPSILON
                        || retrait.xMin() >= base.xMax() - EPSILON) {
                    nouveau.add(base);
                    continue;
                }

                if (retrait.xMin() > base.xMin() + EPSILON) {
                    nouveau.add(new GeometryService.SegmentHorizontal(
                            base.xMin(),
                            Math.min(retrait.xMin(), base.xMax())
                    ));
                }

                if (retrait.xMax() < base.xMax() - EPSILON) {
                    nouveau.add(new GeometryService.SegmentHorizontal(
                            Math.max(retrait.xMax(), base.xMin()),
                            base.xMax()
                    ));
                }
            }

            resultat = nouveau;
        }

        return resultat;
    }
}