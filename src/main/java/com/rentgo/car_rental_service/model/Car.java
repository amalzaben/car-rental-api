package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="family", nullable=false, length=20)
    private String family;

    @Column(name="type", nullable=false, length=20)
    private String type;

    @Column(name="seats", nullable=false)
    private Integer seats;

    @Column(name="fuel", length=20)
    private String fuel;

    @Column(name="price", precision=10, scale=2)
    private BigDecimal price;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @Lob
    @Column(name = "picture_data")
    private byte[] pictureData;

    @Column(name = "picture_content_type", length = 50)
    private String pictureContentType;


    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=30)
    private CarStatus status;
}
