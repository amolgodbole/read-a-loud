Read-­a-­loud is a web portal to promote reading among the users - further referred to as readers. It provides a platform for reading free e-books.

The project aims at promoting reading through a social networking approach. The approach is to initiate group discussion and forums. To further encourage readers, group and book suggestions are provided by the system based on previous book reads and suggestions.

The features of the system are as follows:
1. Read-a-Loud is constructed by employing robust technologies (Hibernate and Apache Wicket) that provide reliable data retrieval and representation
2. Read-a-Loud allows mass registration of books and users into the system. As books are inserted, indexes are generated for the books.
3. Read-a-Loud uses relational database for storing reader and book attributes and uses MongoDB for inverted index storage. The books are stored on file system.
4. The inverted index use tf-idf (term frequency-inverted document frequency) to rank the result as per the user queries.
5. Readers can join clubs as per their reading interests. Each club is associated with a set of books that club members add. Members of the system can subscribe from any one to all the clubs.
6. Book recommendations are generated based on the previously read books and the clubs that a user is a member of.
