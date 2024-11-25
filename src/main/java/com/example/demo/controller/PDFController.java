package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.PDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PDFController {
    private final PDFService pdfService;
    private final UserRepository userRepository;

    @PostMapping("fill-pdf-document")
    public void FillPDFDocument(@RequestBody User user) throws IOException {
        this.pdfService.createPDFDocument(user);
    }

    @GetMapping(value = "download-pdf")
    @ResponseBody
    public ResponseEntity<?> downloadPDF(@RequestParam String cpr) throws Exception {
        return this.pdfService.getPDF(cpr);
    }
}
