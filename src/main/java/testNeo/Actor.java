package testNeo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Actor {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;



    public Actor() {
    }

    public Actor(String name) {
        this.name = name;
    }


}
