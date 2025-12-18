package com.project.auction.repository;

import com.project.auction.models.Photo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PhotoRepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void findUuidByLotId_returnsUuid() {
        Photo photo = new Photo();
        photo.setLotId(1L);
        photo.setUuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        photoRepository.save(photo);

        UUID result = photoRepository.findUuidByLotId(1L);

        assertThat(result).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void findUuidByLotId_returnsNull_forNonExistingLot() {
        UUID result = photoRepository.findUuidByLotId(999L);

        assertThat(result).isNull();
    }

    @Test
    void findByLotId_returnsPhoto() {
        Photo photo = new Photo();
        photo.setLotId(2L);
        photo.setUuid(UUID.randomUUID());
        photoRepository.save(photo);

        Photo result = photoRepository.findByLotId(2L);

        assertThat(result).isNotNull();
        assertThat(result.getLotId()).isEqualTo(2L);
    }

    @Test
    void findByLotId_returnsNull_forNonExistingLot() {
        Photo result = photoRepository.findByLotId(999L);

        assertThat(result).isNull();
    }

    @Test
    void deleteByLotId_removesPhoto() {
        Photo photo = new Photo();
        photo.setLotId(3L);
        photo.setUuid(UUID.randomUUID());
        photoRepository.save(photo);

        photoRepository.deleteByLotId(3L);

        Photo afterDelete = photoRepository.findByLotId(3L);
        assertThat(afterDelete).isNull();
    }
}
