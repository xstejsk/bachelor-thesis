package com.rstejskalprojects.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "event_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty
    @Column(unique = true)
    @NotBlank(message = "Location name cannot be empty")
    private String name;
    @JsonIgnore
    @OneToMany(mappedBy = "location",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Event> events;
    @JsonProperty
    @NotNull
    private LocalTime opensAt;
    @NotNull
    @JsonProperty
    private LocalTime closesAt;

    public Location(String name, LocalTime opensAt, LocalTime closesAt) {
        this.name = name;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", opensAt='" + opensAt + '\'' +
                ", closesAt='" + closesAt + '\'' +
                '}';
    }
}
