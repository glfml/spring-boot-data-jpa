package com.meli.datajpa.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class UploadFileServiceImpl implements IUploadFileService {


    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String UPLOADS_FOLDER = "uploads";

    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path path = this.getPath(filename);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("No se puede cargar el archivo");
        }

        return resource;
    }

    @Override
    public String copy(MultipartFile file) throws IOException {
        Path rootPath = this.getPath(file.getOriginalFilename());
        Files.copy(file.getInputStream(), rootPath, StandardCopyOption.REPLACE_EXISTING);

        return file.getOriginalFilename();
    }

    @Override
    public boolean delete(String filename) {
        Path path = getPath(filename);
        File file = path.toFile();
        if (file.exists() && file.canRead()) {
            if (file.delete()) {
                return true;
            }
        }

        return false;
    }

    private Path getPath(String filename) {
        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
    }

    @Override
    public void init() throws IOException {
        Files.createDirectory(Paths.get(UPLOADS_FOLDER));
    }
}
