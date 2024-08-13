package com.email.modifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.email.entities.EmailWrapper;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public class CodingErrorPdfInvoiceCreator {

	private static final Logger logger = LoggerFactory.getLogger(CodingErrorPdfInvoiceCreator.class);

	public Document document;
	PdfDocument pdfDocument;
	public String pdfName;
	float threecol = 190f;
	float twocol = 285f;
	float twocol150 = twocol + 150f;
	float[] twocolumnWidth = { twocol150, twocol };
	float[] threeColumnWidth = { threecol, threecol, threecol };
	float[] fullwidth = { threecol * 3 };

	public CodingErrorPdfInvoiceCreator(String pdfName) {
		this.pdfName = pdfName;
		logger.info("Initialized CodingErrorPdfInvoiceCreator with PDF name: {}", pdfName);
	}

	public void createDocument() throws FileNotFoundException {
		logger.info("Creating document...");
		PdfWriter pdfWriter = new PdfWriter(pdfName);
		pdfDocument = new PdfDocument(pdfWriter);
		pdfDocument.setDefaultPageSize(PageSize.A4);
		logger.info("Document created with name: {}", pdfName);
		this.document = new Document(pdfDocument);
	}

	public void createTnc(List<String> tncList, Boolean lastPage) {
		logger.info("Creating terms and conditions...");
		if (Boolean.TRUE.equals(lastPage)) {
			float threecol = 190f;
			float[] fullwidth = { threecol * 3 };
			Table tb = new Table(fullwidth);
			tb.addCell(new Cell().add("TERMS AND CONDITIONS\n").setBold().setBorder(Border.NO_BORDER));
			for (String tnc : tncList) {

				tb.addCell(new Cell().add(tnc).setBorder(Border.NO_BORDER));
			}

			document.add(tb);
		} else {
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new MyFooter(document, tncList));
		}

		document.close();
		logger.info("Terms and conditions created, document closed.");
	}

	public void createAddress(EmailWrapper wrapper) {
		logger.info("Creating address section...");
		Table tableOne = new Table(twocolumnWidth);
		Cell cell = new Cell(1, 2);
		cell.add("Theater Details");
		cell.setBold().setTextAlignment(TextAlignment.CENTER);
		tableOne.addHeaderCell(cell);
		tableOne.addCell("Theater name");
		tableOne.addCell(wrapper.theaterName());
		tableOne.addCell("Theater Location");
		tableOne.addCell(wrapper.theaterLocation());
		tableOne.addCell("Contact email");
		tableOne.addCell(wrapper.theaterContact());
		document.add(tableOne);

		Table tableTwo = new Table(twocolumnWidth);
		Cell cell2 = new Cell(1, 2);
		cell2.add("Ticket Details");
		cell2.setBold().setTextAlignment(TextAlignment.CENTER);
		tableTwo.addHeaderCell(cell2);
		tableTwo.addCell("Movie Name");
		tableTwo.addCell(wrapper.movieName());
		tableTwo.addCell("Room Name");
		tableTwo.addCell(wrapper.roomName());

		tableTwo.addCell("Seat Name");
		tableTwo.addCell(wrapper.seatName());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

		tableTwo.addCell("Start Time");
		tableTwo.addCell(formatter.format(wrapper.startTime()));

		tableTwo.addCell("End Time");
		tableTwo.addCell(formatter.format(wrapper.endTime()));

		tableTwo.addCell("Movie Price");
		tableTwo.addCell("Rs." + wrapper.moviePrice());

		Paragraph paragraph = new Paragraph("\n\n");
		document.add(paragraph);
		document.add(tableTwo);

		logger.info("Address section created.");
	}

	public void createHeader(HeaderDetails header, EmailWrapper wrapper) {
		logger.info("Creating header section...");
		Table table = new Table(twocolumnWidth);
		table.addCell(new Cell().add(header.getInvoiceTitle() + ":" + wrapper.userName()).setFontSize(20f)
				.setBorder(Border.NO_BORDER).setBold());
		Table nestedtabe = new Table(new float[] { twocol / 2, twocol / 2 });
		nestedtabe.addCell(getHeaderTextCell(header.getInvoiceNoText()));
		nestedtabe.addCell(getHeaderTextCellValue(header.getInvoiceNo()));
		nestedtabe.addCell(getHeaderTextCell(header.getInvoiceDateText()));
		nestedtabe.addCell(getHeaderTextCellValue(header.getInvoiceDate()));
		table.addCell(new Cell().add(nestedtabe).setBorder(Border.NO_BORDER));
		Border gb = new SolidBorder(header.getBorderColor(), 2f);
		document.add(table);
		document.add(getNewLineParagraph());
		document.add(getDividerTable(fullwidth).setBorder(gb));
		document.add(getNewLineParagraph());
		logger.info("Header section created.");
	}

	static Table getDividerTable(float[] fullwidth) {
		return new Table(fullwidth);
	}

	static Paragraph getNewLineParagraph() {
		return new Paragraph("\n");
	}

	static Cell getHeaderTextCell(String textValue) {

		return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
	}

	static Cell getHeaderTextCellValue(String textValue) {

		return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
	}

	static Cell getBillingandShippingCell(String textValue) {

		return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT);
	}

	static Cell getCell10fLeft(String textValue, Boolean isBold) {
		Cell myCell = new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT);
		return Boolean.TRUE.equals(isBold) ? myCell.setBold() : myCell;

	}
}
