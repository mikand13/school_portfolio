Resoulution: Spillet er spillbart i alle oppløsningner. Litt trangt på 640x480. Optimalt er standardoppløsning som er 1920x1080. Testet opp til 2560x1600.

Bevegelse med a og d, skyting med space. Space brukes også til menyvalg. Jeg valgte dette heller enn en button men legger ved koden kommenterte så du ser at jeg behersker GUI buttons.

Var også fristet til å legge til liv, men antok at du ikke ville ha det for at du skal kunne avslutte spillet hurtig. Som du vil se av koden vil det være en smal sak å implementere en LifeCounter etter samme model som ScoreCounter og binde denne opp mot GameManager med states osv.

Jeg vurderte en statemachine for playeren og en egen for aliens men funksjonaliteten er såpass simpel at jeg føler det bare ville vært smør på flesk. Har laget en statemachine for spillet overordnet slik jeg har gjort i tidligere innleveringer, fordi jeg synes det er en ryddig og god måte å organisere spillet.

Designet også spillet som en throwback til arcaden i gamle dager så det er ingen quit button. Bruk alt+f4!

Enjoy! :)

Known issues final:

-Lagger i fullscreen på min stasjonære. Ingen anelse hvorfor.
