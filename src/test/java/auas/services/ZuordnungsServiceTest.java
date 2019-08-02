package auas.services;

import auas.read_only.domain.Abgabe;
import auas.read_only.domain.Blatt;
import auas.read_only.domain.Korrektor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ZuordnungsServiceTest {

    ZuordnungsService zuordnungsService;
    KorrektorService korrektorService;
    BlattService blattService;


    @Before
    public void setup() {
        zuordnungsService = mock(ZuordnungsService.class);
        korrektorService = mock(KorrektorService.class);
        blattService = mock(BlattService.class);

    }

    private int max_stunden = 0;

    @Test
    public void einKorrektor_vieleAbgaben() {
        List<Abgabe> abgaben = new ArrayList<>();
        initAbgaben(abgaben, 10);

        List<Blatt> blaetter = new ArrayList<>();
        blaetter.add(new Blatt(0, abgaben));
        Blatt blatt = blaetter.get(0);

        Deque<Korrektor> korrektoren = new LinkedList<>();
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Anton", 5));

        //Check with Mockito
        Mockito.when(korrektorService.getAll()).thenReturn(korrektoren);
        Mockito.when(blattService.getAllBlaetter()).thenReturn(blaetter);

        ZuordnungsService zuordnung = new ZuordnungsService(blattService, korrektorService);
        zuordnung.abgabenZuordnen(0);

        List<Abgabe> alleAbgaben = blatt.getAlleAbgaben();
        Korrektor korrektor = korrektoren.getFirst();
        long count = alleAbgaben.stream().filter(x -> x.getKorrektor().equals(korrektor)).count();

        assertEquals(10, count, 0.1);

    }

    @Test
    public void einKorrektor_eineAbgabe() {

        List<Abgabe> abgaben = new ArrayList<>();
        initAbgaben(abgaben, 1);

        List<Blatt> blaetter = new ArrayList<>();
        blaetter.add(new Blatt(0, abgaben));
        Blatt blatt = blaetter.get(0);

        Deque<Korrektor> korrektoren = new LinkedList<>();
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Anton", 5));

        //Check with Mockito
        Mockito.when(korrektorService.getAll()).thenReturn(korrektoren);
        Mockito.when(blattService.getAllBlaetter()).thenReturn(blaetter);

        ZuordnungsService zuordnung = new ZuordnungsService(blattService, korrektorService);
        zuordnung.abgabenZuordnen(0);

        List<Abgabe> alleAbgaben = blatt.getAlleAbgaben();
        Korrektor korrektor = korrektoren.getFirst();
        long count = alleAbgaben.stream().filter(x -> x.getKorrektor().equals(korrektor)).count();

        assertEquals(1, count, 0.1);


    }


    @Test
    public void zweiKorrektoren_vieleAbgaben() {

        List<Abgabe> abgaben = new ArrayList<>();
        initAbgaben(abgaben, 100);

        List<Blatt> blaetter = new ArrayList<>();
        blaetter.add(new Blatt(0, abgaben));
        Blatt blatt = blaetter.get(0);

        Deque<Korrektor> korrektoren = new LinkedList<>();
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Anton", 10));
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Bernd", 5));

        Mockito.when(korrektorService.getAll()).thenReturn(korrektoren);
        Mockito.when(blattService.getAllBlaetter()).thenReturn(blaetter);


        ZuordnungsService zuordnung = new ZuordnungsService(blattService, korrektorService);
        zuordnung.abgabenZuordnen(0);

        List<Abgabe> alleAbgaben = blatt.getAlleAbgaben();

        for (Korrektor k : korrektoren) {
            double expected = (((double) 100 / 15) * (double) k.getStunden());                                // INFO : Abgaben/gesamtstunden * korrektorstunden
            long count = alleAbgaben.stream().filter(x -> x.getKorrektor().equals(k)).count();
            assertEquals((int) expected, count, 1);
        }


    }

    @Test
    public void zweiKorrektoren_zweiAbgaben() {

        List<Abgabe> abgaben = new ArrayList<>();
        initAbgaben(abgaben, 2);

        List<Blatt> blaetter = new ArrayList<>();
        blaetter.add(new Blatt(0, abgaben));
        Blatt blatt = blaetter.get(0);

        Deque<Korrektor> korrektoren = new LinkedList<>();
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Anton", 5));
        korrektoren.add(new Korrektor(UUID.randomUUID(), "Bernd", 5));

        Mockito.when(korrektorService.getAll()).thenReturn(korrektoren);
        Mockito.when(blattService.getAllBlaetter()).thenReturn(blaetter);


        ZuordnungsService zuordnung = new ZuordnungsService(blattService, korrektorService);
        zuordnung.abgabenZuordnen(0);

        for (int i = 0; i < korrektoren.size(); i++) {

            Korrektor korrektor = korrektoren.getFirst();

            List<Abgabe> alleAbgaben = blatt.getAlleAbgaben();
            long count = alleAbgaben.stream().filter(x -> x.getKorrektor().equals(korrektor)).count();
            assertEquals(1, count, 0.2);
            korrektoren.removeFirst();
        }
    }

    @Test
    public void stundentotal() {

        Deque<Korrektor> korrektoren = new LinkedList<>();
        korrektoren = randomKorrektoren(10);

        Mockito.when(korrektorService.getAll()).thenReturn(korrektoren);
        ZuordnungsService zuordnung = new ZuordnungsService(blattService, korrektorService);
        int sum = zuordnung.gesamtstunden(korrektoren);
        assertEquals(max_stunden, sum, 0);
    }


    /************************** UTILITY FOR TEST CLASS BUILDING ******************************/

    private Deque<Korrektor> randomKorrektoren(int amount) {
        Deque<Korrektor> korrektoren = new LinkedList<>();
        int max = 12;
        int min = 1;
        int random = 0;

        for (int i = 0; i < amount; i++) {
            random = (int) (Math.random() * ((max - min) + 1)) + min;
            korrektoren.add(new Korrektor(UUID.randomUUID(), "person", random));
            max_stunden += random;
        }

        return korrektoren;

    }

    private void initAbgaben(List<Abgabe> abgaben, int amount) {
        for (int i = 0; i < amount; i++) {
            abgaben.add(new Abgabe());
        }
    }

    private void check_Abgaben_von_Korrektor(Korrektor korrektor, double expected, List<Abgabe> alleAbgaben) {

        long count = alleAbgaben.stream().filter(x -> x.getKorrektor().equals(korrektor)).count();
        assertEquals((int) expected, count, 1);
    }


}


