package com.email.services;


import com.codingerror.model.Product;
import com.email.entities.EmailWrapper;
import com.email.modifications.CodingErrorPdfInvoiceCreator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CodingErrorPdfInvoiceCreatorTest {


    @Mock
    private PdfWriter pdfWriter;

    @Mock
    private PdfDocument pdfDocument;

    @Mock
    private Document document;

    @InjectMocks
    private CodingErrorPdfInvoiceCreator codingErrorPdfInvoiceCreator;

    private List<Product> productList;
    private EmailWrapper emailWrapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        codingErrorPdfInvoiceCreator = new CodingErrorPdfInvoiceCreator("test.pdf");

        productList = new ArrayList<>();
        productList.add(new Product("Product1", 2, 10.0f));
        productList.add(new Product("Product2", 3, 15.0f));

        emailWrapper = new EmailWrapper(
                "Theater1", "Location1", "Room1", "contact@theater.com",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "User1", "Movie1", "Seat1", "BookingRef123", "user@example.com", 150.0
        );
    }

    @Test
    void testCreateDocument() throws FileNotFoundException {
        codingErrorPdfInvoiceCreator.createDocument();
        assertEquals("test.pdf", codingErrorPdfInvoiceCreator.pdfName);
        // Further assertions or verifications can be added here
    }

    @Test
    void testCreateTnc() {
        List<String> tncList = new ArrayList<>();
        tncList.add("Term1");
        tncList.add("Term2");

        codingErrorPdfInvoiceCreator.document = mock(Document.class);
        codingErrorPdfInvoiceCreator.createTnc(tncList, true);

        verify(codingErrorPdfInvoiceCreator.document, times(1)).add(any(Table.class));
    }

    @Test
    void testCreateAddress() {
        codingErrorPdfInvoiceCreator.document = mock(Document.class);

        codingErrorPdfInvoiceCreator.createAddress(emailWrapper);

        verify(codingErrorPdfInvoiceCreator.document, times(2)).add(any(Table.class));
    }

  
}