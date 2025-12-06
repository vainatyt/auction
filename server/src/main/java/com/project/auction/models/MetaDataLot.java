package com.project.auction.models;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("metadata_lot")
public class MetaDataLot {

    @Column("id_lot")
    private Long lotId;
    @Column("name")
    private String name;
    @Column("description")
    private String description;

    public MetaDataLot() {
    }

    public MetaDataLot(Long lotId, String name, String description) {
        this.lotId = lotId;
        this.name = name;
        this.description = description;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
