package com.project.auction.repository;

import com.project.auction.models.MetaDataLot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MetaDataLotRepositoryTest {

    @Autowired
    private MetaDataLotRepository metaDataLotRepository;

    @Test
    void findByLotId_returnsMetaData() {
        MetaDataLot meta = new MetaDataLot();
        meta.setLotId(1L);
        meta.setName("Phone");
        meta.setDescription("iPhone 15");
        metaDataLotRepository.save(meta);

        Optional<MetaDataLot> result = metaDataLotRepository.findByLotId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Phone");
    }

    @Test
    void findByLotId_returnsEmpty_forNonExisting() {
        Optional<MetaDataLot> result = metaDataLotRepository.findByLotId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByLotId_singlePage_returnsPage() {
        MetaDataLot meta1 = new MetaDataLot();
        meta1.setLotId(2L);
        meta1.setName("TV");
        metaDataLotRepository.save(meta1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<MetaDataLot> result = metaDataLotRepository.findByLotId(2L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("TV");
    }

    @Test
    void findByLotIdIn_returnsMultiple() {
        metaDataLotRepository.save(createMeta(10L, "Laptop"));
        metaDataLotRepository.save(createMeta(20L, "Monitor"));
        metaDataLotRepository.save(createMeta(30L, "Keyboard"));

        Pageable pageable = PageRequest.of(0, 5);
        Page<MetaDataLot> result = metaDataLotRepository.findByLotIdIn(List.of(10L, 30L), pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(MetaDataLot::getName)
                .containsExactlyInAnyOrder("Laptop", "Keyboard");
    }

    @Test
    void deleteByLotId_removesMetaData() {
        MetaDataLot meta = createMeta(5L, "Book");
        metaDataLotRepository.save(meta);

        metaDataLotRepository.deleteByLotId(5L);

        Optional<MetaDataLot> afterDelete = metaDataLotRepository.findByLotId(5L);
        assertThat(afterDelete).isEmpty();
    }

    private MetaDataLot createMeta(Long lotId, String name) {
        MetaDataLot meta = new MetaDataLot();
        meta.setLotId(lotId);
        meta.setName(name);
        meta.setDescription("Test desc");
        return meta;
    }
}
