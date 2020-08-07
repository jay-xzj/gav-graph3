# gav-graph3

This project is bulit up with Spring Boot, Swagger, lohg4j2, Neo4j OGM, etc.

The goal is mainly to study the complex internal dependencies of maven central repository. 
Through the analysis and data cleaning of the 4 million pom files, finally, presented the data as a graph in the Neo4j. 
Providing a well-structured API for users to quickly and efficiently query.

To setup this project, You should have Neo4j desktop on your laptop.
Step 1. Load the csv files into the database using cyphers in loadcsv.cypher.
Step 2. Run GavGraphApplication.java file's main method to start the app.
Step 3. Use url: http://localhost:8090/swagger-ui.html to open the swagger-ui page.
