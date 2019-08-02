package auas.services;

import auas.read_only.domain.Abgabe;
import auas.read_only.domain.Blatt;
import auas.read_only.domain.Korrektor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Deque;
import java.util.List;

@Service
public class ZuordnungsService {

    public BlattService blattService;
    public KorrektorService korrektorService;

    @Inject
    public ZuordnungsService(BlattService blattService, KorrektorService korrektorService) {
        this.blattService = blattService;
        this.korrektorService = korrektorService;
    }

    public void abgabenZuordnen(int i) {
        List<Blatt> blaetter = blattService.getAllBlaetter();
        Blatt blatt = blaetter.get(i);
        Deque<Korrektor> korrektoren = korrektorService.getAll();
        List<Abgabe> unzugeordnete_abgaben = blatt.getUnzugeordneteAbgaben();
        int gesamtstunden = gesamtstunden(korrektoren);

        while (!unzugeordnete_abgaben.isEmpty()) {


            int total_abgaben = unzugeordnete_abgaben.size();

            for (Korrektor k : korrektoren) {

                double abgaben_fuer_k = (((double) total_abgaben) / gesamtstunden) * k.getStunden();

                for (int j = 0; (int) abgaben_fuer_k > j; j++) {
                    Abgabe abgabe = unzugeordnete_abgaben.get(0);
                    blatt.abgabeZuordnen(abgabe, k);
                    unzugeordnete_abgaben.remove(0);
                }
            }
            if (unzugeordnete_abgaben.size() <= korrektoren.size()) {
                for (int abgaben = 0; abgaben < unzugeordnete_abgaben.size(); abgaben++) {
                    Korrektor k = korrektoren.getFirst();
                    Abgabe abgabe = unzugeordnete_abgaben.get(0);
                    blatt.abgabeZuordnen(abgabe, k);
                    unzugeordnete_abgaben.remove(0);
                    korrektoren.removeFirst();
                    blattService.save(blatt);
                }
                break;
            }
        }
            blattService.save(blatt);
    }

    public int gesamtstunden(Deque<Korrektor> korrektoren) {
        int total = 0;
        for (Korrektor k : korrektoren) {
            total += k.getStunden();
        }
        return total;
    }
}

