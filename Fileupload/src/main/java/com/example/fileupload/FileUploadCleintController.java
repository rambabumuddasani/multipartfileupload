package com.example.fileupload;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadCleintController {

        @GetMapping("/upload")
        public ResponseEntity<byte[]> uploadFile(){
                try {
                        byte[] contents = uploadSingleFile().getBody();

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        // Here you have to set the actual filename of your pdf
                        String filename = "output.pdf";
                        headers.setContentDispositionFormData(filename, filename);
                        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
                        return response;


                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
        }

        @PostMapping("/stub/multipart")
        public ResponseEntity<String> uploadFile(MultipartFile file) throws IOException {
                String result = new String(file.getBytes());
                //File f = Files.write(Paths.get("/"))
                System.out.println("Result is "+result);
                return new ResponseEntity<>(result, HttpStatus.OK);
        }

        private static ResponseEntity<byte[]>  uploadSingleFile() throws IOException {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                //headers.add("htmlString");
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("htmlString", getTestFile());

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                //String serverUrl = "http://localhost:9595/stub/multipart";
                String serverUrl = "http://localhost:8080/pdfsvc/pdfRequest/convert";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<byte[]> response = restTemplate.postForEntity(serverUrl, requestEntity,byte[].class);
                return ResponseEntity.ok(response.getBody());
               // String pdfResponse = new String(response.getBody());
               // System.out.println("Response code: " + response.getStatusCode()+" PDF Response "+pdfResponse);
        }


        public static Resource getTestFile() throws IOException {
                Path testFile = Files.createTempFile("test-file", ".html");
                System.out.println("Creating and Uploading Test File: " + testFile);
                Files.write(testFile, "<html><head> THis is my HEAD </head> <body> Hello World !!, This is a test file.</body></html>".getBytes());
                return new FileSystemResource(testFile.toFile());
        }


}
