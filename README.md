BQPrueba
========

##Ciclo de vida de la aplicación:

![Screenshot] (https://github.com/VSADevelopment/BQPrueba/blob/master/diagrama.png?raw=true)

##¿Como usar la aplicación?
 Al ejecutar la aplicación por primera vez se solicitarán las credenciales y la autorización para que la aplicación pueda usar tu cuenta de Dropbox. Una vez hecho esto debes subir tus archivos epub al directorio Dropbox (/Aplicaciones/BQPrueba). A continuación, debes volver a ejecutar la aplicación, entonces se sincronizarán los ficheros y se mostrará una lista con los títulos de los libros. Haciendo click en cada uno de los títulos, se mostrará la portada del libro.

##Conexión a Dropbox:
* Para la conexón se ha utilizado la API de Dropbox (https://www.dropbox.com/developers), de modo que cuando se ejecuta la aplcación, se solicitarán las credenciales, ya sea mediante el navegador, o mediante la aplicación de Dropbox para Android, en caso de disponer de ella. 
* A la aplicación se le ha proporcionado un permiso restringido a su propio directorio, tal y como es aconsejado por Dropbox. Este directorio se crea automaticamente en la cuenta de dropbox en el momento en que es autorizada (/Aplicaciones/BQPrueba/). Trás la primera ejecución, debemos subir los archivos .epub a nuestro directorio /Aplicaciones/BQPrueba. A continuación, debemos ejecutar de nuevo la aplicación para visualizar los libros.
* No se ha implementado un sistema de gestión de sesiones, por lo que la aplicación solicita autorización de Dropbox en cada ejecución.

##Sincronización de archivos:
* Una vez obtenida la autorización, la aplicación descarga los archivos con extensión epub ubicados en el directorio de Dropbox correspondiente (/Aplicaciones/BQPrueba). Este proceso se realiza de forma concurrente mediante una clase privada que hereda del objeto AsyncTask proporcionada por el framework de Android.

##Tratamiento de archivos epub:
* El tratamiendo de los epub se realiza a través de la librería Epublib (http://www.siegmann.nl/epublib) desarrollada por Paul Siegmann bajo licencia GNU. Esta librería permite extraer toda la información de un archivo epub. En este caso, se ha usado para obtener los títulos y las imágenes de los libros.

##Notas generales:
* La aplcación muestra los libros mediante un ListView, ya que es el control más adecuado que proporciona Android para mostrar una lista de elementos. En dicha lista de libros ya se muestran directamente los títulos de los mismos, en lugar de los nombres de los ficheros.
