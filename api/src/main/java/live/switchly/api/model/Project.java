package live.switchly.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JoinColumnOrFormula;

import java.util.UUID;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @Column(name = "id")
    private  UUID id;

    @Column(name = "organization_id")
    private  UUID organizationId;

    @Column(name = "name")
    private  String name;


    protected Project(){

    }

    public Project(UUID id, UUID organizationId, String name) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
}