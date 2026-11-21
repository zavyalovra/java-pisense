package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "bridges", schema = "public")
@Getter
@Setter
@ToString
public class Bridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** pisense.bridge-id из application.properties */
    @Column(name = "bridge_name", nullable = false, unique = true)
    private String bridgeName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant lastSeenAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bridge)) return false;
        return id != null && id.equals(((Bridge) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
