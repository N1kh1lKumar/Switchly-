package live.switchly.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table (name = "organization")
public class Organization {

    @Id
    @Column(name = "id")
    private  UUID id;

    @Column(name = "name")
    private  String name;


    protected Organization(){

    }
    public Organization(UUID id, String name){
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
