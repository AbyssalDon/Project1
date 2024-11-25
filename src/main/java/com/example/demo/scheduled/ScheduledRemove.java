package com.example.demo.scheduled;

import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ScheduledRemove {
    private final UserRepository userRepository;

    @Value("${timer.expiry}")
    private Long expiryTime;

    @Scheduled(fixedDelayString = "${timer.removeTimer}")
    public void deleteFile() {
        String directoryPath = "src/main/resources/pdf";

        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    UUID id = UUID.fromString(file.getName().substring(0, 36));
                    User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found."));
                    LocalDateTime expiry = user.getCreatedAt().plusMinutes(this.expiryTime);
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (expiry.isEqual(currentTime) || expiry.isBefore(currentTime)) {
                        if (file.delete())
                            System.out.println("File deleted!");
                        else
                            System.out.println("File was not deleted...");
                    }
                }
            }
        }
    }

}
