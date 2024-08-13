package com.email.modifications;


import com.itextpdf.kernel.color.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HeaderDetails {

	String invoiceTitle= Constants.INVOICE_TITLE;
    String invoiceNoText=Constants.INVOICE_NO_TEXT;
    String invoiceDateText=Constants.INVOICE_DATE_TEXT;
    String invoiceNo=Constants.EMPTY;
    String invoiceDate=Constants.EMPTY;
    Color borderColor=Color.GRAY;

    public HeaderDetails setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
        return this;
    }

    public HeaderDetails setInvoiceNoText(String invoiceNoText) {
        this.invoiceNoText = invoiceNoText;
        return this;
    }

    public HeaderDetails setInvoiceDateText(String invoiceDateText) {
        this.invoiceDateText = invoiceDateText;
        return this;
    }

    public HeaderDetails setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
        return this;
    }

    public HeaderDetails setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public HeaderDetails setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }
    public HeaderDetails build()
    {
        return  this;
    }
}

