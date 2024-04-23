package com.dreamsol.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EndPoint {
    @Id
     //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String endpoint;
}
