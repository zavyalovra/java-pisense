package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "buses", schema = "public")
@Getter
@Setter
@ToString
public class Bus extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bridge_id", nullable = false)
    @ToString.Exclude
    private Bridge bridge;

    @Column(nullable = false)
    private String name;
}
