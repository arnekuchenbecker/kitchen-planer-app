## Wo kommt UI State hin?
    - zwei Arten von UI state:
        * Was soll dargestellt werden (z.B. Zutaten eines Rezept, Liste von Projekten in der Datenbank, ...)
        * Wie soll Zeug dargestellt werden (z.B. ist ein Button aktiviert, ist eine Card ausgeklappt, ...)
    - Je nachdem sollte der state, den die UI zum Anzeigen verwendet an unterschiedlichen Stellen gespeichert sein:
        * "Business Logic State": 
            + Zugriff über ein ViewModel (das wiederum die Daten via Flows etc. vom Domain Layer bekommt)
            + 1 ViewModel / Screen
            + ViewModels sollten nicht in Komponenten durchgereicht werden, sondern der Screen (also die äußerste Composable Funktion, die das ViewModel
              kennt) extrahiert für die Komponente relevanten State und gibt den weiter
        * "UI Element State":
            + Direkt im Composable
            + Achtung: Normale Variablen werden bei jeder Re-Composition zurückgesetzt (1) UND triggern keine Re-Composition, wenn ihre Werte sich ändern (2)
            + Lösung für (1): remember() --> merkt sich den Wert über Re-Compositions
            + Lösung für (2): State / MutableState --> Observable, d.h. wenn sich der dahinterliegende Wert ändert, wird Re-Composition getriggert
            + Übliches Pattern: ```var x by remember { mutableStateOf(3) }``` --> kann x wie normale variable behandeln, ist im Hintergrund aber State (--> (2))
              und remember (--> (1))
            + https://dev.to/zachklipp/remember-mutablestateof-a-cheat-sheet-10ma
        * https://developer.android.com/topic/architecture/ui-layer/stateholders