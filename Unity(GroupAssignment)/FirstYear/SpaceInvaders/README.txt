Resoulution: Spillet er spillbart i alle oppl�sningner. Litt trangt p� 640x480. Optimalt er standardoppl�sning som er 1920x1080. Testet opp til 2560x1600.

Bevegelse med a og d, skyting med space. Space brukes ogs� til menyvalg. Jeg valgte dette heller enn en button men legger ved koden kommenterte s� du ser at jeg behersker GUI buttons.

Var ogs� fristet til � legge til liv, men antok at du ikke ville ha det for at du skal kunne avslutte spillet hurtig. Som du vil se av koden vil det v�re en smal sak � implementere en LifeCounter etter samme model som ScoreCounter og binde denne opp mot GameManager med states osv.

Jeg vurderte en statemachine for playeren og en egen for aliens men funksjonaliteten er s�pass simpel at jeg f�ler det bare ville v�rt sm�r p� flesk. Har laget en statemachine for spillet overordnet slik jeg har gjort i tidligere innleveringer, fordi jeg synes det er en ryddig og god m�te � organisere spillet.

Designet ogs� spillet som en throwback til arcaden i gamle dager s� det er ingen quit button. Bruk alt+f4!

Enjoy! :)

Known issues final:

-Lagger i fullscreen p� min stasjon�re. Ingen anelse hvorfor.
