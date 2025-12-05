package com.project.auction.repository;

import org.springframework.data.repository.CrudRepository;
import com.project.auction.models.Goods;


public interface GoodsRepository extends CrudRepository<Goods, Long> {
}
