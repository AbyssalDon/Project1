package com.example.demo.service;

import com.example.demo.Exceptions.CprNotValidException;
import com.example.demo.Exceptions.GlobalExceptionHandler;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
public class PDFService {
    private final UserRepository userRepository;

    public void createPDFDocument(User user) {
        if (user.getCpr().length() != 9)
            throw new CprNotValidException("Cpr has is not exactly 9 digits!");

        UUID id = userRepository.save(user).getUserId();
        LocalDateTime currentTime = LocalDateTime.now();

        try (PDDocument document = new PDDocument()) {
            PDPage my_page = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(document, my_page);

            contentStream.beginText();
            contentStream.newLineAtOffset(25, 700);
            PDFont pdfFont=  new PDType1Font(HELVETICA_BOLD);
            contentStream.setFont(pdfFont, 14 );
            contentStream.setLeading(14.5f);

            contentStream.showText("Name: " + user.getName());
            contentStream.newLine();
            contentStream.showText("CPR: " + user.getCpr());
            contentStream.endText();
            contentStream.close();

            document.addPage(my_page);
            String pdfname = id + currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".pdf";
            document.save("src/main/resources/pdf/" + pdfname);
            user.setCreatedAt(currentTime);
            user.setPdfname(pdfname);
            userRepository.save(user);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResponseEntity<byte[]> getPDF(String cpr) throws Exception {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try {
            User user = userRepository.findByCpr(cpr).orElseThrow(() -> new UserNotFoundException("User does not exist!"));
            File file = new File("src/main/resources/pdf/" + user.getPdfname());
            PDDocument pdf = Loader.loadPDF(file);
            pdf.save(arr);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(arr.toByteArray());
    }
}
