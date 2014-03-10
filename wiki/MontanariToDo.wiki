  * Fix the compilation with IKVM on mac os x (build.xml)

plugin
  * il trunk ha tutto (sono tre progetti indipendenti ma con interdipendenze) ma nei branch si mette solo il codice del plugin ((o degli altri feature o updatesite) -> evenienza molto rara)
  * nei tag si tagga solo il codice del plugin, così non si replicano inutilmente i jar dei plugin e features. La parte che merita di essere taggata e' quella del plugin vero e proprio.
  * mettere note su quello sopra e note sulle versioni vecchie che sono manuali e non hanno update site

ikvm branch
  * per ogni versione di ikvm serve mettere un'informazione su cosa va e cosa non va

da rivedere
  * sezione download sul repo e aggiunta download automatica (ant? http://code.google.com/p/ant-googlecode/) (vedi mail "meditazione").

maven
  * punto d'inizio --> http://stackoverflow.com/questions/1280470/maven-repository-for-google-code-project