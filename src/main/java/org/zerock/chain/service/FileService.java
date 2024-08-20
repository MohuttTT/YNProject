package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;  // 설정 파일의 uploadDir을 사용

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;  // 파일이 없을 경우 null 반환
        }

        // 파일 이름을 고유하게 생성
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        // 파일 경로 생성
        Path filePath = Paths.get(uploadDir, uniqueFilename);

        // 디렉토리가 존재하지 않으면 생성
        Files.createDirectories(filePath.getParent());

        // 파일 저장
        file.transferTo(filePath.toFile());

        // 파일 경로 반환
        return filePath.toString();
    }

    public String getFilePath(String fileName) {
        return Paths.get(uploadDir, fileName).toString();
    }
}
