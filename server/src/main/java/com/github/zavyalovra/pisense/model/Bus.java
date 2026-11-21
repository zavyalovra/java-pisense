package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buses", schema = "public")
@Getter
@Setter
@ToString
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_name", nullable = false)
    private String busName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bridge_id", nullable = false)
    @ToString.Exclude
    private Bridge bridge;

    public Bus(Bridge bridge, String busName) {
        this.bridge = bridge;
        this.busName = busName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus)) return false;
        return id != null && id.equals(((Bus) o).getId());
    }

    @Override
    public int hashCode() {
        return Bus.class.hashCode();
    }
}
