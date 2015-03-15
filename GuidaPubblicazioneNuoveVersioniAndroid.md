# Questo documento riassume i passi per generare e taggare una nuova versione Android #

Come prima cosa deve essere stata generata la corretta versione Java.
Poi:

  1. nel trunk di Android, aggiornare le SVN properties in modo da farle puntare alla versione corretta del JAR e del manuale; fare poi subito dopo un commit seguito da un update, per riflettere in locale i cambiamenti. Assicurarsi, ricontrollando le SVN properties, che le modifiche siano state effettivamente applicate.
  1. aprire il progetto in Eclipse (o meglio in ADT) e cambiare i numeri di versione nel'AndroidManifest.xml (general attributes), ricordando che in base alle nostre convenzioni il _version name_ ha la forma X.Y.Z mentre il _version code_ è il numero XYZ
  1. fare un clean generale del progetto: altrimenti, potrebbero non essere rigenerati i numeri di versione corretti
  1. **far partire l'emulatore**: questo passo è indispensabile per far ri-generare l'apk, che altrimenti non viene aggiornato. In pratica è solo in questo momento che Eclipse fa realmente il build. Verificare che tutto funzioni e in particolare che la versione mostrata sia quella voluta.
  1. tornare in Eclipse e aprire il file _environment.properties_ impostando in esso l'ID su Google Code del deployer; salvare (NB: **tenere pronta la propria password su Google Code in quanto sarà chiesta per taggare**)
  1. ora, aprire la Ant View e caricare in essa (trascinandolo) il file ant/build.xml; poi, attivare il task 3 (tag) e lasciar operare. Se tutto fila liscio, verrà generato _sul server_ il nuovo tag. Fare immediatamente un update dal repository per riflettere in locale i cambiamenti.

_Cosa può andare storto:_

  * nel punto 6), il build fallisce se esiste già un tag omonimo sul server; in tal caso, occorre prima cancellarlo e per farlo sid eve prima fare un update, poi svn -> delete, poi commit
  * nel punto 6), il build fallirà anche se la pwd dell'utente Google Code è errata (ricordare che **non** è quella di Gmail ma la stringa alfanumerica speciale che si trova in Google Code sotto **project home -> nome utente -> settings**)