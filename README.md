# completable-futures
This project aims to make asyncronous programming easier.

 - [`CompletableFutures.anyOf`](src/main/java/name/falgout/jeffrey/concurrent/CompletableFutures.java) returns a `Future<Optional<T>>`
 - `CompletableFutures.allOf` returns a `Future<List<T>>`
 - [`CompletableFutures.stream`](src/main/java/name/falgout/jeffrey/stream/future/FutureStream.java) lets you perform `java.util.stream.Stream` operations on `Future`s. Each terminal operation of the stream returns a `Future`.
 
 ###How can I use this library in my project?
 - [Clone the repository](http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository) and use maven to generate a .jar file:

Note: You will need to download [ThrowingStreams](https://github.com/JeffreyFalgout/ThrowingStreams) and create a snapshot.
````
git clone https://github.com/JeffreyFalgout/completable-futures
cd completable-futures/
mvn package
````
 - Download a .jar from the [releases](../../releases/) page (eventually!)
 - Maven central: Coming soon!
