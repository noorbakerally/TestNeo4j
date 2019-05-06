import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import testNeo.Actor;

public class Main {
    public static void main(String [] args)  {

        Configuration configuration = new Configuration.Builder()
                .uri("file:///var/lib/neo4j/data/databases/graph.db")
                .credentials("neo4j","noor")
                .build();

        SessionFactory sessionFactory = new SessionFactory(configuration,"testNeo");
        Session session = sessionFactory.openSession();
        Actor keanu = new Actor("Keanu Reeves");
        session.save(keanu);
        sessionFactory.close();
    }
}
