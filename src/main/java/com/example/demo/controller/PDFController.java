package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.PDFService;
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
    private final PDFService pdfService;

    @PostMapping("fill-pdf-document")
    public void FillPDFDocument(@RequestBody User user) throws IOException {
        this.pdfService.createPDFDocument(user);
    }

    @GetMapping(value = "download-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadPDF(@RequestParam Integer cpr) {
        return this.pdfService.getPDF(cpr);
    }
}
