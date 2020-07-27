package uk.ac.newcastle.redhat.gavgraph.repository;

import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import uk.ac.newcastle.redhat.gavgraph.config.MyConfiguration;

import java.util.Collections;
import java.util.Map;

import static org.neo4j.ogm.cypher.ComparisonOperator.EQUALS;

public class MyNeo4jRepository<E> {

    protected Class<E> entityTypeClazz;
    protected Session neo4jSession;

    public MyNeo4jRepository(Class<E> entityTypeClazz) {
        MyConfiguration myConfiguration = new MyConfiguration();
        SessionFactory sessionFactory = myConfiguration.sessionFactory();
        //get Ogm session
        neo4jSession = sessionFactory.openSession();
        this.entityTypeClazz = entityTypeClazz;
        //neo4jSession = Neo4jSessionFactory.getInstance().getNeo4jSession();
    }

    public Iterable<E> getPage(int pageNumber, int pageSize, int depth) {
        Pagination p = new Pagination(pageNumber, pageSize);
        return neo4jSession.loadAll(entityTypeClazz, p, depth);
    }

    public Iterable<E> getPage(int pageNumber, int pageSize) {
        return getPage(pageNumber, pageSize, 1);
    }

    public Iterable<E> getEntity(String key, String value) {
        //default depth = 1
        return neo4jSession.loadAll(entityTypeClazz, new Filter(key, EQUALS, value));
    }

    public E getSingleEntity(String primaryKey) {
        return neo4jSession.load(entityTypeClazz, primaryKey);
    }

    public int getCount() {
        return (int) neo4jSession.countEntitiesOfType(entityTypeClazz);
    }

    public void clearCache() {
        neo4jSession.clear();
    }

    public Long queryCount(String cypherQuery) {
        return (Long) neo4jSession.query(cypherQuery, Collections.EMPTY_MAP ).iterator().next().values().iterator().next();
    }
    public Iterable<E> query(String cypherQuery) {
        return neo4jSession.query(entityTypeClazz, cypherQuery, Collections.EMPTY_MAP);
    }
    public Iterable<E> query(String cypherQuery, Map<String, ?> params) {
        return neo4jSession.query(entityTypeClazz, cypherQuery, params);
    }
    public Result queryForResult(String cypherQuery) {
        return queryForResult(cypherQuery, Collections.EMPTY_MAP);
    }
    public Result queryForResult(String cypherQuery, Map<String, ?> params) {
        return neo4jSession.query(cypherQuery, params);
    }

}
