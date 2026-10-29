package org.campusguard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String building;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    public Location() {}

    public Location(Long id, String name, String building, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.description = description;
        this.active = active != null ? active : true;
    }

    public Location(String name, String building, String description) {
        this.name = name;
        this.building = building;
        this.description = description;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
