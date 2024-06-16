Caso de uso:

Consulta masiva de miles de datos en base de datos sin que la plataforma se vea comprometida.

Ejemplo:

Sacar un informe de todos los usuarios que empiecen por una letra o conjunto de letras en una base de datos que contiene millones de registros.

Para ello disponemos de un indexador de contenidos (elasticsearch) donde tenemos indexado los nombres de los usuarios. Por otro lado tenemos mongodb donde tenemos almacenado todo los datos completos de los usuarios. Por último disponemos de una memoria caché para tener cacheado los usuarios.

Problemas:
Si hacemos una consulta en elasticsearch de forma normal para traernos todos los registros, el tiempo de la consulta será alta y todas las demás peticiones que lleguen por otros flujos a elasticserach se encolarán y se verán ralentizadas hasta que termine la primera consulta. Además elasticsearch no permite traer más de 10k elementos para una consulta, nisiquera paginando por lo que esa opción no es viable.

Para ello la mejor opción es usar un scroll que consiste en traer poco a poco los datos de x en x registros. Gracias al scroll podemos traernos tantos datos como queramos sin el límite de 10k que tiene para consultas normales. Hay que tener en cuenta que aunque tengamos un elasticsearch montado en cluster con varios nodos, el scroll solo se genera en un nodo, y aunque se vaya pidiendo por lotes, siempre asumirá la carga el nodo donde se generó. Por otro lado, elasticsearch tiene la opción de no traerse el objeto source que es el que contiene toda la información. Así solo se traería el campo id de cada usuario sin necesidad de ningún campo más. Gracias a eso y al ir por lotes, el resto de consultas que llegan de la plataforma no se ven afectados dado que se cuelan entre consulta y consulta del scroll. Una vez que tenemos todos los miles de ids de la consulta, necesitamos toda la información completa del usuario para generar el informe. Esa información está en mongodb y puede estar también cacheada en redis. Para ello vamos a usar la lógica dividir ese listado de miles de ids en sublistas de 50 elementos y por cada lista vamos a usar la estrategia de primero consultar por lote a la cache de redis con una única consulta y los que no esté en redis los pedimos igual por lote con una única consulta a mongodb. Aprovechamos para meter en cache todos los usuarios que no estaban cacheados y que nos acabamos de traer de mongodb para futuras consultas. Todo este proceso se repite por cada sublista de 50 elementos y para aprovechar la potencia de tener en cluster tanto redis como mongodb, lo hacemos en paralelo.

Como todo se hace en bloques pequeños y en paralelo, conseguimos extraer todos los datos de una manera muy rápida y sin ralentizar los demás flujos de la plataforma. Tanto la subdivición de la lista de ids en listas de 50 elementos como el tamaño de elementos extraidos por cada iteración del scroll son personalizables en función de la plataforma.

Ejemplo:

	sudo prueba
