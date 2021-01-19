Aplicación que permite visualizar productores y consumidores de Kafka en acción. Permite simular variaciones en el throughput de acuerdo a la configuración del número de productores, consumidores, particiones, y latencia en el envío y recepción de los mensajes. (En desarrollo). 

La aplicación se basa en dos thread pools, uno de consumidores y otro de productores, los cuales ejecutan runnables que consumen/producen de un broker de kafka dentro de un while loop (simulando por lo tanto consumidores y productores en paralelo). A traves de una API REST se puede manipular la cantidad de threads que corre cada uno, permitiendo probar configuraciones con distinta cantidad de productores y consumidores.

//TODO poder modificar número de partitions.

//TODO poder modificar latencia (con un Thread.sleep variable)

//TODO front end, y conectarlo con websockets. 
