package shop.storage;

import org.springframework.core.io.Resource;

import java.nio.file.Path;

public interface StorageService {
    void init();
    Resource loadAsResource(String filename);
    String save(String base64);
    void  removeFile(String removeFile);
    Path load(String filename);
}
