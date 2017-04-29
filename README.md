# TODO

- Kezeljük le azt is, ha olyan szót adtunk meg, ami nincs benne a wrok vector-ban.
- TrainRecipes.java, TestRecipes.java: A jarban lévő RecipeData mappa egy temp mappába másolása.
- A maven-assembly-plugin segítségével csak a szükséges fájlokat kéne bemásolni egy jar-ba.
- Run configurationt létrehozni mindhárom jarnak.
- Dokumentáció:
    - Egy összefoglalót írni a projektről a README.md-be, magyarul.
    - maven-site-plugin bekötése.
    - Dokumentáció a fordításról és a futtatásról sitedocként, angolul.
    - Javadoc report bekötése.
    - A kód strukturáltságának javítása, ezzel együtt javadoc és unit tesztek írása.

# Recipe Analyzer

Az ELTE Projekt Eszközök gyakorlatára készített beadandó. Készült a 2016/17-es tanítása év tavaszi félévében. Készítette: Mészáros Renáta, Vári Csaba, Mester Zsolt.

## Fordítás

1. Legyen feltelepítve egy *8-as JDK*.
2. A *JAVA_HOME* környezeti változó az előbbi rootjába mutasson.
2. Legyen feltelepítve egy *3.3.9-es Maven*.
3. Fordítani a *pom.xml* mappájából lehet a következő paranccsal: `mvn clean package`. A `clean` törli a *target*-et (hogy tiszta lappal induljon a fordítás), a `package` meg lefordítja a java forrásfájlokat és megcsinálja a *jar*-t. Az utóbbi a target mappába került a következő néven: *recipe_analyzer.jar*.

## Futtatás

1. Futtatni ezzel a parancsal lehet, amit a *target* mappában kell kiadni: `java -jar recipe_analyzer.jar`.

## Unit tesztek fordítása és futtatása

A unit tesztek automatikusan fordulnak és lefutnak a package fázisnál. Ha egy teszt elhal, akkor a build is elhal. Példa: *SampleJUnitTest*.

## Dokumentáció fordítása és megtekintése

## IntelliJ IDEA integráció

1. Az IDEA legutolsó stabil verzióját töltsétek le [innen](https://www.jetbrains.com/idea/download/).
2. Importáljátok a projektet: *Import project*, majd válasszátok ki a *pom.xml*-t. A megjelenő ablakban alul van egy *Environment settings...*, itt válasszátok ki, hogy az előre csomagolt helyett a saját 3.3.9-es Maven-eteket használja. Utána tovább addig, amíg az SDK kiválasztó ablakhoz nem kerültök. Itt adjátok hozzá (ha kell) és válasszátok ki a 8-as JDK-t. A végén még egy next és egy finish.
3. Ha betöltötte a projektet és a jobb alsó sarokban kiírja, hogy *Unregistered VCS root detected*, akkor válasszátok azt, hogy *Add root*.
4. Zárjátok be az IDEA-t, másoljátok be a *runConfigurations* mappát a *.idea* mappába, majd indítsátok el az IDEA-t. Ezután lesz egy *clean package* nevű conf, ami futtat egy `mvn clean package`-t.

## Code style

Az IDEA default kódformázóját használjuk, amit a következővel tudunk futtatni: **ctrl + alt + l**.
