package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletContextResource;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PDFController {
    private final UserRepository userRepository;
    private final ServletContext servletContext;

    @PostMapping("fill-pdf-document")
    public void FillPDFDocument(@RequestBody User user) throws IOException {
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
            document.save("src/main/resources/" + pdfname);
            user.setCreatedAt(currentTime);
            user.setPdfname(pdfname);
            userRepository.save(user);
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }

    @GetMapping(value = "download-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadPDF(@RequestParam Integer cpr) {
        User user = userRepository.findByCpr(cpr).orElseThrow(() -> new IllegalStateException("No user found"));
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
//        Resource pdfFile = new ClassPathResource(this.getPath(user));
        try {
            File file = new File("src/main/resources/" + user.getPdfname());
            PDDocument pdf = Loader.loadPDF(file);
            pdf.save(arr);
        } catch (IOException e) {
            System.out.println("File could not be loaded");
        }
        ;



        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(arr.toByteArray());
    }

    private String getPath(User user) {
        return user.getPdfname();
    }
}
