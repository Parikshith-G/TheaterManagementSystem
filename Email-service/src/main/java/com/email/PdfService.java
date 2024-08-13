package com.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.entities.EmailWrapper;
import com.email.modifications.CodingErrorPdfInvoiceCreator;
import com.email.modifications.HeaderDetails;

import jakarta.mail.MessagingException;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PdfService {

	private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

	@Autowired
	private EmailService service;

	@PostMapping("/pdf")
	public ResponseEntity<byte[]> generatePdf(@RequestBody EmailWrapper wrapper)
			throws IOException, MessagingException {
		logger.info("Received request to generate PDF for booking reference: {}", wrapper.bookingReference());

		
		String pdfName = wrapper.bookingReference() + ".pdf";
		File tempFile = File.createTempFile("temp", ".pdf");

		CodingErrorPdfInvoiceCreator cepdf = new CodingErrorPdfInvoiceCreator(tempFile.getAbsolutePath());
		cepdf.createDocument();

		// Create Header start
		HeaderDetails header = new HeaderDetails();
		header.setInvoiceNo(wrapper.bookingReference())
				.setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).build();
		cepdf.createHeader(header, wrapper);
		// Header End

		cepdf.createAddress(wrapper);

		// Term and Condition Start
		List<String> tncList = new ArrayList<>();
		tncList.add("1. The theater is not responsible for your lost valuables.");
		tncList.add("2. The ticket cannot be used after the show date");
		tncList.add("3. No refunds are entertained");
	
		cepdf.createTnc(tncList, false);
		// Term and condition end

		// Read the PDF file into a byte array
		byte[] pdfBytes;
		try (FileInputStream fis = new FileInputStream(tempFile)) {
			pdfBytes = new byte[(int) tempFile.length()];
			long bytesRead = fis.read(pdfBytes);
			logger.info("Number of bytes read are: {}", bytesRead);
		}

		// Delete the temporary file
		boolean isDeleted = tempFile.delete();
		if (isDeleted) {
			logger.info("Temp file deleted successfully: {}", true);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", pdfName);
		headers.setContentLength(pdfBytes.length);
		logger.info("PDF generated successfully for booking reference: {}", wrapper.bookingReference());
		logger.info("Sending email to: {}", wrapper.userMail());
		service.sendEmailWithPdfAttachment(wrapper.userMail(), wrapper.theaterContact(), "Movie ticket",
				"This is your movie ticket", pdfBytes, pdfName);

		logger.info("Email sent successfully to: {}", wrapper.userMail());

		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}
}
