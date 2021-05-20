package ru.clevertec.check.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "warehouse")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductWarehouse {
    @Id
    @PrimaryKeyJoinColumn
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Integer quantity;
}
