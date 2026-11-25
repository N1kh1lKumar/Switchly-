package live.switchly.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "flag")
public class Flag {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private boolean enabled;

    protected Flag() {
        // for JPA
    }

    public Flag(UUID id, UUID organizationId, UUID projectId, String key, String name, boolean enabled) {
        this.id = id;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.key = key;
        this.name = name;
        this.enabled = enabled;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getProjectId() { return projectId; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}