# Guida alla pubblicazione delle nuove versioni del plug-in #

Quando si ha una nuova versione del plug-in non c’è bisogno di cambiare la versione direttamente nel manifest del plug-in stesso, verrà fatto tutto tramite la feature.

La procedura è la seguente:

  1. Nell’Overview della feature (feature.xml) si cambia la versione della feature che sarà la stessa impostata anche nel plug-in.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/EclipseGuide/feature.png' width='75%' />

Per fare ciò si va in : Plug-ins -> Versions... -> Force feauture version in plug-in e fragment manifests -> Finish . Salvando le modifiche si ha la nuova versione della feature e del plug-in.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/EclipseGuide/feature2.png' width='75%' />

> 2. Per aggiungere la nuova versione nell’ update site si lavora su “site.xml”: Site Map -> Seleziona la categoria “tuProlog plug-in features” -> Add Feature.. e si seleziona l’ultima versione.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/EclipseGuide/UpdateSite.png' width='75%' />

Synchronize... -> Synchronize all features in the site -> Finish -> Build All

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/EclipseGuide/UpdateSite2.png' width='75%' />

A questo punto verranno generati i due jar della nuova versione del plug-in e della feature nelle rispettive cartelle.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/EclipseGuide/project.png' width='50%' />