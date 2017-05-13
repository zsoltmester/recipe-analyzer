# A projectről

A program célja receptek elemzése egy rekurrens neurális háló segítségével. A rekurrens hálók előnye, hogy tetszőleges méretű halmazon dolgozhatunk velük, jelen esetben a receptek mennyisége és hossza változó.
A tanító adatok fel vannak osztva kategóriákra, például édesség, levesek, stb. A jelenleg használt modell ezen kategóriák alapján lett betanítva, de lehetőség van máshogy rendezni az adatokat és új modellt készíteni.

# TODO

- Kezeljük le azt is, ha olyan szót adtunk meg, ami nincs benne a wrok vector-ban.
- TrainRecipes.java, TestRecipes.java: A jarban lévő RecipeData mappa egy temp mappába másolása.
- A maven-assembly-plugin segítségével csak a szükséges fájlokat kéne bemásolni egy jar-ba.
- Run configurationt létrehozni mindhárom jarnak.
- Dokumentáció:
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

## Haladó használat

1. Resource fájlok
  1. A recepteket először osszuk szét kategóriánként és egy recept egy sorban legyen.
  2. Ezeket egyenként osszuk szét tanító és tesztelőhalmazra, úgy hogy az előbbi tartalmazza a receptek nagy részét.
  3. A categories.txt-be írjuk bele a kategóriákat sorszámokkal.
2. Előkészítés
  1. A receptek szavait vektorizálni kell, ehhez használjuk a PrepareWordVector-t, ez elkészíti a RecipesWordVector.txt fájlt.
  2. A modell ekészítéséhez állítsuk be a TrainRecipes-ben a truncate-t, batchsize-t és a epoch-ok számát majd indítsuk el a tanítást.
  3. A truncate segítségével lekorlátozhatjuk a receptek elemzett részének hosszát.
  4. A batchsize és az epoch adja meg mennyi ideig tart a tanulás, hosszabb tanulás eseten várható a pontosság növekedése.
3. Használat
  1. Indítsuk el a TestRecipes-t. A szöveges mezőbe írjuk be a vizsgálni kívánt recept szövegét.


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
