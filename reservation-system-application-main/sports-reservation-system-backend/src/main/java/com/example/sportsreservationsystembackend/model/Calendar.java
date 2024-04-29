package com.example.sportsreservationsystembackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * This class represents calendar entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "calendar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"events", "thumbnail"})
@EqualsAndHashCode(exclude = {"events", "thumbnail"})
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="location_id", nullable=false)
    private Location location;

    @Column(unique = true)
    @NotBlank(message = "Calendar name cannot be blank")
    private String name;

//    @NotBlank(message = "Calendar description cannot be blank")
    private byte[] thumbnail;

    @JsonIgnore
    @OneToMany(mappedBy = "calendar",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Event> events;

}
