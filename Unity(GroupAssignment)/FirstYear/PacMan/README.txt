PG2201, Unity utvikling, Innlevering 1
--------------------------------------

I denne oppgaven har vi valgt å implementere vår utgave av PacMan i en miks av 2D og 3D.

Banen som blir benyttet er laget i Google Sketch Up 8 Pro da vi følte at dette ville gjøre
arbeidet med implementasjon av AI lettere.

Så lenge lenge minst en av de fire Power-Up ikke er "spist" av PacMan foregår spillet i 2D,
så tett opp mot orginalen som vi klarte. Da den siste Power-Up'en blir "spist" går spillet
over i en 3D-mode for resten av levelen. Dette gjorde vi for å vise at vi behersker både
2D og 3D. Vi er av den oppfatningen at de bugs som forekommer er på grunn av manglende
kompetanse i matematikk og ikke dårlig forståelse av Unity motoren og dens funksjoner.

Alle assets som blir benyttet i spillet er laget av gruppens medlemmer.
Dette gjelder forøvrig ikke lydene som blir spilt av i spillet. 
Disse er hentet fra http://www.classicgaming.cc/classics/pacman/sounds.php.
Sangen som blir avspilt under 3D-mode er Knife Party - Rage Valley.

Den mest utfordrende delen av oppgaven var AI til spøkelsene. Vi implementerte AI'en til
spøkelsene i henhold til oppgaven, men ettersom dette var en spennende del bestemte vi
oss for å forsøke å implemtere AI som er nærmere den vi finner i orginalspillet.
Dette klarte vi ikke å fullføre. Vi tror problemet er relatert til korrekte vinkel-utregninger.
Den aktuelle koden er kommentert ut i filen GhostRandomMove.cs. 