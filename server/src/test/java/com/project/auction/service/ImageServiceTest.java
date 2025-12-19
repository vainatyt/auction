package com.project.auction.service;

import com.project.auction.models.Photo;
import com.project.auction.repository.PhotoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private ImageService imageService;

    @Test
void savePhoto_validImage_returnsSavedPhoto() throws Exception {
    // КРИТИЧЕСКИ ВАЖНО: установить uploadDir ПРЯМО В ТЕСТЕ
    ReflectionTestUtils.setField(imageService, "uploadDir", "target/test-uploads/");
    
    // given
    byte[] imageContent = "test image data".getBytes();
    MockMultipartFile image = new MockMultipartFile(
        "image", "test.jpg", "image/jpeg", imageContent
    );
    Long lotId = 1L;

    Photo savedPhoto = new Photo();
    savedPhoto.setLotId(lotId);
    savedPhoto.setUuid(UUID.randomUUID());

    when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

    // when
    Photo result = imageService.savePhoto(lotId, image);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getLotId()).isEqualTo(lotId);
    verify(photoRepository).save(any(Photo.class));
}

    @Test
    void savePhoto_nullImage_returnsNull() {
        Photo result = imageService.savePhoto(1L, null);
        assertThat(result).isNull();
        verifyNoInteractions(photoRepository);
    }

    @Test
    void savePhoto_emptyImage_returnsNull() {
        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);

        Photo result = imageService.savePhoto(1L, emptyImage);
        assertThat(result).isNull();
        verifyNoInteractions(photoRepository);
    }

    @Test
    void savePhoto_tooLargeImage_throwsException() {
        MockMultipartFile largeImage = new MockMultipartFile(
            "image", "large.jpg", "image/jpeg", 
            new byte[6 * 1024 * 1024] // 6MB > 5MB limit
        );

        assertThatThrownBy(() -> imageService.savePhoto(1L, largeImage))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Image size exceeds 5MB");
    }

    @Test
    void savePhoto_invalidFormat_throwsException() {
        byte[] imageContent = "test image data".getBytes();
        MockMultipartFile invalidImage = new MockMultipartFile("image", "test.txt", "text/plain", imageContent);


        assertThatThrownBy(() -> imageService.savePhoto(1L, invalidImage))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid image format");
    }

    @Test
    void saveImage_validImage_returnsUUID() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile(
            "image", "test.png", "image/png", "data".getBytes()
        );
        
        // Устанавливаем uploadDir для теста
        ReflectionTestUtils.setField(imageService, "uploadDir", "target/test-uploads/");

        // when
        UUID result = imageService.saveImage(image);

        // then
        assertThat(result).isNotNull();
        
        // Проверяем, что файл создался
        Path filePath = Path.of("target/test-uploads/" + result + ".png");
        assertThat(Files.exists(filePath)).isTrue();
        Files.deleteIfExists(filePath); // cleanup
    }

    @Test
    void getImage_existingFile_returnsResource() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        ReflectionTestUtils.setField(imageService, "uploadDir", "target/test-images/");
        
        Path testFile = Path.of("target/test-images/" + uuid + ".jpg");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test image data".getBytes());

        // when
        ResponseEntity<Resource> result = imageService.getImage(uuid, "image/jpeg");

        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isInstanceOf(UrlResource.class);
        
        Files.deleteIfExists(testFile);
    }

    @Test
    void delete_validPhoto_deletesFileAndDbRecord() throws Exception {
        // given
        Photo photo = new Photo();
        photo.setLotId(1L);
        
        UUID uuid = UUID.randomUUID();
        when(photoRepository.findUuidByLotId(1L)).thenReturn(uuid);
        
        ReflectionTestUtils.setField(imageService, "uploadDir", "target/test-delete/");
        Path testFile = Path.of("target/test-delete/" + uuid + ".jpg");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test".getBytes());

        // when
        imageService.delete(photo);

        // then
        verify(photoRepository).deleteByLotId(1L);
        assertThat(Files.exists(testFile)).isFalse();
    }
}
