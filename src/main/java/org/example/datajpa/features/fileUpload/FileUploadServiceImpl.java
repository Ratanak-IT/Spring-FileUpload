package org.example.datajpa.features.fileUpload;

import ch.qos.logback.core.util.StringUtil;
import lombok.NoArgsConstructor;
import org.example.datajpa.features.fileUpload.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class FileUploadServiceImpl implements FileUploadService{

    private final FileUploadRepository fileUploadRepository;
    private final FileUploadMapper fileUploadMapper;

    @Value("${file.storage-location}")
    private String storageLocation;

    @Value("${file.base-uri}")
    private String baseUri;
    public FileUploadServiceImpl(FileUploadRepository fileUploadRepository, FileUploadMapper fileUploadMapper) {
        this.fileUploadRepository = fileUploadRepository;
        this.fileUploadMapper = fileUploadMapper;
    }



    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
       // Prepare file information
        //file name
        String name =UUID.randomUUID().toString();

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        name+=extension;
        //create absolutePath
        try{
            Path absolutePath = Paths.get(storageLocation, name);
            Files.copy(file.getInputStream(),
                    absolutePath,
                    StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file");
        }

        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(name);
        fileUpload.setExtension(extension);
        fileUpload.setSize(file.getSize());
        fileUpload.setCaption("Harry porter");
        fileUpload.setMimeType(extension);
        fileUploadRepository.save(fileUpload);

        return FileUploadResponse.builder()
                .fileName(name)
                .size(file.getSize())
                .mimeType(file.getContentType())
                .uri(baseUri + "/" + name)
                .extension(extension)
                .build();
    }

    @Override
    public List<FileUploadResponse> uploadMultipleFiles(MultipartFile[] files) {
        //
        List<FileUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files){
            responses.add(uploadFile(file));
        }
        return responses;
    }

    //upload multiple v2 advance
    public List<FileUploadResponse> uploadMultipleFile(List<MultipartFile> files){
        //logic validate file size
        if (files.size() > 5){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 5 files allowed");
        }
        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileName) {

        FileUpload fileUpload = fileUploadRepository.findByFileName(fileName)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        fileUploadRepository.delete(fileUpload);
        Path filePath = Paths.get(storageLocation + fileUpload.getFileName() + "." + fileName);
        try{

            boolean deleted = Files.deleteIfExists(filePath);

            if (!deleted){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
        }
    }


    @Override
    public Page<FileUploadResponse> getAllFile(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageRequest= PageRequest.of(page, size, sort);
        Page<FileUpload> fileUploads = fileUploadRepository.findAll(pageRequest);
        return fileUploads.map(fileUploadMapper::mapFileUploadToFileUploadResponse);

    }

    @Override
    public FileUploadResponse findFileByName(String fileName) {
        return fileUploadRepository.findByFileName(fileName)
                .map(fileUploadMapper::mapFileUploadToFileUploadResponse)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    }
}
